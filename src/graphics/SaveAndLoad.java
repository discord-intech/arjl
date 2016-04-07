package graphics;


import actions.Actions;
import exceptions.BadCallException;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

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
                DeviceManager.closeEvryConfigStage();
                DeviceManager.closeEvryRequestStage();

                DeviceManager.setArrayList_of_infoMenu(new ArrayList<>());
                DeviceManager.setArray_list_of_devices(new ArrayList<>());
                DeviceManager.setArray_list_of_links_hard(new ArrayList<>());
                DeviceManager.setArrayList_of_configStage(new ArrayList<>());
                DeviceManager.setArrayList_of_requestStage(new ArrayList<>());

                System.out.println("La machine est vide !");

                // Partie où l'on charge


                ArrayList <ArrayList<Object>> load_list= Actions.loadGUIGraph(file.getPath().toString());
                DeviceManager.setArray_list_of_devices(load_list.get(1));
                DeviceManager.setArray_list_of_links_hard(load_list.get(0));

                DeviceManager.loadImageView(load_list.get(2),anchorPane,scrollPane,content,status,primaryStage);
                DeviceManager.loadLink(load_list.get(3),anchorPane,content,status);

                DeviceManager.startThread();
                DeviceManager.updateInfoEtBarre();









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
                ArrayList<ImageToSave> array_list_of_imageconverted = DeviceManager.converterImageView();
                ArrayList<LinkToSave> array_list_of_linkconverted = DeviceManager.converterLink();


                //Et on sauvegarde le tout dans une bonne pitite marmite à feu doux.
                Actions.saveGUIGraph(file.getPath().toString(),DeviceManager.getArray_list_of_devices(),DeviceManager.getArray_list_of_links_hard(),array_list_of_imageconverted,array_list_of_linkconverted);





            } catch (BadCallException e) {
                DeviceManager.afficheError("Impossible to save");
            }
        }
    }

    public void SaveControleS (Stage primaryStage,AnchorPane anchorPane) {




        File file = DeviceManager.getCurrentFile();


        //Si le fichier est valide
        if (file != null) {

            try {

                //On convertie les images et les liens
                ArrayList<ImageToSave> array_list_of_imageconverted = DeviceManager.converterImageView();
                ArrayList<LinkToSave> array_list_of_linkconverted = DeviceManager.converterLink();


                //Et on sauvegarde le tout dans une bonne pitite marmite à feu doux.
                Actions.saveGUIGraph(file.getPath().toString(),DeviceManager.getArray_list_of_devices(),DeviceManager.getArray_list_of_links_hard(),array_list_of_imageconverted,array_list_of_linkconverted);





            } catch (BadCallException e) {
                DeviceManager.afficheError("Impossible to save");
            }
        }
    }




}
