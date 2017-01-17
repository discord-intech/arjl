/**
 * Copyright (C) 2016 Desvignes Julian, Louis-Baptiste Trailin, Aymeric Gleye, Rémi Dulong
 */

/**
 This file is part of ARJL.

 ARJL is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 ARJL is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with ARJL.  If not, see <http://www.gnu.org/licenses/>

 */

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
