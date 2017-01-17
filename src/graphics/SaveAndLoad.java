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
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

import static graphics.DeviceManager.reset;

/**
 * Classe qui gére la sauvegarde et le chargement
 */
public class SaveAndLoad {


    /**
     * Chargement
     * @param primaryStage Fenêtre
     * @param content      AnchorPane of the ScrollPane
     * @param anchorPane   AnchorPane
     * @param status       Label au bois dormant
     * @param scrollPane   ScrollPane
     */
    public void Load (Stage primaryStage, AnchorPane content, AnchorPane anchorPane, Label status , ScrollPane scrollPane,MenuItem saveButton) {
        //On met en pause la simu
        if(!DeviceManager.getIsPause()) {
            ArrayList<AbstractHardware> arrayList_Hardware = DeviceManager.getArray_list_of_devices();
            for (AbstractHardware abstractHardWare : arrayList_Hardware) {
                abstractHardWare.changeState(false);
            }
        }




        //On passe le LoadMode en true
        DeviceManager.setLoadmode(true);

        //On ouvre une fenetre de selection de fichier avec comme préférance l'extention .arjl
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ARJL files (*.arjl)", "*.arjl");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);



        //Si on a selectionner un fichier valide
        if (file != null) {
            try {
                //File actuel
                DeviceManager.setCurrentFile(file,primaryStage);
                saveButton.setDisable(false);



                // Partie effacement de l'affichage. On enlève tout élément présent du graphe

                DeviceManager.stopThread();

                DeviceManager.getArray_list_of_image().forEach(ImageView -> {

                    content.getChildren().remove(ImageView);
                });
                DeviceManager.setArray_list_of_image(new ArrayList<ImageView>());

                DeviceManager.getArrayList_of_infoMenu().forEach(AnchorPane -> {

                    content.getChildren().remove(AnchorPane);
                });
                DeviceManager.setArrayList_of_infoMenu(new ArrayList<AnchorPane>());

                DeviceManager.getArrayList_of_progressBar().forEach(ProgressBar -> {
                    content.getChildren().remove(ProgressBar);

                });
                DeviceManager.setArrayList_of_progressBar(new ArrayList<ProgressBar>());


                DeviceManager.getArrayList_of_progressBarRequest().forEach(ProgressBar -> {
                    content.getChildren().remove(ProgressBar);

                });
                DeviceManager.setArrayList_of_progressBarRequest(new ArrayList<ProgressBar>());


                DeviceManager.getArray_list_of_link().forEach(Element -> {
                    content.getChildren().remove(Element.getLine());

                });

                DeviceManager.getArrayList_of_error().forEach(imageView -> {
                    content.getChildren().remove(imageView);
                });
                DeviceManager.closeEvryConfigStage();
                DeviceManager.closeEvryRequestStage();
                DeviceManager.closeEvryDHCPStage();

                DeviceManager.setArrayList_of_infoMenu(new ArrayList<>());
                DeviceManager.setArray_list_of_devices(new ArrayList<>());
                DeviceManager.setArray_list_of_links_hard(new ArrayList<>());
                DeviceManager.setArrayList_of_configStage(new ArrayList<>());
                DeviceManager.setArrayList_of_requestStage(new ArrayList<>());
                DeviceManager.setarrayList_of_stageDHCP(new ArrayList<>());
                DeviceManager.setArrayList_of_error(new ArrayList<>());
                DeviceManager.setArrayListOfStateError(new ArrayList<>());
                DeviceManager.setArrayListOfX(new ArrayList<>());
                DeviceManager.setArrayListOfY(new ArrayList<>());
                DeviceManager.setWAN(0);



                // Partie où l'on charge


                ArrayList <ArrayList<Object>> load_list= Actions.loadGUIGraph(file.getPath().toString());
                DeviceManager.setArray_list_of_devices(load_list.get(1));
                DeviceManager.setArray_list_of_links_hard(load_list.get(0));

                loadImageView(load_list.get(2),anchorPane,scrollPane,content,status,primaryStage);
                loadLink(load_list.get(3),anchorPane,content,status);

                DeviceManager.startThread();
                Actualization.updateInfoEtBarre();
                DeviceManager.resetzoomScale();









            }  catch (BadCallException e) {
                DeviceManager.afficheError("Impossible to load this file");
            }
        }





        DeviceManager.setLoadmode(false);
    }


    /**
     * Sauvegarde
     * @param primaryStage Fenêtre of the WORLD
     * @param anchorPane   AnchorPane of the Universe
     */
    public void Save (Stage primaryStage,AnchorPane anchorPane,MenuItem saveButton) {

        //On met en pause la simu
        if(!DeviceManager.getIsPause()) {
            ArrayList<AbstractHardware> arrayList_Hardware = DeviceManager.getArray_list_of_devices();
            for (AbstractHardware abstractHardWare : arrayList_Hardware) {
                abstractHardWare.changeState(false);
            }
        }

        //On ouvre une fenetre de selection de fichier avec comme préférance l'extention .arjl
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("ARJL files (*.arjl)", "*.arjl");
        fileChooser.getExtensionFilters().add(extFilter);



        File file = fileChooser.showSaveDialog(primaryStage);


        //Si le fichier est valide
        if (file != null) {

            try {
                //File actuel
                DeviceManager.setCurrentFile(file,primaryStage);
                saveButton.setDisable(false);


                //On convertie les images et les liens
                ArrayList<ImageToSave> array_list_of_imageconverted = converterImageView();
                ArrayList<LinkToSave> array_list_of_linkconverted = converterLink();


                //Et on sauvegarde le tout dans une bonne pitite marmite à feu doux.
                Actions.saveGUIGraph(file.getPath().toString(),DeviceManager.getArray_list_of_devices(),DeviceManager.getArray_list_of_links_hard(),array_list_of_imageconverted,array_list_of_linkconverted);





            } catch (BadCallException e) {
                DeviceManager.afficheError("Impossible to save");
            }
        }

        //On remet en play si besoin
        if(!DeviceManager.getIsPause()) {
            ArrayList<AbstractHardware> arrayList_Hardware = DeviceManager.getArray_list_of_devices();

            ArrayList<Integer> arrayList = DeviceManager.getArrayList_of_state();

            int n = arrayList.size();

            for (int i = 0; i < n; i++) {
                if (arrayList.get(i).equals(1)) {
                    arrayList_Hardware.get(i).changeState(true);
                }

            }

        }
    }

    public void SaveControleS (Stage primaryStage,AnchorPane anchorPane) {



        //On met en pause la simu
        if(!DeviceManager.getIsPause()) {
            ArrayList<AbstractHardware> arrayList_Hardware = DeviceManager.getArray_list_of_devices();
            for (AbstractHardware abstractHardWare : arrayList_Hardware) {
                abstractHardWare.changeState(false);
            }
        }

        File file = DeviceManager.getCurrentFile();


        //Si le fichier est valide
        if (file != null) {

            try {

                //On convertie les images et les liens
                ArrayList<ImageToSave> array_list_of_imageconverted = converterImageView();
                ArrayList<LinkToSave> array_list_of_linkconverted = converterLink();


                //Et on sauvegarde le tout dans une bonne pitite marmite à feu doux.
                Actions.saveGUIGraph(file.getPath().toString(),DeviceManager.getArray_list_of_devices(),DeviceManager.getArray_list_of_links_hard(),array_list_of_imageconverted,array_list_of_linkconverted);





            } catch (BadCallException e) {
                DeviceManager.afficheError("Impossible to save");
            }
        }

        //On remet en play si besoin
        if(!DeviceManager.getIsPause()) {
            ArrayList<AbstractHardware> arrayList_Hardware = DeviceManager.getArray_list_of_devices();

            ArrayList<Integer> arrayList = DeviceManager.getArrayList_of_state();

            int n = arrayList.size();

            for (int i = 0; i < n; i++) {
                if (arrayList.get(i).equals(1)) {
                    arrayList_Hardware.get(i).changeState(true);
                }

            }

        }
    }

    /**
     * On convertit les ImageView pour la sauvegarde
     * @return
     */
    public static ArrayList<ImageToSave> converterImageView() {
        ArrayList<ImageView> array_list_of_image = DeviceManager.getArray_list_of_image();


        ArrayList <ImageToSave > array_list_of_imageconverted = new ArrayList<>();


        array_list_of_image.forEach(imageView -> {
            ImageToSave imageToSave = new ImageToSave(imageView);
            array_list_of_imageconverted.add(imageToSave);

        });




        return array_list_of_imageconverted;




    }


    /**
     * On recrée les ImageView depuis la sauvegarde
     * @param array_list_of_imageconverted
     * @param anchorPane
     * @param scrollPane
     * @param content
     * @param status
     */
    public static void loadImageView(ArrayList<Object> array_list_of_imageconverted, AnchorPane anchorPane,  ScrollPane scrollPane, AnchorPane content, Label status,Stage primaryStage){



        int n = array_list_of_imageconverted.size();
        for(int i=0;i<n;i++) {
            ImageToSave imageToSave = (ImageToSave) array_list_of_imageconverted.get(i);
            String type = imageToSave.type;
            double x = imageToSave.x;
            double y = imageToSave.y;
            try {
                DeviceManager.device(type,anchorPane,scrollPane,content,status,primaryStage);
            } catch (BadCallException e) {
                e.printStackTrace();
            }
            ArrayList<ImageView> array_list_of_image = DeviceManager.getArray_list_of_image();
            ArrayList<Double> arrayListOfX = DeviceManager.getArrayListOfX();
            ArrayList<Double> arrayListOfY = DeviceManager.getArrayListOfY();

            ImageView imageView = array_list_of_image.get(i);
            imageView.setX(x);
            arrayListOfX.set(i,x);
            imageView.setY(y);
            arrayListOfY.set(i,y);

            ArrayList<ProgressBar> arrayList_of_progressBar = DeviceManager.getArrayList_of_progressBar();
            ArrayList<ProgressBar> arrayList_of_progressBarRequest = DeviceManager.getArrayList_of_progressBarRequest();

            if(imageToSave.progressBarIsShowing) {

                content.getChildren().add(arrayList_of_progressBar.get(i));
            }
            if(imageToSave.requestBarIsShowing) {
                content.getChildren().add(arrayList_of_progressBarRequest.get(i));
            }
        }



    }





    /**
     * On convertit les liens pour la sauvegarde
     * @return
     */
    public static ArrayList<LinkToSave> converterLink() {
        ArrayList<ElementOfLink> array_list_of_link = DeviceManager.getArray_list_of_link();


        ArrayList <LinkToSave > array_list_of_linkconverted = new ArrayList<>();


        array_list_of_link.forEach(Element_of_Link-> {
            LinkToSave linkToSave = new LinkToSave(Element_of_Link);
            array_list_of_linkconverted.add(linkToSave);

        });




        return array_list_of_linkconverted;
    }


    /**
     * On recrée les liens depuis la sauvegarde
     * @param array_list_of_linkconverted
     * @param anchorPane
     * @param content
     * @param status
     */
    public static void loadLink (ArrayList<Object> array_list_of_linkconverted, AnchorPane anchorPane,  AnchorPane content, Label status) {

        ArrayList<ImageView> array_list_of_image = DeviceManager.getArray_list_of_image();

        int n = array_list_of_linkconverted.size();
        DeviceManager.setAddlinkmode(1);

        for(int i=0;i<n;i++) {

            LinkToSave linkToSave = (LinkToSave) array_list_of_linkconverted.get(i);
            int indexImage1=linkToSave.indexImage1;
            int indexImage2=linkToSave.indexImage2;

            DeviceManager.setFirstimageView(array_list_of_image.get(indexImage1));
            Image image1=DeviceManager.getFirstimageView().getImage();
            DeviceManager.setFirstimagesizeX(image1.getWidth());
            DeviceManager.setFirstimagesizeY(image1.getHeight());
            DeviceManager.setSecondimageView(array_list_of_image.get(indexImage2));
            Image image2=DeviceManager.getSecondimageView().getImage();
            DeviceManager.setSecondimagesizeX(image2.getWidth());
            DeviceManager.setSecondimagesizeY(image2.getHeight());



            try {
                AddLink.addlink(content,status,anchorPane);
            } catch (BadCallException e) {
                e.printStackTrace();
            }

        }
        reset("",status,content);




    }



}
