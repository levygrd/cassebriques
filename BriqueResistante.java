import java.awt.Color;

class BriqueResistante extends Brique {
    
    private int pointsDeVie;

    public BriqueResistante() {
        super();
        this.pointsDeVie = 3;
        actualiserCouleur();
    }

    private void actualiserCouleur() {
        if (pointsDeVie == 3) {
            couleur = Color.black;
        } else if (pointsDeVie == 2) {
            couleur = Color.darkGray;
        } else if (pointsDeVie == 1) {
            couleur = Color.lightGray;
        }
    }

    public int choc() {
        if (!detruite) {
            pointsDeVie--;
            actualiserCouleur();
            
            if (pointsDeVie <= 0) {
                detruite = true;
            }
        }
        // Retourne 0 car c'est une brique "neutre" qui ne lâche pas de bonus direct
        return 0; 
    }
}