import java.awt.Color;

class BriqueRetrecit extends Brique {

    private final int RETRECIT = 4;

    public BriqueRetrecit() {
        super();
        couleur = Color.orange;
    }

    public int choc() {
        if (!detruite) {
            detruite = true;
            return RETRECIT;
        }
        return 0;
    }
}