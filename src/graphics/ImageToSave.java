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
import javafx.scene.layout.AnchorPane;

import java.io.Serializable;


/**
 * Class qui à partir d'une imageview créer un element simple qui peut être serialisé.
 */
public class ImageToSave implements Serializable{
    /**
     * Type de l'image (pc,server, etc ...)
     */
    String type = null;
    /**
     * Position de l'image
     */
    double x = 0;
    double y = 0;
    /**
     * Dit si la progress bar de l'image est affiché
     */
    boolean progressBarIsShowing = false;
    boolean requestBarIsShowing = false;

    public ImageToSave (ImageView imageView)
    {
        //On récupére le type qui est stocké dans AccessibleText
        type=imageView.getAccessibleText();
        x = imageView.getX()/DeviceManager.getZoomScale();
        y = imageView.getY()/DeviceManager.getZoomScale();

        //On regarde si la progress bar est un fils de l'anchorpane <=> si la progress bar est affiché
        AnchorPane content = (AnchorPane) imageView.getParent();
        progressBarIsShowing = content.getChildren().contains(DeviceManager.getArrayList_of_progressBar().get(DeviceManager.getArray_list_of_image().indexOf(imageView)));
        requestBarIsShowing = content.getChildren().contains(DeviceManager.getArrayList_of_progressBarRequest().get(DeviceManager.getArray_list_of_image().indexOf(imageView)));
    }




    @Override
    public String toString() {
        String str = "Type : "+ type + "\nPosition X : " + x +"\nPosition Y : " + y;

        return str;
    }

}