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

import actions.Actions;
import enums.LinkTypes;
import exceptions.BadCallException;
import hardware.AbstractHardware;
import hardware.Link;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.ArrayList;

import static graphics.DeviceManager.afficheError;
import static graphics.DeviceManager.reset;

/**
 * Created by Louis-Baptiste on 2016-05-12.
 */
public class AddLink {
    /**
     * Ajout d'un lien
     * @param content
     * @param status
     * @param anchorPane
     * @throws exceptions.BadCallException
     */
    public static void addlink (AnchorPane content, Label status, AnchorPane anchorPane) throws exceptions.BadCallException {
        if(!DeviceManager.is_bugging()) {
            DeviceManager.setCreatemode(true);
            ArrayList<ImageView> array_list_of_image = DeviceManager.getArray_list_of_image();
            ArrayList<Link> array_list_of_links_hard = DeviceManager.getArray_list_of_links_hard();
            ArrayList<AbstractHardware> array_list_of_devices = DeviceManager.getArray_list_of_devices();
            ArrayList<ElementOfLink> array_list_of_link = DeviceManager.getArray_list_of_link();
            ImageView firstimageView = DeviceManager.getFirstimageView();
            ImageView secondimageView = DeviceManager.getSecondimageView();
            Double firstimagesizeX = DeviceManager.getFirstimagesizeX();
            Double firstimagesizeY = DeviceManager.getFirstimagesizeY();
            Double secondimagesizeX = DeviceManager.getSecondimagesizeX();
            Double secondimagesizeY = DeviceManager.getSecondimagesizeY();

            Line line = new Line();

            line.setStartX(firstimageView.getX() + firstimagesizeX / 2);
            line.setStartY(firstimageView.getY() + firstimagesizeY / 2);
            line.setEndX(secondimageView.getX() + secondimagesizeX / 2);
            line.setEndY(secondimageView.getY() + secondimagesizeY / 2);

            line.setOnMouseClicked(event -> {
                if (DeviceManager.getsupressmode()) {
                    try {
                        Supress.suprimerLien(line, content, status);
                    } catch (BadCallException e) {
                        reset("", status, content);
                        e.printStackTrace();
                    }
                }
            });


            try {
                int index1 = array_list_of_image.indexOf(firstimageView);
                int index2 = array_list_of_image.indexOf(secondimageView);

                if (!DeviceManager.getLoadmode()) {
                    array_list_of_links_hard.add(Actions.connect(array_list_of_devices.get(index1), array_list_of_devices.get(index2), LinkTypes.ETH));
                }
                line.setStrokeWidth(4*DeviceManager.getZoomScale());
                line.setStroke(Color.web("000000"));
                content.getChildren().add(line);
                line.toBack();


                array_list_of_link.add(new ElementOfLink(firstimageView, secondimageView, line));



                DeviceManager.setAddlinkmode(1);
                status.setText("addLink");
                Actualization.updateInfoMenu();
            } catch (BadCallException e) {
                reset("", status, content);
                afficheError("Unable to create link between this two devices");
            }

            DeviceManager.setCreatemode(false);
        }
    }






    /**
     * Passer en mode création de liens
     * @param status
     * @param content
     */
    public static void addlinkmodeactivate (Label status,AnchorPane content) {
        if(!DeviceManager.is_bugging()) {
            int addlinkmode = DeviceManager.getAddlinkmode();
            if (addlinkmode != 0) {
                reset("", status, content);
            } else {
                status.setText("addLink");
                DeviceManager.setAddlinkmode(1);
                reset("addlink", status, content);
            }
        }
    }
}
