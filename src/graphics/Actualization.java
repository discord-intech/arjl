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
import exceptions.BadCallException;
import hardware.AbstractHardware;
import hardware.Link;
import hardware.client.AbstractClient;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;

/**
 * Created by Louis-Baptiste on 2016-05-12.
 */
public class Actualization {

    /**
     * On update les liens liés à une image pour le drag ans drop
     * @param imageView
     */
    public static void updateLinkDrag(ImageView imageView) {
        if(!DeviceManager.is_bugging()) {
            ArrayList<ElementOfLink> array_list_of_link = DeviceManager.getArray_list_of_link();
            int n = array_list_of_link.size();
            double imagex=imageView.getImage().getWidth();
            double imagey=imageView.getImage().getHeight();
            for (int i = 0; i < n; ++i) {
                ElementOfLink element = array_list_of_link.get(i);
                if (element.img1 == imageView) {
                    element.line.setStartX(imageView.getX() + imagex / 2);
                    element.line.setStartY(imageView.getY() + imagey / 2);
                }
                if (element.img2 == imageView) {
                    element.line.setEndX(imageView.getX() + imagex / 2);
                    element.line.setEndY(imageView.getY() + imagey / 2);
                }
            }

        }

    }

    /**
     * On update tous les liens pour le drag and drop
     */
    public static void updateLinkDrag() {

        if(!DeviceManager.is_bugging()) {
            ArrayList<ElementOfLink> array_list_of_link = DeviceManager.getArray_list_of_link();
            array_list_of_link.forEach(ElementOfLink -> {

                ImageView image1 = ElementOfLink.img1;
                ImageView image2 = ElementOfLink.img2;
                double image1x=image1.getImage().getWidth();
                double image1y=image1.getImage().getHeight();
                ElementOfLink.line.setStartX(image1.getX() + image1x / 2);
                ElementOfLink.line.setStartY(image1.getY() + image1y / 2);

                double image2x=image2.getImage().getWidth();
                double image2y=image2.getImage().getHeight();
                ElementOfLink.line.setEndX(image2.getX() + image2x / 2);
                ElementOfLink.line.setEndY(image2.getY() + image2y / 2);


            });
        }
    }


    /**
     * On update les images d'erreur
     */
    public static void updateError() throws BadCallException {
        ArrayList<Integer> arrayListOfStateError = DeviceManager.getArrayListOfStateError();
        ArrayList<ImageView> arrayList_of_error = DeviceManager.getArrayList_of_error();
        ArrayList<ImageView> array_list_of_image = DeviceManager.getArray_list_of_image();




        int n = arrayListOfStateError.size();
        for (int i = 0; i<n; i++ ) {
            Integer integer = arrayListOfStateError.get(i);
            int a = integer.compareTo(new Integer(0));
            if( a > 0 ) {

                arrayList_of_error.get(i).setX(array_list_of_image.get(i).getX()+array_list_of_image.get(i).getImage().getWidth()/2-arrayList_of_error.get(i).getImage().getWidth()/2);
                arrayList_of_error.get(i).setY(array_list_of_image.get(i).getY()+array_list_of_image.get(i).getImage().getHeight()/2-arrayList_of_error.get(i).getImage().getHeight()/2);
                arrayListOfStateError.set(i,new Integer(integer.intValue()-1));

            } else if (a==0) {

                arrayList_of_error.get(i).setX(-100);
                arrayList_of_error.get(i).setY(-100);


            } else {
                throw new BadCallException();
            }
        }





    }



    /**
     * On update les liens
     * @throws BadCallException
     */
    public static void updateLink() throws BadCallException {
        ArrayList<ElementOfLink> array_list_of_link = DeviceManager.getArray_list_of_link();
        ArrayList<Link> array_list_of_links_hard = DeviceManager.getArray_list_of_links_hard();
        Double zoomScale = DeviceManager.getZoomScale();

        int[] linkStats=actions.Actions.linkStats(array_list_of_links_hard);

        int n = linkStats.length;
        for(int i = 0; i<n;i++) {


            ElementOfLink elementOfLink = array_list_of_link.get(i);

            int ratio = linkStats[i];


            int red = 255*(ratio/100);
            int green = 255*(1-ratio/100);

            elementOfLink.line.setStrokeWidth(4*zoomScale);
            elementOfLink.line.setStroke(Color.rgb(red,green,0));

        }
    }


    /**
     * On actualise le trafic dans les progressBars
     */
    public static void updateprogressBar() {
        if(!DeviceManager.getLoadmode()) {
            ArrayList<AbstractHardware> array_list_of_devices = DeviceManager.getArray_list_of_devices();
            ArrayList<ImageView> array_list_of_image = DeviceManager.getArray_list_of_image();
            ArrayList<ProgressBar> arrayList_of_progressBar = DeviceManager.getArrayList_of_progressBar();
            ArrayList<ProgressBar> arrayList_of_progressBarRequest = DeviceManager.getArrayList_of_progressBarRequest();

            int[] progressionList = Actions.hardwareStats(array_list_of_devices);

            for (int i = 0; i < progressionList.length; i++) {
                if(array_list_of_image.get(i).getAccessibleText().contains("pc")) {
                    AbstractClient abstractClient = (AbstractClient) array_list_of_devices.get(i);
                    arrayList_of_progressBarRequest.get(i).setProgress((float) abstractClient.progress() / 100);
                }

                arrayList_of_progressBar.get(i).setProgress((float) progressionList[i] / 100);

            }



        }

    }


    /**
     * On update les menus de info et barre de progression si on change de pack de texture
     */
    public static void updateInfoEtBarre() {
        ArrayList<AnchorPane> arrayList_of_infoMenu = DeviceManager.getArrayList_of_infoMenu();
        ArrayList<ImageView> array_list_of_image = DeviceManager.getArray_list_of_image();
        ArrayList<ProgressBar> arrayList_of_progressBar = DeviceManager.getArrayList_of_progressBar();
        ArrayList<ProgressBar> arrayList_of_progressBarRequest = DeviceManager.getArrayList_of_progressBarRequest();


        int n = arrayList_of_infoMenu.size();
        for (int i = 0;i<n;i++) {
            arrayList_of_infoMenu.get(i).setLayoutX(array_list_of_image.get(i).getX() + array_list_of_image.get(i).getImage().getWidth());
            arrayList_of_infoMenu.get(i).setLayoutY(array_list_of_image.get(i).getY());
        }



        int m = arrayList_of_progressBar.size();
        for (int i = 0;i<m;i++) {
            arrayList_of_progressBar.get(i).setLayoutX(array_list_of_image.get(i).getX());
            arrayList_of_progressBar.get(i).setLayoutY(array_list_of_image.get(i).getY() + array_list_of_image.get(i).getImage().getHeight());
            arrayList_of_progressBar.get(i).setPrefWidth(array_list_of_image.get(i).getImage().getWidth());
            arrayList_of_progressBar.get(i).setPrefHeight(1);

            arrayList_of_progressBarRequest.get(i).setLayoutX(array_list_of_image.get(i).getX());
            arrayList_of_progressBarRequest.get(i).setLayoutY(array_list_of_image.get(i).getY() - 13);
            arrayList_of_progressBarRequest.get(i).setPrefWidth(array_list_of_image.get(i).getImage().getWidth());
            arrayList_of_progressBarRequest.get(i).setPrefHeight(1);



        }




    }
    /**
     * On update les menus de info
     */
    public static void updateInfoMenu() {

        ArrayList<AnchorPane> arrayList_of_infoMenu = DeviceManager.getArrayList_of_infoMenu();
        ArrayList<Integer> arrayList_of_state = DeviceManager.getArrayList_of_state();
        ArrayList<AbstractHardware> array_list_of_devices = DeviceManager.getArray_list_of_devices();

        arrayList_of_infoMenu.forEach(infoAnchorPane -> {


            ObservableList<Node> list_of_machin = infoAnchorPane.getChildren();
            Text t0 = new Text();
            t0 = (Text) list_of_machin.get(0);


            if (!t0.getText().equals("Device = Error")) {
                //On update la info d'un PC



                Text t1 = (Text) list_of_machin.get(1);



                if (arrayList_of_state.get(arrayList_of_infoMenu.indexOf(infoAnchorPane)).equals(1)) {
                    t1.setText("Power : " + "ON");
                } else {
                    t1.setText("Power : " + "OFF");
                }

                Text t2 = (Text) list_of_machin.get(2);






                t2.setText("Free ports : " + array_list_of_devices.get(arrayList_of_infoMenu.indexOf(infoAnchorPane)).getFreePorts().size());
            }




        });




    }
}
