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

import java.io.Serializable;


/**
 * Class qui à partir d'une ElementOfLink créer un element simple qui peut être serialisé.
 */
public class LinkToSave implements Serializable {
    //Les indexs des 2 images lié au lien
    int indexImage1;
    int indexImage2;

    /**
     * Constructeur
     * @param element ElementOfLink
     */
    public LinkToSave(ElementOfLink element) {

        indexImage1=DeviceManager.getArray_list_of_image().indexOf(element.img1);
        indexImage2=DeviceManager.getArray_list_of_image().indexOf(element.img2);
    }

    @Override
    public String toString() {
        String str = "IndexImage1 : "+ indexImage1 + "\nIndexImage2 : " + indexImage2;

        return str;
    }
}
