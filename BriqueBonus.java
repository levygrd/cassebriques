import java.awt.Color;

class BriqueBonus extends Brique {

    private final int BONUS = 6;

    public BriqueBonus() {
        super();
        couleur = Color.cyan; // Couleur unique et bien visible
    }

    public int choc() {
        if (!detruite) {
            detruite = true;
            return BONUS;
        }
        return 0;
    }
}