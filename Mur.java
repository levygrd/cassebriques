import java.awt.Graphics2D;

class Mur {
  private Brique[][] mur=new Brique[10][20];
  private int nbBriques;

  public void construit(int niveau) {
    nbBriques = 0; 
    
    for(int l=0; l<10; l++) {
      for(int c=0; c<20; c++) {
        
        if (niveau == 1) {
            // Génération avec toutes les briques de tes collègues + la nouvelle brique Bonus
            switch ((int)(Math.random()*15)) { // *15 pour rendre les briques spéciales un peu plus rares
              case 1 : mur[l][c]=new BriqueRetourNorme(); break;
              case 2 : mur[l][c]=new BriqueBouleRapide(); break;
              case 3 : mur[l][c]=new BriqueDedouble(); break;
              case 4 : mur[l][c]=new BriqueRetrecit(); break;
              case 5 : mur[l][c]=new BriqueAgrandit(); break;
              case 6 : mur[l][c]=new BriqueBonus(); break; // <-- LA NOUVELLE BRIQUE CYAN
              default : mur[l][c]=new Brique();
            }
            nbBriques++;
        } 
        else {
            // Niveau 2 par défaut (que des briques vertes pour l'instant)
            mur[l][c]=new Brique(); 
            nbBriques++;
        }
        
        mur[l][c].positionne(c*(mur[l][c].getLargeur()+1), l*(mur[l][c].getHauteur()+1));
      }
    }
  }

  public boolean percute(int l, int c) {
    if (l<0 || l > 9 || c<0 || c>19) { return false; }
    else if (mur[l][c].isDetruite()) { return false; }
    else { return true; }
  }

  public int casse(int l, int c) {
    int consequence=0;
    if (l>=0 && l <10 && c>=0 && c<20) {
      if (!mur[l][c].isDetruite()) {
        consequence=mur[l][c].choc();
        if (mur[l][c].isDetruite()) {
          nbBriques--;
        }
      }
    }
    return consequence;
  }

  public int getNbBriques() { return nbBriques; }
  public int getLargeurBrique() { return mur[0][0].getLargeur(); }
  public int getHauteurBrique() { return mur[0][0].getHauteur(); }

  public void dessine(Graphics2D support) {
    for(int l=0;l<10;l++) {
      for (int c=0; c<20; c++) {
        if (!mur[l][c].isDetruite()) mur[l][c].dessine(support);
      }
    }
  }
}