import java.awt.Color;

class BriqueDedouble extends Brique {

    private final int DEDOUBLE = 3;

    public BriqueDedouble() {
        super();
        couleur = Color.pink;
    }

    public int choc() {
        if (!detruite) {
            detruite = true;
            return DEDOUBLE;
        }
        return 0;
    }
}