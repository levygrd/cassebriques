import java.awt.*;

public class Bonus {
    private int x, y, type, rayon, vitesse;
    private Color couleur;

    public Bonus(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.rayon = 6; 
        this.vitesse = 3; 
        
        switch (type) {
            case 1: couleur = Color.pink; break;
            case 2: couleur = Color.yellow; break;
            case 3: couleur = Color.magenta; break;
            case 4: couleur = Color.orange; break;
            case 5: couleur = Color.cyan; break;
            default: couleur = Color.white;
        }
    }

    public void deplace() {
        y += vitesse;
    }

    public void dessine(Graphics2D motif) {
        motif.setColor(couleur);
        motif.fillRect(x - rayon, y - rayon, rayon * 2, rayon * 2);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getRayon() { return rayon; }
    public int getType() { return type; }
}