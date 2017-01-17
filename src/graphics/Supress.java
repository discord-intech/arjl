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
import hardware.AbstractHardware;
import hardware.Link;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

import java.util.ArrayList;

import static graphics.DeviceManager.reset;

/**
 * Created by Louis-Baptiste on 2016-05-12.
 */
public class Supress {
    /**
     * Suppression d'une image
     * @param imageView
     * @param anchorPane
     * @throws exceptions.BadCallException
     */
    public static synchronized void suprimerImage (ImageView imageView, AnchorPane anchorPane) throws exceptions.BadCallException {
        if(!DeviceManager.is_bugging()) {
            ArrayList<ImageView> array_list_of_image = DeviceManager.getArray_list_of_image();
            ArrayList<ElementOfLink> array_list_of_link = DeviceManager.getArray_list_of_link();
            ArrayList<Link> array_list_of_links_hard = DeviceManager.getArray_list_of_links_hard();
            ArrayList<AbstractHardware> array_list_of_devices = DeviceManager.getArray_list_of_devices();
            ArrayList<ProgressBar> arrayList_of_progressBar = DeviceManager.getArrayList_of_progressBar();
            ArrayList<ProgressBar> arrayList_of_progressBarRequest = DeviceManager.getArrayList_of_progressBarRequest();
            ArrayList<AnchorPane> arrayList_of_infoMenu = DeviceManager.getArrayList_of_infoMenu();
            ArrayList<Integer> arrayListOfStateError = DeviceManager.getArrayListOfStateError();
            ArrayList<ImageView> arrayList_of_error = DeviceManager.getArrayList_of_error();
            ArrayList<Stage> arrayList_of_configStage = DeviceManager.getArrayList_of_configStage();
            ArrayList<Integer> arrayList_of_state = DeviceManager.getArrayList_of_state();
            ArrayList<Double> arrayListOfX = DeviceManager.getArrayListOfX();
            ArrayList<Double> arrayListOfY = DeviceManager.getArrayListOfY();
            ArrayList<Stage> arrayList_of_requestStage=DeviceManager.getArrayList_of_requestStage();
            ArrayList<Stage> arrayList_of_DHCPStage=DeviceManager.getarrayList_of_stageDHCP();


            int index = array_list_of_image.indexOf(imageView);




            ArrayList<ElementOfLink> list_element = new ArrayList<>();

            int n = array_list_of_link.size();
            ArrayList<Integer> array_list_of_index = new ArrayList();
            for (int i = 0; i < n; ++i) {

                ElementOfLink element = array_list_of_link.get(i);
                if (element.img1 == imageView) {
                    array_list_of_index.add(i);
                    list_element.add(element);
                }
                if (element.img2 == imageView) {
                    array_list_of_index.add(i);
                    list_element.add(element);
                }

            }






            for (int i = array_list_of_index.size() - 1; i >= 0; i--) {


                Actions.disconnect(array_list_of_links_hard.get(array_list_of_index.get(i).intValue()));
                array_list_of_link.remove(i);
                array_list_of_links_hard.remove(i);
            }



            if(imageView.getAccessibleText().contains("wan")) {
                DeviceManager.setWAN(DeviceManager.getWAN()-1);
            }
            array_list_of_devices.get(index).stop();
            array_list_of_devices.remove(index);
            array_list_of_image.remove(index);
            anchorPane.getChildren().remove(imageView);
            anchorPane.getChildren().remove(arrayList_of_infoMenu.get(index));
            arrayList_of_infoMenu.remove(index);
            anchorPane.getChildren().remove(arrayList_of_progressBar.get(index));
            arrayList_of_progressBar.remove(index);
            anchorPane.getChildren().remove(arrayList_of_progressBarRequest.get(index));
            arrayList_of_progressBarRequest.remove(index);
            arrayList_of_configStage.get(index).close();
            arrayList_of_configStage.remove(index);
            arrayList_of_requestStage.get(index).close();
            arrayList_of_requestStage.remove(index);
            arrayList_of_DHCPStage.get(index).close();
            arrayList_of_DHCPStage.remove(index);
            arrayList_of_state.remove(index);
            anchorPane.getChildren().remove(arrayList_of_error.get(index));
            arrayList_of_error.remove(index);
            arrayListOfStateError.remove(index);
            arrayListOfX.remove(index);
            arrayListOfY.remove(index);

            n = list_element.size();
            for (int i = 0; i < n; ++i) {

                ElementOfLink element = list_element.get(i);
                anchorPane.getChildren().remove(element.line);
                array_list_of_link.remove(element);
            }


            Actualization.updateInfoMenu();
        }
    }

    /**
     * Suppression d'un lien
     * @param line
     * @param anchorPane
     * @param status
     * @throws exceptions.BadCallException
     */
    public static void suprimerLien (Line line, AnchorPane anchorPane, Label status) throws exceptions.BadCallException {

        if(!DeviceManager.is_bugging()) {
            ArrayList<ElementOfLink> array_list_of_link = DeviceManager.getArray_list_of_link();
            ArrayList<Link> array_list_of_links_hard = DeviceManager.getArray_list_of_links_hard();

            anchorPane.getChildren().remove(line);


            ElementOfLink element_supr = null;

            int n = array_list_of_link.size();
            for (int i = 0; i < n; ++i) {
                ElementOfLink element = array_list_of_link.get(i);
                if (element.line == line) {
                    element_supr = element;
                }

            }
            int index = array_list_of_link.indexOf(element_supr);

            Actions.disconnect(array_list_of_links_hard.get(index));

            array_list_of_link.remove(index);
            array_list_of_links_hard.remove(index);


            Actualization.updateInfoMenu();
        }
    }

    /**
     * Passer en mode DELETE
     * @param status
     * @param content
     */
    public static void suprimodeactivate (Label status,AnchorPane content) {
        if(!DeviceManager.is_bugging()) {
            if (DeviceManager.getsupressmode()) {
                reset("", status, content);
            } else {

                status.setText("DELETE");
                DeviceManager.setsupressmode(true);
                reset("delete", status, content);
            }
        }
    }

}
