import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

class EspaceJeu extends JPanel implements Runnable, MouseListener, MouseMotionListener, KeyListener {

  private final int DELAI=16;

  private final int MENU=0;
  private final int ATTEND=1;
  private final int ROULE=2;
  private final int SORT=3;
  private final int GAGNE=4;
  private final int PAUSE=6; 

  private final int SIMPLE=0;
  private final int NORME=1;
  private final int RAPIDE=2;
  private final int DEDOUBLE=3;
  private final int RETRECIT=4;
  private final int AGRANDIT=5;
  
  private int vies;
  private int niveauActuel;
  private int score;
  private int chronoBonus; // Compteur pour les 30 secondes d'effet

  private Thread action;
  private boolean fini;
  private int phase;
  private int phaseAvantPause; 
  private int delai;
  private Barre barre;
  private Boule boule;
  private Mur mur;
  
  private ArrayList<Bonus> listeBonus;
  private ArrayList<Boule> boulesExtra;

  public EspaceJeu() {
    barre=new Barre();
    boule=new Boule();
    listeBonus = new ArrayList<>();
    boulesExtra = new ArrayList<>();
    delai=DELAI;
    phase=MENU;

    addMouseMotionListener(this);
    addMouseListener(this);
    addKeyListener(this); 
    setFocusable(true);   
  }

  public void initialiseNiveau() {
    vies = 3;
    score = 0;
    chronoBonus = 0;
    barre.setMiLargeur(25);
    niveauActuel = 1;
    listeBonus.clear(); 
    boulesExtra.clear();

    fini=true;
    if(action != null) {
      while(action.isAlive());
    }

    if (mur==null) {
      mur=new Mur();
    }
    mur.construit(niveauActuel);
    phase= ATTEND;
    delai = DELAI;
    requestFocusInWindow(); 

    action = new Thread(this);
    action.start();
  }

  public void run() {
    fini=false;
    while (!fini) {
      switch (phase) {
        case ATTEND:
          boule.place(barre.getX(), barre.getY() - boule.getRayon());
          break;

        case PAUSE:
          break;

        case ROULE:
          boule.deplace();

          // --- GESTION DU CHRONO DES BONUS TEMPORAIRES (30 SECONDES) ---
          if (chronoBonus > 0) {
              chronoBonus--;
              if (chronoBonus <= 0) {
                  modifJeu(NORME); // Annule l'effet une fois à 0
              }
          }

          for (int i = 0; i < listeBonus.size(); i++) {
              Bonus bonus = listeBonus.get(i);
              bonus.deplace();

              if (bonus.getY() + bonus.getRayon() >= barre.getY() &&
                  bonus.getY() - bonus.getRayon() <= barre.getY() + barre.getHauteur() &&
                  bonus.getX() + bonus.getRayon() >= barre.getX() - barre.getMiLargeur() &&
                  bonus.getX() - bonus.getRayon() <= barre.getX() + barre.getMiLargeur()) {
                  
                  modifJeu(bonus.getType()); 
                  score += 500;
                  listeBonus.remove(i);
                  i--; 
              } 
              else if (bonus.getY() > 450) {
                  listeBonus.remove(i);
                  i--;
              }
          }

          for (int i = 0; i < boulesExtra.size(); i++) {
              Boule bEx = boulesExtra.get(i);
              bEx.deplace();
              
              if (bEx.getX() < bEx.getRayon()) { bEx.chocH(); bEx.place(bEx.getRayon(), bEx.getY()); }
              else if (bEx.getX() > getSize().width - bEx.getRayon()) { bEx.chocH(); bEx.place(getSize().width - bEx.getRayon(), bEx.getY()); }
              
              if (bEx.getY() < bEx.getRayon()) { bEx.chocV(); bEx.place(bEx.getX(), bEx.getRayon()); }
              else {
                  if (bEx.getY() > 310 - bEx.getRayon()) {
                      if ((bEx.getX() - bEx.getRayon() < barre.getX() + barre.getMiLargeur()) && (bEx.getX() + bEx.getRayon() > barre.getX() - barre.getMiLargeur())) {
                          rebondSurBarre(bEx, bEx.getX() - barre.getX());
                          bEx.place(bEx.getX(), 310 - bEx.getRayon());
                      }
                      else {
                          if (bEx.getY() > 310 + barre.getHauteur() + bEx.getRayon()) {
                              boulesExtra.remove(i);
                              i--;
                              continue;
                          }
                      }
                  }
              }
              gereCollisionBrique(bEx);
          }

          if (boule.getX() < boule.getRayon()) { boule.chocH(); boule.place(boule.getRayon(), boule.getY()); }
          else {
            if (boule.getX() > getSize().width - boule.getRayon()) { boule.chocH(); boule.place(getSize().width - boule.getRayon(), boule.getY()); }
          }

          if (boule.getY() < boule.getRayon()) { boule.chocV(); boule.place(boule.getX(), boule.getRayon()); }
          else {
            if (boule.getY() > 310 - boule.getRayon()) {
              if ((boule.getX() - boule.getRayon() < barre.getX() + barre.getMiLargeur()) && (boule.getX() + boule.getRayon() > barre.getX() - barre.getMiLargeur())) {
                rebondSurBarre(boule, boule.getX() - barre.getX());
                boule.place(boule.getX(), 310-boule.getRayon());
              }
              else {
                if (boule.getY() > 310 + barre.getHauteur() - boule.getRayon()) {
                    if (boulesExtra.isEmpty()) { phase = SORT; } 
                    else { boule = boulesExtra.remove(0); }
                }                
              }
            }
          }

          int hauteur = mur.getHauteurBrique();
          int largeur = mur.getLargeurBrique();

          if (boule.getY()-boule.getRayon()<10*(hauteur+1)){
            int l1, l2, c1, c2;
            l1=(int)((boule.getY()-boule.getRayon())/(hauteur+1));
            l2=(int)((boule.getY()+boule.getRayon())/(hauteur+1));
            c1=(int)((boule.getX()-boule.getRayon())/(largeur+1));
            c2=(int)((boule.getX()+boule.getRayon())/(largeur+1));

            if (mur.percute(l1,c1)) {
              if (mur.percute(l1,c2)) { boule.chocV(); }
              else {
                if (mur.percute(l2,c1)) { boule.chocH(); }
                else { boule.chocV(); boule.chocH(); }
              }
            }
            else {
              if (mur.percute(l1,c2)) {
                if (mur.percute(l2,c2)) { boule.chocH(); }
                else { boule.chocV(); boule.chocH(); }
              }
              else {
                if (mur.percute(l2,c1)) {
                  if (mur.percute(l2,c2)) { boule.chocV(); }
                  else { boule.chocV(); boule.chocH(); }
                }
                else {
                  if (mur.percute(l2,c2)) { boule.chocV(); boule.chocH(); }
                }
              }
            }
            
            casserBrique(l1, c1);
            casserBrique(l1, c2);
            casserBrique(l2, c1);
            casserBrique(l2, c2);

            if (mur.getNbBriques() == 0) { phase = GAGNE; }
          }
          break;

        case SORT :
            vies--;
            if (vies > 0) {
                JOptionPane.showMessageDialog(this,
                    "<html><h2 style='color: #E53935; text-align: center; margin-top: 5px;'>Balle perdue !</h2>" +
                    "<p style='text-align: center; font-size: 14px;'>Vies restantes : <b>" + vies + "</b></p>" +
                    "<p style='text-align: center; font-size: 14px;'>Score actuel : <b>" + score + "</b> points</p></html>",
                    "Casse briques", JOptionPane.PLAIN_MESSAGE);
                boulesExtra.clear();
                listeBonus.clear(); 
                chronoBonus = 0; // Remise à zéro
                phase = ATTEND;
            } else {
                JOptionPane.showMessageDialog(this,
                    "<html><h1 style='color: #B71C1C; text-align: center;'>GAME OVER</h1>" +
                    "<p style='text-align: center; font-size: 14px;'>Score final : <b>" + score + "</b></p>" +
                    "<p style='text-align: center; font-size: 14px;'>Retour au menu principal.</p></html>",
                    "Casse briques", JOptionPane.PLAIN_MESSAGE);
                barre.setMiLargeur(25);
                boulesExtra.clear();
                chronoBonus = 0;
                phase = MENU;
                fini = true; 
            }
            break;

        case GAGNE :
            if (niveauActuel >= 3) {
                JOptionPane.showMessageDialog(this, 
                    "<html><h1 style='color: #43A047; text-align: center;'>Félicitations !</h1>" +
                    "<p style='text-align: center; font-size: 14px;'>Vous avez terminé le jeu avec <b>" + score + "</b> points !</p></html>", 
                    "Victoire !", JOptionPane.PLAIN_MESSAGE);
                phase = MENU;
                fini = true;
            } else {
                JOptionPane.showMessageDialog(this, 
                    "<html><h2 style='color: #1E88E5; text-align: center;'>Niveau " + niveauActuel + " terminé !</h2>" +
                    "<p style='text-align: center; font-size: 14px;'>Score actuel : <b>" + score + "</b> points</p>" +
                    "<p style='text-align: center; font-size: 14px;'>Préparez-vous pour le niveau " + (niveauActuel + 1) + "</p></html>", 
                    "Niveau Complété", JOptionPane.PLAIN_MESSAGE);
                niveauActuel++;
                boulesExtra.clear();
                listeBonus.clear();
                barre.setMiLargeur(25);
                chronoBonus = 0;
                mur.construit(niveauActuel);
                phase = ATTEND;
                delai = DELAI;
            }
            break;
      }

      repaint();

      try {
        Thread.sleep(delai);
      } catch (InterruptedException e) {}
    }
  }

  private void casserBrique(int l, int c) {
      int briquesAvant = mur.getNbBriques();
      int action = mur.casse(l, c); 

      if (briquesAvant > mur.getNbBriques()) {
          score += 100;

          if (action == 6) {
              int typeBonus = (int)(Math.random() * 5) + 1; 
              int briqueX = c * (mur.getLargeurBrique() + 1) + (mur.getLargeurBrique() / 2);
              int briqueY = l * (mur.getHauteurBrique() + 1) + (mur.getHauteurBrique() / 2);
              listeBonus.add(new Bonus(briqueX, briqueY, typeBonus));
          } 
          else if (action > 0 && action < 6) {
              modifJeu(action);
          }
      }
  }

  private void gereCollisionBrique(Boule b) {
    int hauteur = mur.getHauteurBrique();
    int largeur = mur.getLargeurBrique();

    if (b.getY()-b.getRayon()<10*(hauteur+1)) {
      int l1, l2, c1, c2;
      l1=(int)((b.getY()-b.getRayon())/(hauteur+1));
      l2=(int)((b.getY()+b.getRayon())/(hauteur+1));
      c1=(int)((b.getX()-b.getRayon())/(largeur+1));
      c2=(int)((b.getX()+b.getRayon())/(largeur+1));

      if (mur.percute(l1,c1)) {
        if (mur.percute(l1,c2)) { b.chocV(); }
        else {
          if (mur.percute(l2,c1)) { b.chocH(); }
          else { b.chocV(); b.chocH(); }
        }
      }
      else {
        if (mur.percute(l1,c2)) {
          if (mur.percute(l2,c2)) { b.chocH(); }
          else { b.chocV(); b.chocH(); }
        }
        else {
          if (mur.percute(l2,c1)) {
            if (mur.percute(l2,c2)) { b.chocV(); }
            else { b.chocV(); b.chocH(); }
          }
          else {
            if (mur.percute(l2,c2)) { b.chocV(); b.chocH(); }
          }
        }
      }

      casserBrique(l1, c1);
      casserBrique(l1, c2);
      casserBrique(l2, c1);
      casserBrique(l2, c2);

      if (mur.getNbBriques() == 0) { phase = GAGNE; }
    }
  }

  void rebondSurBarre(Boule b, int impact) {
    b.chocV();
    if (impact<-(barre.getMiLargeur()*0.6)) b.modifAngle(30);
    else if (impact<-(barre.getMiLargeur()*0.2)) b.modifAngle(15);
    if (impact>(barre.getMiLargeur()*0.6)) b.modifAngle(-30);
    else if (impact>(barre.getMiLargeur()*0.2)) b.modifAngle(-15);
  }

  public void modifJeu(int action) {
    switch (action) {
      case NORME : 
          delai=DELAI; 
          barre.setMiLargeur(25); 
          chronoBonus = 0; 
          break;
      case RAPIDE : 
          delai=(int)(DELAI * 0.75); 
          chronoBonus = 10000 / DELAI; // 10 secondes
          break;
      case DEDOUBLE :
          if (boulesExtra.size() < 4) {
              Boule nouvelleBoule = new Boule();
              nouvelleBoule.place(boule.getX(), boule.getY());
              nouvelleBoule.copieMouvement(boule);
              boulesExtra.add(nouvelleBoule);
          }
          break;
      case RETRECIT : 
          barre.setMiLargeur(15); 
          chronoBonus = 10000 / DELAI; // 10 secondes
          break;
      case AGRANDIT : 
          barre.setMiLargeur(35); 
          chronoBonus = 10000 / DELAI; // 10 secondes
          break;
    }
  }

  void lanceBoule(int angle) {
    if (phase==ATTEND) { phase=ROULE; boule.angleDep(angle); }
  }

  private void dessinerMenu(Graphics2D g) {
    g.setColor(new Color(30, 30, 30));
    g.fillRect(0, 0, getSize().width, getSize().height);
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 32));
    g.drawString("CASSE BRIQUES", 45, 120);
    
    g.setColor(new Color(67, 160, 71));
    g.fillRect(110, 200, 150, 40);
    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 16));
    g.drawString("JOUER", 155, 226);
    
    g.setColor(new Color(229, 57, 53));
    g.fillRect(110, 260, 150, 40);
    g.setColor(Color.WHITE);
    g.drawString("QUITTER", 146, 286);
  }

  public void paintComponent(Graphics comp) {
    Graphics2D comp2D = (Graphics2D)comp;
    comp2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    if (phase == MENU) {
        dessinerMenu(comp2D);
    } else {
        comp2D.setColor(getBackground());
        comp2D.fillRect(0,0,getSize().width,getSize().height);

        barre.dessine(comp2D);
        boule.dessine(comp2D);
        
        for (Boule bEx : boulesExtra) {
            bEx.dessine(comp2D);
        }
        
        if (mur != null) mur.dessine(comp2D);

        for (Bonus b : listeBonus) {
            b.dessine(comp2D);
        }

        comp2D.setFont(new Font("Arial", Font.BOLD, 14));
        comp2D.setColor(Color.BLACK);
        comp2D.drawString("Niveau : " + niveauActuel, 15, 360);
        comp2D.drawString("Score : " + score, 110, 360);
        comp2D.drawString("Vies : ", 220, 360);
        comp2D.setColor(Color.RED);
        for (int i = 0; i < vies; i++) {
            comp2D.fillOval(265 + (i * 15), 350, 10, 10);
        }

        // --- AFFICHAGE DU CHRONOMETRE SOUS LA RAQUETTE ---
        if (chronoBonus > 0) {
            comp2D.setColor(new Color(255, 140, 0)); 
            int secondesRestantes = (chronoBonus * DELAI) / 1000;
            // barre.getX() - 70 permet de centrer le texte par rapport à la barre
            // barre.getY() + 25 le place juste en dessous
            comp2D.drawString("Reset Effets dans : " + secondesRestantes + "s", barre.getX() - 70, barre.getY() + 25);
        }

        if (phase == PAUSE) {
            comp2D.setColor(new Color(0, 0, 0, 150)); 
            comp2D.fillRect(0, 0, getSize().width, getSize().height);
            
            comp2D.setColor(Color.WHITE);
            comp2D.setFont(new Font("Arial", Font.BOLD, 40));
            comp2D.drawString("PAUSE", 115, 140);
            
            comp2D.setColor(new Color(67, 160, 71));
            comp2D.fillRect(110, 200, 150, 40);
            comp2D.setColor(Color.WHITE);
            comp2D.setFont(new Font("Arial", Font.BOLD, 16));
            comp2D.drawString("REPRENDRE", 132, 226);
            
            comp2D.setColor(new Color(229, 57, 53));
            comp2D.fillRect(110, 260, 150, 40);
            comp2D.setColor(Color.WHITE);
            comp2D.drawString("QUITTER", 146, 286);
        }
    }
  }

  public void mouseMoved(MouseEvent evt) {
    if (phase != PAUSE) { 
        if (evt.getX()<barre.getMiLargeur()) barre.setX(barre.getMiLargeur());
        else if (evt.getX()>getSize().width-barre.getMiLargeur()) barre.setX(getSize().width-barre.getMiLargeur());
        else barre.setX(evt.getX());
    }
  }

  public void mouseDragged(MouseEvent evt) {}

  public void mouseClicked(MouseEvent evt) {
    requestFocusInWindow(); 

    int mx = evt.getX();
    int my = evt.getY();

    if (phase == MENU) {
        if (mx >= 110 && mx <= 260 && my >= 200 && my <= 240) initialiseNiveau(); 
        if (mx >= 110 && mx <= 260 && my >= 260 && my <= 300) System.exit(0);
    } 
    else if (phase == PAUSE) {
        if (mx >= 110 && mx <= 260 && my >= 200 && my <= 240) {
            phase = phaseAvantPause;
        }
        if (mx >= 110 && mx <= 260 && my >= 260 && my <= 300) {
            boulesExtra.clear();
            listeBonus.clear();
            chronoBonus = 0;
            phase = MENU;
            fini = true; 
        }
    }
    else if (phase == ATTEND) {
        lanceBoule((int)(Math.random()*120)+30);
    }
  }

  public void mouseEntered(MouseEvent evt) {}
  public void mouseExited(MouseEvent evt) {}
  public void mousePressed(MouseEvent evt) {}
  public void mouseReleased(MouseEvent evt) {}

  public void keyPressed(KeyEvent evt) {
    if (evt.getKeyCode() == KeyEvent.VK_P || evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
        if (phase == ROULE || phase == ATTEND) {
            phaseAvantPause = phase; 
            phase = PAUSE; 
        } else if (phase == PAUSE) {
            phase = phaseAvantPause; 
        }
    }
  }

  public void keyReleased(KeyEvent evt) {}
  public void keyTyped(KeyEvent evt) {}
}