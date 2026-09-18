import java.awt.Color;

class BriqueAgrandit extends Brique {

    private final int AGRANDIT = 5;

    public BriqueAgrandit() {
        super();
        couleur = Color.green;
    }

    public int choc() {
        super.choc();
        return AGRANDIT;
    }
}