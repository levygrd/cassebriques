//package cassebriques;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class EspaceJeu extends JPanel implements Runnable, MouseListener,
                                          MouseMotionListener {

  // Delai entre 2 déplacements
  private final int DELAI=16;

  // Constantes rattachées aux phases de jeu
  private final int ATTEND=1;
  private final int ROULE=2;
  private final int SORT=3;
  private final int GAGNE=4;

  // Constantes rattachées aux types de briques
  private final int SIMPLE=0;
  private final int NORME=1;
  private final int RAPIDE=2;
  private final int DEDOUBLE=3;
  private final int RETRECIT=4;
  private final int AGRANDIT=5;
  
  // Niveau de difficulté du jeu
  private int level;

  // vies
  private int vies;
  // niveaux²
  private int niveauActuel;

  // Champs d'instance
  private Thread action;
  private boolean fini;
  private int phase;
  private int delai;
  private Barre barre;
  private Boule boule;
  private Boule boule2;
  private Mur mur;


  public EspaceJeu() {

    // Création de la barre
    barre=new Barre();
    // Création de la boule
    boule=new Boule();
    boule2=null;

    // Délai entre 2 déplacements
    delai=DELAI;

    // phase d'attente
    phase=ATTEND;

    // Gestion des évenement liés à la souris
      addMouseMotionListener(this);
      addMouseListener(this);
  }

  public void initialiseNiveau() {
      level = 1; // Niveau de difficulté par défaut
      vies = 3;
      barre.setMiLargeur(25);
      boule2=null;
      niveauActuel = 1;

    // Arrêt du thread action s'il est en cours d'exécution.
    fini=true;
    if(action != null) {
      while(action.isAlive());
    }

    // Création du mur de brique
    if (mur==null) {
      mur=new Mur();
    }
    mur.construit(niveauActuel);
    // Premiére phase du jeu
    phase= ATTEND;
    delai = DELAI;

    // Lancement de l'exécution du jeu dans un thread
    action = new Thread(this);
    action.start();
  }

  public void niveauSuivant() {
    level++;
    mur.construit(level);
    phase= ATTEND;
    delai= DELAI;
  }

  // Traitement central exécuté avec une périodicité précise
  public void run() {
    fini=false;
    while (!fini) {
      // on redessine l'espace de jeu
      repaint();

      try {
        Thread.sleep(delai);
      } catch (InterruptedException e) {}

      // Selon la phase du jeu ...
      switch (phase) {
        // Attente de lancement de la boule
        case ATTEND:
          // Placement de la boule au milieu de la barre
          boule.place(barre.getX(), barre.getY() - boule.getRayon());
          break;

          // La boule roule
        case ROULE:
          // Déplacement de la boule
          boule.deplace();

          if (boule2 != null) {
            boule2.deplace();

            // Rebond sur les côtés
            if (boule2.getX() < boule2.getRayon()) {
              boule2.chocH();
              boule2.place(boule2.getRayon(), boule2.getY());
            }

            if (boule2.getX() > getSize().width - boule2.getRayon()) {
              boule2.chocH();
              boule2.place(getSize().width - boule2.getRayon(), boule2.getY());
            }

            // Rebond sur le haut
            if (boule2.getY() < boule2.getRayon()) {
              boule2.chocV();
              boule2.place(boule2.getX(), boule2.getRayon());
            }

            // Rebond sur la plateforme
            if (boule2.getY() + boule2.getRayon() >= barre.getY()
                && boule2.getY() - boule2.getRayon() <= barre.getY() + barre.getHauteur()
                && boule2.getX() + boule2.getRayon() >= barre.getX() - barre.getMiLargeur()
                && boule2.getX() - boule2.getRayon() <= barre.getX() + barre.getMiLargeur()) {

              boule2.chocV();
              boule2.place(
                  boule2.getX(),
                  barre.getY() - boule2.getRayon()
              );
            }

            // Si la deuxième boule sort par le bas
            if (boule2.getY() > 310 + barre.getHauteur() + boule2.getRayon()) {
             boule2 = null;
}

            // Gestion du choc de la deuxième boule avec une brique
            if (boule2 != null) {
              gereCollisionBrique(boule2);
            }
          }

          // Rebond sur le bord gauche ?
          if (boule.getX() < boule.getRayon()) {
            boule.chocH();
            boule.place(boule.getRayon(), boule.getY());
          }
          else {
            // Rebond sur le bord droit ?
            if (boule.getX() > getSize().width - boule.getRayon()) {
              boule.chocH();
              boule.place(getSize().width - boule.getRayon(), boule.getY());
            }
          }

          // Rebond sur le haut ?
          if (boule.getY() < boule.getRayon()) {
            boule.chocV();
            boule.place(boule.getX(), boule.getRayon());
          }
          else {
            // Rebond (ou non) sur la barre ?
            if (boule.getY() > 310 - boule.getRayon()) {
              if ((boule.getX() - boule.getRayon() < barre.getX() + barre.getMiLargeur())
              &&
                (boule.getX() + boule.getRayon() > barre.getX() - barre.getMiLargeur())) {
                // Rebond sur la barre
                // Le rebond dépend de la zone de la barre touchée
                rebondSurBarre(boule.getX() - barre.getX());

                boule.place(boule.getX(), 310-boule.getRayon());
              }
              else {
                // Si la boule touche le fond ...
                if (boule.getY() > 310 + barre.getHauteur() - boule.getRayon()) {
                    if (boule2 == null) {
                       phase = SORT;                
                    } else {
                        boule = boule2;
                        boule2 = null;
                    }
                }                
              }
            }

          }

          // Gestion du choc avec une brique
          // Récupération de la hauteur d'une brique
          int hauteur = mur.getHauteurBrique();
          // Récupération de la largeur d'une brique
          int largeur = mur.getLargeurBrique();
          // Si la boule se trouve dans la zone du mur de briques ...
          if (boule.getY()-boule.getRayon()<10*(hauteur+1)){
            // l1, c1 sont les coordonnées du coin supérieur gauche de la boule
            // l2, c2 sont les coordonnées du coin inférieur droit de la boule
            int l1, l2, c1, c2;
            l1=(int)((boule.getY()-boule.getRayon())/(hauteur+1));
            l2=(int)((boule.getY()+boule.getRayon())/(hauteur+1));
            c1=(int)((boule.getX()-boule.getRayon())/(largeur+1));
            c2=(int)((boule.getX()+boule.getRayon())/(largeur+1));

            // Le rebond dépend des coins (1 ou 2) en contact avec une brique
            // Coin supérieur gauche ...
            if (mur.percute(l1,c1)) {
              // et coin supérieur droit
              if (mur.percute(l1,c2)) {
                // Choc vertical
                boule.chocV();
              }
              else {
                // et coin inférieur gauche
                if (mur.percute(l2,c1)) {
                  // Choc horizontal
                  boule.chocH();
                }
                else {
                  // Double choc
                  boule.chocV();
                  boule.chocH();
                }
              }
            }
            else {
              // Coin supérieur droit ...
              if (mur.percute(l1,c2)) {
                // et coin inférieur droit
                if (mur.percute(l2,c2)) {
                  // Choc horizontal
                  boule.chocH();
                }
                else {
                  // Double choc
                  boule.chocV();
                  boule.chocH();
                }
              }
              else {
                // Coin inférieur gauche ...
                if (mur.percute(l2,c1)) {
                  // et coin inférieur droit
                  if (mur.percute(l2,c2)) {
                    // Choc vertical
                    boule.chocV();
                  }
                  else {
                    // Double choc
                    boule.chocV();
                    boule.chocH();
                  }
                }
                else {
                  // Coin inférieur droit
                  if (mur.percute(l2,c2)) {
                    // Double choc
                    boule.chocV();
                    boule.chocH();
                  }
                }
              }
            }
            // Casse effective des brique du mur
            //(et mise en place des conséquences)
            modifJeu(mur.casse(l1,c1));
            modifJeu(mur.casse(l1,c2));
            modifJeu(mur.casse(l2,c1));
            modifJeu(mur.casse(l2,c2));

            // Si toutes les briques sont cassées ...
            // Si toutes les briques sont cassées ...
if (mur.getNbBriques() == 0) {
    niveauActuel++;
    
    if (niveauActuel > 3) {
        // Le joueur a fini tous les niveaux
        phase = GAGNE;
    } else {
        // Transition vers le niveau suivant
        mur.construit(niveauActuel);
        phase = ATTEND;
    }
}

        // Fin de la gestion des collisions avec les briques
        }
          
          break;

      case SORT :
            vies--;
            if (vies > 0) {
                // Pop-up "Balle perdue" modernisée
                JOptionPane.showMessageDialog(
                    this,
                    "<html><h2 style='color: #E53935; text-align: center; margin-top: 5px;'>Balle perdue !</h2>" +
                    "<p style='text-align: center; font-size: 14px;'>Vies restantes : <b>" + vies + "</b></p></html>",
                    "Casse briques",
                    JOptionPane.PLAIN_MESSAGE
                );
                boule2 = null;
                phase = ATTEND;
            } else {
                barre.setMiLargeur(25);
                // Pop-up "Game Over" modernisée
                int choix = JOptionPane.showConfirmDialog(
                    getTopLevelAncestor(),
                    "<html><h1 style='color: #B71C1C; text-align: center;'>GAME OVER</h1>" +
                    "<p style='text-align: center; font-size: 14px;'>Voulez-vous relancer une partie ?</p></html>",
                    "Casse briques",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE
                );

                if (choix == JOptionPane.YES_OPTION) {
                    vies = 3;
                    niveauActuel = 1;
                    boule2 = null;
                    mur.construit(niveauActuel);
                    barre.setMiLargeur(25);
                    phase = ATTEND;
                } else {
                    fini = true;
                }
            }
            break;

        case GAGNE :
            if (niveauActuel >= 3) {
                // Pop-up "Victoire totale"
                JOptionPane.showMessageDialog(
                    this, 
                    "<html><h1 style='color: #43A047; text-align: center;'>Félicitations !</h1>" +
                    "<p style='text-align: center; font-size: 14px;'>Vous avez terminé tous les niveaux.</p></html>", 
                    "Victoire !", 
                    JOptionPane.PLAIN_MESSAGE
                );
                fini = true;
            } else {
                // Pop-up "Niveau suivant"
                JOptionPane.showMessageDialog(
                    this, 
                    "<html><h2 style='color: #1E88E5; text-align: center;'>Niveau " + niveauActuel + " terminé !</h2>" +
                    "<p style='text-align: center; font-size: 14px;'>Préparez-vous pour le niveau " + (niveauActuel + 1) + "</p></html>", 
                    "Niveau Complété", 
                    JOptionPane.PLAIN_MESSAGE
                );
                niveauActuel++;
                boule2 = null;
                barre.setMiLargeur(25);
                mur.construit(niveauActuel);
                phase = ATTEND;
                delai = DELAI;
            }
            break;

    }
  }
  }

  // Gestion du choc d'une boule avec une brique
  private void gereCollisionBrique(Boule b) {

    // Récupération de la hauteur d'une brique
    int hauteur = mur.getHauteurBrique();

    // Récupération de la largeur d'une brique
    int largeur = mur.getLargeurBrique();

    // Si la boule se trouve dans la zone du mur de briques ...
    if (b.getY()-b.getRayon()<10*(hauteur+1)) {

      int l1, l2, c1, c2;

      l1=(int)((b.getY()-b.getRayon())/(hauteur+1));
      l2=(int)((b.getY()+b.getRayon())/(hauteur+1));
      c1=(int)((b.getX()-b.getRayon())/(largeur+1));
      c2=(int)((b.getX()+b.getRayon())/(largeur+1));

      // Le rebond dépend des coins (1 ou 2) en contact avec une brique
      // Coin supérieur gauche ...
      if (mur.percute(l1,c1)) {

        // et coin supérieur droit
        if (mur.percute(l1,c2)) {
          // Choc vertical
          b.chocV();
        }
        else {
          // et coin inférieur gauche
          if (mur.percute(l2,c1)) {
            // Choc horizontal
            b.chocH();
          }
          else {
            // Double choc
            b.chocV();
            b.chocH();
          }
        }
      }
      else {
        // Coin supérieur droit ...
        if (mur.percute(l1,c2)) {

          // et coin inférieur droit
          if (mur.percute(l2,c2)) {
            // Choc horizontal
            b.chocH();
          }
          else {
            // Double choc
            b.chocV();
            b.chocH();
          }
        }
        else {
          // Coin inférieur gauche ...
          if (mur.percute(l2,c1)) {

            // et coin inférieur droit
            if (mur.percute(l2,c2)) {
              // Choc vertical
              b.chocV();
            }
            else {
              // Double choc
              b.chocV();
              b.chocH();
            }
          }
          else {
            // Coin inférieur droit
            if (mur.percute(l2,c2)) {
              // Double choc
              b.chocV();
              b.chocH();
            }
          }
        }
      }

      // Casse effective des brique du mur
      //(et mise en place des conséquences)
      modifJeu(mur.casse(l1,c1));
      modifJeu(mur.casse(l1,c2));
      modifJeu(mur.casse(l2,c1));
      modifJeu(mur.casse(l2,c2));

      // Si toutes les briques sont cassées ...
      if (mur.getNbBriques() == 0) {
          niveauActuel++;
          
          if (niveauActuel > 3) {
              phase = GAGNE;
          } else {
              mur.construit(niveauActuel);
              phase = ATTEND;
          }
      }
    }
  }

  void rebondSurBarre(int impact) {
    // Rebond sur la barre
    boule.chocV();

    // La barre est divisée en 5 parties. Chaque partie provoque un rebond différent
    // Partie extréme gauche : Augmentation de l'angle de 30 degrés
    if (impact<-(barre.getMiLargeur()*0.6))
      boule.modifAngle(30);
    else
      // Partie suivante : Augmentation de l'angle de 15 degrés
      if (impact<-(barre.getMiLargeur()*0.2))
        boule.modifAngle(15);

    // Partie extréme droite : Diminution de l'angle de 30 degrés
    if (impact>(barre.getMiLargeur()*0.6))
      boule.modifAngle(-30);
    else
      // Partie précédante : Diminution de l'angle de 15 degrés
      if (impact>(barre.getMiLargeur()*0.2))
        boule.modifAngle(-15);

    // La partie centrale de la barre provoque un rebond normal
  }

 public void modifJeu(int action) {
    switch (action) {
      case NORME :
        delai=DELAI;
        break;

      case RAPIDE :
        delai=(int)(DELAI/2);
        break;

      case DEDOUBLE :
        boule2 = new Boule();
        boule2.place(boule.getX(), boule.getY());
        boule2.copieMouvement(boule);
        break;

      case RETRECIT :
        barre.setMiLargeur(15);
        break;

      case AGRANDIT :
        barre.setMiLargeur(35);
        break;
    }
  }
  void lanceBoule(int angle) {
    if (phase==ATTEND) {
        phase=ROULE;
        boule.angleDep(angle);
    }
}
  public void paintComponent(Graphics comp) {
    Graphics2D comp2D = (Graphics2D)comp;
    
    // 1. Lisse les pixels pour rendre la balle et le texte beaucoup plus nets et beaux
    comp2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // 2. Fond de base
    comp2D.setColor(getBackground());
    comp2D.fillRect(0,0,getSize().width,getSize().height);

    // 3. Dessin du jeu classique
    barre.dessine(comp2D);
    boule.dessine(comp2D);
    if (boule2 != null) {
      boule2.dessine(comp2D);
    }
    if (mur != null) {
      mur.dessine(comp2D);
    }

    // 4. Affichage des infos (placé tout en bas à Y=360 pour ne rien gêner)
    comp2D.setFont(new Font("Arial", Font.BOLD, 14));
    comp2D.setColor(Color.BLACK);
    
    // Niveau en bas à gauche
    comp2D.drawString("Niveau : " + niveauActuel, 15, 360);
    
    // Texte "Vies :" un peu plus loin
    comp2D.drawString("Vies : ", 120, 360);

    // Dessin des petites pastilles rouges pour les vies
    comp2D.setColor(Color.RED);
    for (int i = 0; i < vies; i++) {
        comp2D.fillOval(165 + (i * 15), 350, 10, 10);
    }
  }

  // Méthodes de l'interface MouseMotionListener
  public void mouseMoved(MouseEvent evt) {
    // Si le pointeur est trop à gauche ...
    if (evt.getX()<barre.getMiLargeur())
      // barre contre le bord gauche
      barre.setX(barre.getMiLargeur());
    else
      // Si de pointeur est trop à droite ...
      if (evt.getX()>getSize().width-barre.getMiLargeur())
        // barre contre le bord droit
        barre.setX(getSize().width-barre.getMiLargeur());
      else
        // barre centrée sur le pointeur
        barre.setX(evt.getX());
  }

  public void mouseDragged(MouseEvent evt) {}

  // Méthodes de l'interface MouseListener
  public void mouseClicked(MouseEvent evt) {
    lanceBoule((int)(Math.random()*120)+30);
  }

  public void mouseEntered(MouseEvent evt) {}
  public void mouseExited(MouseEvent evt) {}
  public void mousePressed(MouseEvent evt) {}
  public void mouseReleased(MouseEvent evt) {}
}