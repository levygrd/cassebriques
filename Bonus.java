import java.awt.*;

public class Bonus {
    private int x, y, type, rayon, vitesse;
    private Color couleur;

    public Bonus(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.rayon = 6; // Taille du bonus
        this.vitesse = 3; // Vitesse de chute
        
        // On attribue une couleur selon le type de pouvoir
        switch (type) {
            case 1: couleur = Color.pink; break;    // Retour norme
            case 2: couleur = Color.yellow; break;  // Balle rapide
            case 3: couleur = Color.red; break;     // Dédoublement
            case 4: couleur = Color.orange; break;  // Rétrécit
            case 5: couleur = Color.green; break;   // Agrandit
            default: couleur = Color.white;
        }
    }

    public void deplace() {
        y += vitesse;
    }

    public void dessine(Graphics2D motif) {
        motif.setColor(couleur);
        // Dessine un petit carré pour représenter le bonus qui tombe
        motif.fillRect(x - rayon, y - rayon, rayon * 2, rayon * 2);
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getRayon() { return rayon; }
    public int getType() { return type; }
}