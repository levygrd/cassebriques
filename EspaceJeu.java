package cassebriques;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

class EspaceJeu extends JPanel implements Runnable, MouseListener,
                                          MouseMotionListener {

  // Delai entre 2 déplacements
  private final int DELAI = 16;

  // Constantes rattachées aux phases de jeu
  private final int ATTEND = 1;
  private final int ROULE = 2;
  private final int SORT = 3;
  private final int GAGNE = 4;

  // Constantes rattachées aux types de briques
  private final int SIMPLE = 0;
  private final int NORME = 1;
  private final int RAPIDE = 2;
  private final int DEDOUBLE = 3;
  private final int RETRECIT = 4;
  private final int AGRANDIT = 5;

  // Nombre maximum de boules
  private final int MAX_BOULES = 5;

  // Vies
  private int vies;

  // Niveau actuel
  private int niveauActuel;

  // Champs d'instance
  private Thread action;
  private boolean fini;
  private int phase;
  private int delai;
  private Barre barre;
  private Boule boule;

  // Collection de boules
  private ArrayList<Boule> boules;

  private Mur mur;


  public EspaceJeu() {

    // Création de la barre
    barre = new Barre();

    // Création de la boule principale
    boule = new Boule();

    // Création de la collection
    boules = new ArrayList<Boule>();

    // Ajout de la boule principale
    boules.add(boule);

    // Délai entre 2 déplacements
    delai = DELAI;

    // Phase d'attente
    phase = ATTEND;

    // Gestion des événements liés à la souris
    addMouseMotionListener(this);
    addMouseListener(this);
  }


  public void initialiseNiveau() {

    vies = 3;

    barre.setMiLargeur(25);

    niveauActuel = 1;

    // On vide la collection
    boules.clear();

    // On remet la boule principale
    boule = new Boule();
    boules.add(boule);

    // Arrêt du thread action s'il est en cours d'exécution
    fini = true;

    if (action != null) {
      while (action.isAlive()) {
      }
    }

    // Création du mur de briques
    if (mur == null) {
      mur = new Mur();
    }

    // Construction du mur
    mur.construit();

    // Première phase du jeu
    phase = ATTEND;
    delai = DELAI;

    // Lancement de l'exécution du jeu dans un thread
    action = new Thread(this);
    action.start();
  }

  public void niveauSuivant() {
    level++;
    mur.construit();
    phase= ATTEND;
    delai= DELAI;
  }

  // Traitement central exécuté avec une périodicité précise
  public void run() {

    fini = false;

    while (!fini) {

      switch (phase) {

        // =================
        // ATTENTE
        // =================
        case ATTEND:

          // Placement de toutes les boules sur la barre
          for (Boule b : boules) {

            b.place(
                barre.getX(),
                barre.getY() - b.getRayon()
            );
          }

          break;


        // =================
        // LA BOULE ROULE
        // =================
        case ROULE:

          // Parcours de toutes les boules
          for (int i = boules.size() - 1; i >= 0; i--) {

            Boule b = boules.get(i);

            // Déplacement
            b.deplace();


            // Rebond bord gauche
            if (b.getX() < b.getRayon()) {

              b.chocH();

              b.place(
                  b.getRayon(),
                  b.getY()
              );
            }


            // Rebond bord droit
            if (b.getX() > getSize().width - b.getRayon()) {

              b.chocH();

              b.place(
                  getSize().width - b.getRayon(),
                  b.getY()
              );
            }


            // Rebond sur le haut
            if (b.getY() < b.getRayon()) {

              b.chocV();

              b.place(
                  b.getX(),
                  b.getRayon()
              );
            }


            // Rebond sur la barre
            if (b.getY() > 310 - b.getRayon()) {

              if (
                  (b.getX() - b.getRayon()
                    < barre.getX() + barre.getMiLargeur())
                  &&
                  (b.getX() + b.getRayon()
                    > barre.getX() - barre.getMiLargeur())
              ) {

                // Rebond
                rebondSurBarre(
                    b,
                    b.getX() - barre.getX()
                );

                b.place(
                    b.getX(),
                    310 - b.getRayon()
                );
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
          }
          break;

    case SORT :

        vies--;

          if (vies > 0) {

            JOptionPane.showMessageDialog(
                this,
                "Balle perdue ! Vies restantes : " + vies,
                "Casse briques",
                JOptionPane.INFORMATION_MESSAGE
            );

            // Nouvelle boule
            boule = new Boule();

            boules.clear();
            boules.add(boule);

            phase = ATTEND;
          }

          else {

            barre.setMiLargeur(25);

            int choix = JOptionPane.showConfirmDialog(
                getTopLevelAncestor(),
                "Game Over !\nVoulez-vous relancer la partie ?",
                "Casse briques",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (choix == JOptionPane.YES_OPTION) {

              vies = 3;

              barre.setMiLargeur(25);

              // Nouvelle boule
              boule = new Boule();

              boules.clear();
              boules.add(boule);

              // Nouveau niveau
              niveauActuel = 1;

              // Nouveau délai
              delai = DELAI;

              // Nouveau mur
              if (mur == null) {
                mur = new Mur();
              }
              mur.construit();

              phase = ATTEND;
            }

            else {

              fini = true;
            }
          }

          break;


        // =================
        // GAGNE
        // =================
        case GAGNE:

          JOptionPane.showMessageDialog(
              this,
              "Bravo, vous avez gagné !",
              "Casse briques",
              JOptionPane.INFORMATION_MESSAGE
          );

          fini = true;

          break;
      }


      // Redessine l'espace de jeu
      repaint();


      try {

        Thread.sleep(delai);

      } catch (InterruptedException e) {
      }
    }
  }


  // =========================
  // COLLISION AVEC UNE BRIQUE
  // =========================
  private void gereCollisionBrique(Boule b) {

    int hauteur = mur.getHauteurBrique();
    int largeur = mur.getLargeurBrique();


    if (b.getY() - b.getRayon() < 10 * (hauteur + 1)) {

      int l1, l2, c1, c2;

      l1 = (int) (
          (b.getY() - b.getRayon())
          / (hauteur + 1)
      );

      l2 = (int) (
          (b.getY() + b.getRayon())
          / (hauteur + 1)
      );

      c1 = (int) (
          (b.getX() - b.getRayon())
          / (largeur + 1)
      );

      c2 = (int) (
          (b.getX() + b.getRayon())
          / (largeur + 1)
      );


      // Coin supérieur gauche
      if (mur.percute(l1, c1)) {

        // Coin supérieur droit
        if (mur.percute(l1, c2)) {

          b.chocV();
        }

        else {

          // Coin inférieur gauche
          if (mur.percute(l2, c1)) {

            b.chocH();
          }

          else {

            b.chocV();
            b.chocH();
          }
        }
      }

      else {

        // Coin supérieur droit
        if (mur.percute(l1, c2)) {

          // Coin inférieur droit
          if (mur.percute(l2, c2)) {

            b.chocH();
          }

          else {

            b.chocV();
            b.chocH();
          }
        }

        else {

          // Coin inférieur gauche
          if (mur.percute(l2, c1)) {

            // Coin inférieur droit
            if (mur.percute(l2, c2)) {

              b.chocV();
            }

            else {

              b.chocV();
              b.chocH();
            }
          }

          else {

            // Coin inférieur droit
            if (mur.percute(l2, c2)) {

              b.chocV();
              b.chocH();
            }
          }
        }
      }


      // Casse effective des briques
      modifJeu(mur.casse(l1, c1));
      modifJeu(mur.casse(l1, c2));
      modifJeu(mur.casse(l2, c1));
      modifJeu(mur.casse(l2, c2));


      // Toutes les briques sont cassées
      if (mur.getNbBriques() == 0) {

        niveauActuel++;

        if (niveauActuel > 3) {

          phase = GAGNE;
        }

        else {

          // Construction du nouveau mur
          mur.construit();
          phase = ATTEND;
        }
      }
    }
  }


  // =====================
  // REBOND SUR LA BARRE
  // =====================
  void rebondSurBarre(Boule b, int impact) {

    // Rebond vertical
    b.chocV();


    // Partie extrême gauche
    if (impact < -(barre.getMiLargeur() * 0.6)) {

      b.modifAngle(30);
    }

    else {

      // Partie suivante
      if (impact < -(barre.getMiLargeur() * 0.2)) {

        b.modifAngle(15);
      }
    }


    // Partie extrême droite
    if (impact > (barre.getMiLargeur() * 0.6)) {

      b.modifAngle(-30);
    }

    else {

      // Partie suivante
      if (impact > (barre.getMiLargeur() * 0.2)) {

        b.modifAngle(-15);
      }
    }
  }


  // =====================
  // MODIFICATION DU JEU
  // =====================
  public void modifJeu(int action) {

    switch (action) {

      case NORME:

        // Retour aux valeurs de base
        delai = DELAI;

        break;


      case RAPIDE:

        // Accélération
        delai = (int) (DELAI / 2);

        break;


      case DEDOUBLE:

        // Maximum 5 boules
        if (boules.size() < MAX_BOULES) {

          // On prend la dernière boule existante
          Boule source = boules.get(
              boules.size() - 1
          );

          // Création d'une nouvelle boule
          Boule nouvelleBoule = new Boule();

          // Même position
          nouvelleBoule.place(
              source.getX(),
              source.getY()
          );

          // Même mouvement
          nouvelleBoule.copieMouvement(source);

          // Ajout de la nouvelle boule
          boules.add(nouvelleBoule);
        }

        break;


      case RETRECIT:

        barre.setMiLargeur(15);

        break;


      case AGRANDIT:

        barre.setMiLargeur(35);

        break;
    }
  }


  // =========================
  // LANCER LA BOULE
  // =========================
  private void lanceBoule(int angle) {

    if (phase == ATTEND) {

      // Lance toutes les boules
      for (Boule b : boules) {

        b.angleDep(angle);
      }

      phase = ROULE;
    }
  }


  // ==========================
  // AFFICHAGE
  // ==========================
  public void paintComponent(Graphics comp) {

    Graphics2D comp2D = (Graphics2D) comp;


    // Effacement
    comp2D.setColor(getBackground());

    comp2D.fillRect(
        0,
        0,
        getSize().width,
        getSize().height
    );


    // Dessin de la barre
    barre.dessine(comp2D);


    // Dessin de toutes les boules
    for (Boule b : boules) {

      b.dessine(comp2D);
    }


    // Dessin du mur
    if (mur != null) {

      mur.dessine(comp2D);
    }


    // Affichage des vies
    comp2D.setColor(Color.black);
    comp2D.drawString("Vies : " + vies + " | Niveau : " + level, 10, 20);
  }


  // =========================================================
  // SOURIS
  // =========================================================
  public void mouseMoved(MouseEvent evt) {

    // Trop à gauche
    if (evt.getX() < barre.getMiLargeur()) {

      barre.setX(barre.getMiLargeur());
    }

    // Trop à droite
    else if (
        evt.getX()
        > getSize().width - barre.getMiLargeur()
    ) {

      barre.setX(
          getSize().width - barre.getMiLargeur()
      );
    }

    // Barre centrée
    else {

      barre.setX(evt.getX());
    }
  }


  public void mouseDragged(MouseEvent evt) {
  }


  public void mouseClicked(MouseEvent evt) {

    lanceBoule(
        (int) (Math.random() * 120) + 30
    );
  }


  public void mouseEntered(MouseEvent evt) {
  }


  public void mouseExited(MouseEvent evt) {
  }


  public void mousePressed(MouseEvent evt) {
  }


  public void mouseReleased(MouseEvent evt) {
  }
}