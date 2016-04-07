package graphics;

import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;


/**
 * Classe qui décrit un lien graphique. Donc les deux images assiocié et la ligne entre elles
 */
public class ElementOfLink {
    /**
     * ImageView 1 et 2
     */
    ImageView img1;
    ImageView img2;
    /**
     * Ligne
     */
    Line line;

    ElementOfLink(ImageView img1,ImageView img2, Line line) {
        this.img1=img1;
        this.img2=img2;
        this.line=line;
    }

    public Line getLine() {
        return line;
    }
}
