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
import actions.HardwareFactory;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import hardware.AbstractHardware;
import hardware.Link;
import hardware.client.AbstractClient;
import hardware.router.AbstractRouter;
import hardware.router.WANPort;
import hardware.server.AbstractServer;
import hardware.switchs.AbstractSwitch;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import packet.IP;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.nio.channels.NotYetConnectedException;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Classe de méthodes d'ajout et de gestion d'appareils (bourrée de lambdas)
 */
public class DeviceManager {

    /**
     * Variable pour le port Wan
     */
    private static int WAN = 0;
    public static int getWAN() {
        return WAN;
    }

    public static void setWAN(int WAN_in) {
        WAN=WAN_in;
    }



    /**
     * Variable pour le zoom
     */
    private static double zoomScale = 1;
    public static double getZoomScale() {
        return zoomScale;
    }
    public static void setZoomScale(double zoomin) {
        zoomScale=zoomin;
    }
    private static Slider zoomSlider;
    public static void setZoomSlider(Slider sliderIn) {
        zoomSlider=sliderIn;
    }


    private static TextField zoomField;
    public static void setZoomField(TextField fieldIn) {
        zoomField=fieldIn;
    }

    public static void resetzoomScale() {
        zoomScale=1;
        zoomSlider.setValue(1);
        zoomField.setText("1");
    }

    public static boolean graphic = false;


    /**
     * Thread actuelle
     * */

    public static Thread mainThread = null;

    /***
     * Pour la on off ité
     */

    private static boolean isPaused = true;
    public static boolean getIsPause() {
        return isPaused;
    }
    public static void setIsPause(boolean boo) {
        isPaused=boo;
    }


    /**
     * Liste des images errors
     */
    private static ArrayList<ImageView> arrayList_of_error = new ArrayList<>();
    public static ArrayList<ImageView> getArrayList_of_error () {
        return arrayList_of_error;
    }
    public static void setArrayList_of_error(ArrayList<ImageView> arrayList_of_error_in) {
        arrayList_of_error=arrayList_of_error_in;
    }

    /**
     * Liste pour les triangles d'erreur
     */
    private static ArrayList<Integer> arrayListOfStateError = new ArrayList<>();
    public static ArrayList<Integer> getArrayListOfStateError() {return arrayListOfStateError;}
    public static void setArrayListOfStateError(ArrayList<Integer> arrayListOfStateErrorIn) {
        arrayListOfStateError=arrayListOfStateErrorIn;
    }

    /**
     * Liste des positions absolues
     */
    private static ArrayList<Double> arrayListOfX = new ArrayList<>();
    public static ArrayList<Double> getArrayListOfX() {
        return arrayListOfX;
    }
    public static void setArrayListOfX(ArrayList<Double> arrayIn) {
        arrayListOfX=arrayIn;
    }

    private static ArrayList<Double> arrayListOfY = new ArrayList<>();
    public static ArrayList<Double> getArrayListOfY() {
        return arrayListOfY;
    }
    public static void setArrayListOfY(ArrayList<Double> arrayIn) {
        arrayListOfY=arrayIn;
    }

    /**
     * Liste des états
     */

    private static ArrayList<Integer> arrayList_of_state= new ArrayList<Integer>();
    public static ArrayList<Integer> getArrayList_of_state()  {
        return arrayList_of_state;
    }
    public static void setArrayList_of_state(ArrayList<Integer> arrayList) {
        arrayList_of_state=arrayList;
    }

    /***
     *  Pour le message d'erreur
     */

    public static Stage errorStage = new Stage();


    private static AnchorPane errorPane = new AnchorPane();

    private static Text errortext = new Text("Error : ");
    public static void initErrorStage() {
        errorPane.setPrefHeight(50);
        errorPane.setPrefWidth(400);

        Scene errorScene = new Scene(errorPane, 400, 50);


        errorStage.setTitle("Error");
        errorStage.setScene(errorScene);
        errorStage.sizeToScene();

        errorStage.getIcons().add(new Image("file:sprites/Error.png"));


        errorPane.setStyle("-fx-border-color: black;-fx-background-color: white ;");



        errortext.setX(10);
        errortext.setY(20);
        errorPane.getChildren().add(errortext);


        errorStage.setOnCloseRequest(event -> {

                errorStage.close();
                bugmode=false;
                //TODO relancer la simu


        });

    }


    /**
     * Pour la fenetre de la verbose
     */

    public static Stage verboseStage = new Stage();

    private static Text verboseText = new Text("Verbose :\n");

    //private static ScrollPane verbosePane = new ScrollPane();



    public static void initVerboseStage() {

        AnchorPane anchorPane = new AnchorPane();
        //verbosePane.setContent(anchorPane);
        anchorPane.getChildren().add(verboseText);

        //verbosePane.setPrefHeight(780);
        //verbosePane.setPrefWidth(580);

        //Scene verboseScene = new Scene(verbosePane, 600, 800);
        Scene verboseScene = new Scene(anchorPane, 600, 800);

        verboseStage.setTitle("Verbose");
        verboseStage.setScene(verboseScene);
        verboseStage.sizeToScene();

        verboseStage.getIcons().add(new Image("file:sprites/arjl-logo.png"));

        verboseText.setLayoutX(10);
        verboseText.setLayoutY(20);







        verboseStage.setOnCloseRequest(event -> {

            if(bugmode) {
                event.consume();
            }


        });


        PrintStream origOut = System.out;
        PrintStream interceptor = new Interceptor(origOut);
        System.setOut(interceptor);



    }

    public static void addLineVerbose(String str) throws InterruptedException {
        synchronized(mainThread) {

            ;

            String text = verboseText.getText();

            text = str + "\n" + text;
            String finalText = text;
            Platform.runLater(() -> verboseText.setText(finalText));



            //verbosePane.setVvalue(1);

        }




    }

    private static int entier=0;


    /**
     * Fichier Actuel
     */

    private static File currentFile;
    public static void setCurrentFile(File file,Stage primaryStage) {
        currentFile=file;
        primaryStage.setTitle("ARJL : " + file.getPath());
    }
    public static File getCurrentFile() {
        return currentFile;
    }


    /**
    * Booléen pour dire que l'on est dans la phase de loading et bloquer la création de la partie hard
     */
    private static Boolean loadmode = false;

    public static Boolean getLoadmode() {
        return loadmode;
    }


        public static void setLoadmode (Boolean loadmode_in) {
        loadmode=loadmode_in;


    }

    /**
     * Booléen pour eviter le bug de thread d'actualisation
     */
    private static Boolean createmode = false;

    public static Boolean getCreatemode() {
        return createmode;
    }


    public static void setCreatemode (Boolean createmode_in) {
        createmode=createmode_in;


    }



    /**
     * Booléen pour bloquer le programme en cas de bug
     */

    private static Boolean bugmode = false;

    public static boolean is_bugging() {
            return bugmode;
    }

    public static HardwareFactory factory = new HardwareFactory();


    /**
     * Liste des appareils (L'objet n est en nième position dans toutes les listes)
     */

    private static ArrayList <ImageView> array_list_of_image = new ArrayList <ImageView> (); // sous forme d'image



    public static ArrayList <ImageView> getArray_list_of_image() {
        return array_list_of_image;
    }
    public static void setArray_list_of_image(ArrayList <ImageView> array_list_of_image_in) {
        array_list_of_image=array_list_of_image_in;

    }

    /**
     * // sous forme de hardware
     */
    private static ArrayList <AbstractHardware> array_list_of_devices = new ArrayList();

    public static ArrayList <AbstractHardware> getArray_list_of_devices() {
        return array_list_of_devices;
    }

    public static void setArray_list_of_devices(ArrayList<Object> array_list_of_devices_in) {
        array_list_of_devices=new ArrayList<>();
        array_list_of_devices_in.forEach(object -> {
            array_list_of_devices.add((AbstractHardware) object);
        });

    }
    /**
     * Liste des fenêtres de info
     */

    private static ArrayList <AnchorPane> arrayList_of_infoMenu = new ArrayList<> ();
    public static ArrayList <AnchorPane> getArrayList_of_infoMenu() {
        return arrayList_of_infoMenu;
    }
    public static void setArrayList_of_infoMenu(ArrayList <AnchorPane> arrayList_of_infoMenu_in) {
        arrayList_of_infoMenu =arrayList_of_infoMenu_in;
    }

    /**
     *  Liste des fenetres de config
     */
    private static ArrayList <Stage> arrayList_of_configStage = new ArrayList<>();
    public static ArrayList <Stage> getArrayList_of_configStage() {
        return arrayList_of_configStage;
    }
    public static void setArrayList_of_configStage(ArrayList <Stage> arrayList_of_configStage_in) {
        arrayList_of_configStage=arrayList_of_configStage_in;
    }
    public static void closeEvryConfigStage() {
        for (Stage stage:arrayList_of_configStage) {
            if(stage.isShowing()) {
                stage.close();
            }
        }
    }

    /**
     *  Liste des fenetres de repetitive requete
     */
    private static ArrayList <Stage> arrayList_of_requestStage = new ArrayList<>();
    public static ArrayList <Stage> getArrayList_of_requestStage() {
        return arrayList_of_requestStage;
    }
    public static void setArrayList_of_requestStage(ArrayList <Stage> arrayList_of_requestStage_in) {
        arrayList_of_requestStage=arrayList_of_requestStage_in;
    }
    public static void closeEvryRequestStage() {
        for (Stage stage:arrayList_of_requestStage) {
            if(stage.isShowing()) {
                stage.close();
            }
        }
    }

    /**
     * Liste des DHCPStage
     */
    private static ArrayList<Stage> arrayList_of_stageDHCP = new ArrayList<>();
    public static ArrayList <Stage> getarrayList_of_stageDHCP() {
        return arrayList_of_stageDHCP;
    }
    public static void setarrayList_of_stageDHCP(ArrayList <Stage> arrayList_of_stageDHCP_in) {
        arrayList_of_stageDHCP=arrayList_of_stageDHCP_in;
    }
    public static void closeEvryDHCPStage() {
        for (Stage stage:arrayList_of_stageDHCP) {
            if(stage.isShowing()) {
                stage.close();
            }
        }
    }


    /**
     * Même chose pour les barres d'affichage de la charge
     */
    private static ArrayList <ProgressBar> arrayList_of_progressBar = new ArrayList<> ();
    public static ArrayList <ProgressBar> getArrayList_of_progressBar() {
        return arrayList_of_progressBar;
    }
    public static void setArrayList_of_progressBar(ArrayList <ProgressBar> arrayList_of_progressBar_in) {
        arrayList_of_progressBar=arrayList_of_progressBar_in;
    }


    /**
     * Meme chose pour les barres d'affichage d'avancement de la request
     */
    private static ArrayList <ProgressBar> arrayList_of_progressBarRequest = new ArrayList<> ();
    public static ArrayList <ProgressBar> getArrayList_of_progressBarRequest() {
        return arrayList_of_progressBarRequest;
    }
    public static void setArrayList_of_progressBarRequest(ArrayList <ProgressBar> arrayList_of_progressBarRequest_in) {
        arrayList_of_progressBarRequest=arrayList_of_progressBarRequest_in;
    }



    /**
     * Int définissant le type d'appareil
     */
    private static int modelepc = 0;
    public static void setModelepc(int a) {
        modelepc=a;
    }
    public static int getModelepc() {
        return modelepc;
    }

    private static int modeleserver = 0;
    public static void setModeleserver(int a) {
        modeleserver=a;
    }
    public static int getModeleserver() {
        return modeleserver;
    }

    private static int modelerouter = 0;
    public static void setModelerouter(int a) {
        modelerouter=a;
    }
    public static int getModelerouter() {
        return modelerouter;
    }

    private static int modeleswitch = 0;
    public static void setModeleswitch(int a) {
        modeleswitch=a;
    }
    public static int getModeleswitch() {
        return modeleswitch;
    }

    private static int modelehub = 0;
    public static void setModelehub(int a) {
        modelehub=a;
    }
    public static int getModelehub() {
        return modelehub;
    }


    /**
     * Les différentes images utilisées
     */
    private static Image imagepc = new Image("file:sprites/pack1/pc.png");
    private static Image imageserver = new Image("file:sprites/pack1/server.png");
    private static Image imagerouteur = new Image("file:sprites/pack1/router2.png");
    private static Image imageswitch = new Image("file:sprites/pack1/switch.png");
    private static Image imagehub = new Image("file:sprites/pack1/hub.png");
    private static Image imagewan = new Image("file:sprites/pack1/wan.png");
    private static Image imageerror = new Image("file:sprites/Error.png");

    private static Image imagepcselected = new Image("file:sprites/pack1/pc-selected.png");
    private static Image imageserverselected = new Image("file:sprites/pack1/server-selected.png");
    private static Image imagerouteurselected = new Image("file:sprites/pack1/router2-selected.png");
    private static Image imageswitchselected = new Image("file:sprites/pack1/switch-selected.png");
    private static Image imagehubselected = new Image("file:sprites/pack1/hub-selected.png");
    private static Image imagewanselected = new Image("file:sprites/pack1/wan-selected.png");



    public static void setpacktexture(int i) {

            String path = "file:sprites/pack" + i;
            imagepc = new Image(path+"/pc.png");
            imageserver = new Image(path+"/server.png");
            imagerouteur = new Image(path+"/router2.png");
            imageswitch = new Image(path+"/switch.png");
            imagehub = new Image(path+"/hub.png");
            imagewan = new Image(path+"/wan.png");

            imagepcselected = new Image(path+"/pc-selected.png");
            imageserverselected = new Image(path+"/server-selected.png");
            imagerouteurselected = new Image(path+"/router2-selected.png");
            imageswitchselected = new Image(path+"/switch-selected.png");
            imagehubselected = new Image(path+"/hub-selected.png");
            imagewanselected = new Image(path+"/wan-selected.png");





    }


    private static Image imagepipeau = new Image("file:sprites/pipeau.png");

    /**
     * Liste des liens
     */

    private static ArrayList <ElementOfLink> array_list_of_link = new ArrayList<>();
    private static ArrayList <Link> array_list_of_links_hard = new ArrayList();

    public static ArrayList <ElementOfLink> getArray_list_of_link() {
        return array_list_of_link;
    }

    public static void setArray_list_of_link(ArrayList <ElementOfLink> array_list_of_link_in) {
        array_list_of_link=array_list_of_link_in;
    }

    public static ArrayList <Link> getArray_list_of_links_hard() {
        return array_list_of_links_hard;
    }

    public static void setArray_list_of_links_hard(ArrayList<Object> array_list_of_links_hard_in) {
        array_list_of_links_hard=new ArrayList<>();
        array_list_of_links_hard_in.forEach(object -> {
            array_list_of_links_hard.add((Link) object);

        });
    }

    /**
     * I don't know what this is !!!!!!!
     * THIS! IS! pour lancer une requête dhcp sur tous les clients. (debug)
     * @throws OverflowException
     * @throws BadCallException
     * @author R. Dulong
     */
    public static void dhcpAll() throws OverflowException, BadCallException { // Lance une requete dhcp depuis tous les pc affichés.
        for(int i=0; i<array_list_of_devices.size(); i++){
            if(array_list_of_devices.get(i) instanceof AbstractClient){
                array_list_of_devices.get(i).setVerbose("John");
                ((AbstractRouter)array_list_of_devices.get(i)).DHCPClient(0);

            }
        }
    }


    /**
     * Button pour les liens multiples
     */
    private static Button lienmultiple = new Button("STOP");


    /**
     * Booleen indiquant si on est en mode DELETE
     */
    private static boolean supressmode = false;
    public static boolean getsupressmode () {
        return supressmode;
    }
    public static void setsupressmode (boolean supressmode_in) {
        supressmode=supressmode_in;
    }

    /**
     * Entier indiquant si on est en mode création de liens.
     * 0=non,
     * 1=oui et on a cliqué sur aucune image,
     * 2=oui et on a cliqué sur 1 image,
     * 3=oui et on a cliqu séur 2 images,
     * 4=on vient du MenuContextuelle,
     * 5=AntiBug,
     * 6=lien multiple et on a déjà une première image,
     * 7=lien multiple et on a pas encore la première image
     */
    private static int addlinkmode = 0;

    public static int getAddlinkmode() {
        return addlinkmode;
    }
    public static void setAddlinkmode(int x) {
        addlinkmode=x;
    }

    /**
     * Variable stockant les infos necessairent à la création de liens
     */

    private static ImageView firstimageView;
    private static double firstimagesizeX;
    private static double firstimagesizeY;
    private static ImageView secondimageView;
    private static double secondimagesizeX;
    private static double secondimagesizeY;

    public static void setFirstimageView(ImageView imageView) {
        firstimageView=imageView;
    }
    public static ImageView getFirstimageView() {
        return firstimageView;
    }

    public static void setFirstimagesizeX(double x) {
        firstimagesizeX=x;
    }
    public static double getFirstimagesizeX() {
        return firstimagesizeX;
    }
    public static void setFirstimagesizeY(double y) {
        firstimagesizeY=y;
    }
    public static double getFirstimagesizeY() {
        return firstimagesizeY;
    }

    public static void setSecondimageView(ImageView imageView) {
        secondimageView=imageView;
    }
    public static ImageView getSecondimageView() {
        return secondimageView;
    }

    public static void setSecondimagesizeX(double x) {
        secondimagesizeX=x;
    }
    public static double getSecondimagesizeX() {
        return secondimagesizeX;
    }
    public static void setSecondimagesizeY(double y) {
        secondimagesizeY=y;
    }
    public static double getSecondimagesizeY() {
        return secondimagesizeY;
    }

    /**
     * Fontion qui reset les fonctions de status
     * @param mode
     * @param status
     * @param content
     */
    public static void reset (String mode,Label status,AnchorPane content) {
        if(Objects.equals(mode, "")){
            status.setText("ON");
        }

        if (!Objects.equals(mode, "addlink")) {
            array_list_of_image.forEach(imageView -> {
                String str = imageView.getAccessibleText();
                if (str.contains("pc")) {
                    imageView.setImage(imagepc);
                } else if (str.contains("switch")) {
                    imageView.setImage(imageswitch);
                } else if (str.contains("server")) {
                    imageView.setImage(imageserver);
                } else if (str.contains("router")) {
                    imageView.setImage(imagerouteur);
                } else if (str.contains("hub")) {
                    imageView.setImage(imagehub);
                } else if (str.contains("wan")) {
                    imageView.setImage(imagewan);
                }

            });




            addlinkmode=0;
        }

        if (!Objects.equals(mode, "delete")) {

            supressmode = false ;
        }

        if(content.getChildren().contains(lienmultiple)) {
            content.getChildren().remove(lienmultiple);
        }
        if(firstimageView!=null) {
            if(firstimageView.getAccessibleText().equals("pc")) {
                firstimageView.setImage(imagepc);
            } else if (firstimageView.getAccessibleText().equals("server")){
                firstimageView.setImage(imageserver);
            } else if (firstimageView.getAccessibleText().equals("router")){
                firstimageView.setImage(imagerouteur);
            } else if (firstimageView.getAccessibleText().equals("switch")){
                firstimageView.setImage(imageswitch);
            } else if (firstimageView.getAccessibleText().equals("hub")){
                firstimageView.setImage(imagehub);
            } else if (firstimageView.getAccessibleText().equals("wan")){
                firstimageView.setImage(imagewan);
            }
        }

        Actualization.updateInfoEtBarre();
        Actualization.updateLinkDrag();

    }




    /**
     * fonction qui affiche le popup info
     */

    public static void affichePopupInfo(AnchorPane content, AnchorPane infoPane) {
        if( !content.getChildren().contains(infoPane)) {
            content.getChildren().add(infoPane);
        }
    }
    /**
     * fonction qui enlève le popup info
     */

    public static void enlevePopupConfiq (AnchorPane content, AnchorPane infoPane) {
        if( content.getChildren().contains(infoPane)) {
            content.getChildren().remove(infoPane);
        }
    }

    /**
     * Fenetre de pop up de message d'erreur
     * @param str        Message d'erreur à afficher
     */
    public static void afficheError(String str) {
        //TODO Pausé la simu
        bugmode=true;

        Toolkit.getDefaultToolkit().beep();

        errortext.setText("Error : " + str);

        errorStage.show();









    }





    /**
     * Fonction qui arrête le thread des appareils
     */
    public static void stopThread() {
        for(AbstractHardware device : array_list_of_devices) {
            device.stop();
        }
    }
    /**
     * Fonction qui démare le thread des appareils
     */
    public static void startThread() {
        for(AbstractHardware device : array_list_of_devices) {
            device.start();
        }
    }





    /**
     * Fonction du zoom
     */

    public static void zoomAll() {

        int n = array_list_of_image.size();

        for(int i = 0;i<n;i++) {
            ImageView imageView = array_list_of_image.get(i);
            imageView.setX(arrayListOfX.get(i)*zoomScale);
            imageView.setY(arrayListOfY.get(i)*zoomScale);
            imageView.setScaleX(zoomScale);
            imageView.setScaleY(zoomScale);

        }

        Actualization.updateLinkDrag();
    }


    /**
     * Fonction créatrice, on peut le dire c'est Dieu. Alleluia
     * @param device         Type de l'appareil
     * @param anchorPane
     * @param scrollPane
     * @param content
     * @param status
     * @throws exceptions.BadCallException
     */
    public static void device(String device,AnchorPane anchorPane, ScrollPane scrollPane, AnchorPane content,Label status,Stage primaryStage) throws exceptions.BadCallException {
        if (!bugmode) {

            createmode=true;

            reset("", status, content);
        /*
            Ajout de l'image à l'interface
         */
            ImageView imageView = new ImageView();



            /**
             * On stocke le type dans le accessibleText
             */
            imageView.setAccessibleText(device);


            Image image;
            image = imageerror;
            imageView.setImage(image);
            //Variable d'états
            double difxi = content.getWidth() - anchorPane.getPrefWidth();
            double relxi = scrollPane.getHvalue() * difxi;
            double difyi = content.getHeight() - anchorPane.getPrefHeight();
            double relyi = scrollPane.getVvalue() * difyi;
            imageView.setX(200 + relxi);
            imageView.setY(200 + relyi);                                                //Crée et affiche un pc
            content.getChildren().add(imageView);

            array_list_of_image.add(imageView);



            Image imageError = new Image("file:sprites/alert.png");
            ImageView imageViewError = new ImageView();
            imageViewError.setImage(imageError);
            content.getChildren().add(imageViewError);
            imageViewError.toFront();



            arrayList_of_error.add(imageViewError);
            arrayListOfStateError.add(new Integer(0));

            if (device.contains("pc")) {
                image = imagepc;
                imageView.setImage(image);
                if (!loadmode) {
                    if(device.contains("0")) {
                        array_list_of_devices.add(factory.newStandardPC());
                    }

                    AbstractClient abstractClient = (AbstractClient) array_list_of_devices.get(array_list_of_devices.size() - 1);


                    array_list_of_devices.get(array_list_of_devices.size() - 1).start();
                }
                if(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).isRunning()) {
                    arrayList_of_state.add(new Integer(1));
                } else {
                    arrayList_of_state.add(new Integer(0));
                }
            } else if (device.contains("server")) {
                image = imageserver;
                imageView.setImage(image);
                if (!loadmode) {
                    if(device.contains("0")) {

                        array_list_of_devices.add(factory.newDHCPServer());
                    } else if (device.contains("1")) {

                        array_list_of_devices.add(factory.newStandardFTPServer());
                    } else if (device.contains("2")) {

                        array_list_of_devices.add(factory.newStandardWEBServer());
                    }

                    array_list_of_devices.get(array_list_of_devices.size() - 1).start();
                }
                if(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).isRunning()) {
                    arrayList_of_state.add(new Integer(1));
                } else {
                    arrayList_of_state.add(new Integer(0));
                }
            } else if (device.contains("router")) {
                image = imagerouteur;
                imageView.setImage(image);
                if (!loadmode) {
                    if(device.contains("0")) {
                        array_list_of_devices.add(factory.newStandard2ETHRouter());
                    } else if(device.contains("1")) {
                        array_list_of_devices.add(factory.newCiscoCRS1Router());
                    } else if(device.contains("2")) {
                        array_list_of_devices.add(factory.newCisco2811Router());
                    }


                    array_list_of_devices.get(array_list_of_devices.size() - 1).start();


                }
                if(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).isRunning()) {
                    arrayList_of_state.add(new Integer(1));
                } else {
                    arrayList_of_state.add(new Integer(0));
                }
            } else if (device.contains("switch")) {
                image = imageswitch;
                imageView.setImage(image);
                if (!loadmode) {
                    if(device.contains("0")) {
                        array_list_of_devices.add(factory.newStandard24ETHSwitch());
                    } else if(device.contains("1")) {
                        array_list_of_devices.add(factory.newAvayaERS2550T50ETHSwitch());
                    } else if(device.contains("2")) {
                        array_list_of_devices.add(factory.newNetgearM4100D12G12ETHSwitch());
                    }

                    array_list_of_devices.get(array_list_of_devices.size() - 1).start();
                }
                if(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).isRunning()) {
                    arrayList_of_state.add(new Integer(1));
                } else {
                    arrayList_of_state.add(new Integer(0));
                }
            } else if (device.contains("hub")) {
                image = imagehub;
                imageView.setImage(image);
                if (!loadmode) {
                    if(device.contains("0")) {
                        array_list_of_devices.add(factory.newStandard24ETHHub());
                    } else if(device.contains("1")) {
                        array_list_of_devices.add(factory.newLinkBuilder3Com12ETHHub());
                    } else if(device.contains("2")) {
                        array_list_of_devices.add(factory.newCentreComMR820TR8ETHHub());
                    }

                    array_list_of_devices.get(array_list_of_devices.size() - 1).start();
                }
                if(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).isRunning()) {
                    arrayList_of_state.add(new Integer(1));
                } else {
                    arrayList_of_state.add(new Integer(0));
                }
            } else if (device.contains("wan")) {
                image = imagewan;
                imageView.setImage(image);

                if(WAN == 0) {
                    WAN = 1;
                } else {

                    WAN = 2;
                }

                if (!loadmode) {

                    if(WAN==1) {
                        array_list_of_devices.add(factory.newWANPort());


                        array_list_of_devices.get(array_list_of_devices.size() - 1).start();

                    }

                }
                if(WAN!=2) {
                    if (array_list_of_devices.get(array_list_of_image.indexOf(imageView)).isRunning()) {
                        arrayList_of_state.add(new Integer(1));
                    } else {
                        arrayList_of_state.add(new Integer(0));
                    }
                }
            }


            if(WAN!=2) {

                imageView.preserveRatioProperty().set(true);

                arrayListOfX.add(imageView.getX() / zoomScale);
                arrayListOfY.add(imageView.getY() / zoomScale);

                //Placement de l'image error
                imageViewError.setX(-100);
                imageViewError.setY(-100);


                //Surnom de l'appareil
                String surname = "";
                if (!loadmode) {
                    surname = array_list_of_devices.get(array_list_of_image.indexOf(imageView)).getName() + " : " + device;
                    if(device.contains("wan")) {
                        surname = "WANPort";
                    }
                    array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname = surname;

                } else {
                    surname = array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname;
                }



        /*
            Menu info de l'appareil
         */
                AnchorPane infoAnchorPane = new AnchorPane();
                arrayList_of_infoMenu.add(infoAnchorPane);
                infoAnchorPane.setPrefHeight(10);
                infoAnchorPane.setPrefWidth(10);
                infoAnchorPane.setStyle("-fx-border-color: black;-fx-background-color: white ;");

                ProgressBar progressBar = new ProgressBar();
                arrayList_of_progressBar.add(progressBar);
                progressBar.setPrefWidth(imageView.getImage().getWidth());
                progressBar.setPrefHeight(1);
                progressBar.setProgress(0);

                ProgressBar progressBarRequest = new ProgressBar();
                arrayList_of_progressBarRequest.add(progressBarRequest);
                progressBarRequest.setPrefWidth(imageView.getImage().getWidth());
                progressBarRequest.setPrefHeight(1);
                progressBarRequest.setProgress(0);


                Text text1;
                if (device.contains("pc")) {
                    if (device.contains("0")) {
                        text1 = new Text("PC Standard");
                    } else {
                        text1 = new Text("Error");
                    }

                } else if (device.contains("server")) {
                    if (device.contains("0")) {
                        text1 = new Text("Server DHCP");
                    } else if (device.contains("1")) {
                        text1 = new Text("Server FTP");
                    } else if (device.contains("2")) {
                        text1 = new Text("Server WEB");
                    } else {
                        text1 = new Text("Error");
                    }

                } else if (device.contains("router")) {
                    if (device.contains("0")) {
                        text1 = new Text("Router 2 Ports ETH");
                    } else if (device.contains("1")) {
                        text1 = new Text("Router CISCO CRS1 12 Ports ETH");
                    } else if (device.contains("2")) {
                        text1 = new Text("Router CISCO 2811 2 Ports ETH 1 Port SERIAL");
                    } else {
                        text1 = new Text("Error");
                    }

                } else if (device.contains("switch")) {
                    if (device.contains("0")) {
                        text1 = new Text("Switch 24 Ports");
                    } else if (device.contains("1")) {
                        text1 = new Text("Switch Avaya ERS2550T");
                    } else if (device.contains("2")) {
                        text1 = new Text("Switch Netgear M4100D12G");
                    } else {
                        text1 = new Text("Error");
                    }
                } else if (device.contains("hub")) {
                    if (device.contains("0")) {
                        text1 = new Text("Hub Standard 24 Ports ETH");
                    } else if (device.contains("1")) {
                        text1 = new Text("Hub Link Builder 3Com");
                    } else if (device.contains("2")) {
                        text1 = new Text("Hub CentreCom MR820TR");
                    } else {
                        text1 = new Text("Error");
                    }
                } else if (device.contains("wan")) {

                    text1 = new Text("");

                } else {
                    text1 = new Text("Error");
                }
                text1.setX(5);
                text1.setY(15);

                infoAnchorPane.getChildren().add(text1);


                imageView.setOnMouseEntered(event -> {
                    if (!bugmode) {
                        infoAnchorPane.setLayoutX(imageView.getX() + imageView.getImage().getWidth());
                        infoAnchorPane.setLayoutY(imageView.getY());

                        affichePopupInfo(content, infoAnchorPane);


                    }
                });

                imageView.setOnMouseExited(event -> {


                    enlevePopupConfiq(content, infoAnchorPane);


                });


                //Item du menu contextuelle deplacé ici car on lui fait référence juste en dessous


                MenuItem itemOnOff = new MenuItem("ON");
                itemOnOff.setOnAction(event -> {
                    reset("", status, content);

                    if (arrayList_of_state.get(array_list_of_image.indexOf(imageView)).equals(1)) {
                        if (!isPaused) {
                            array_list_of_devices.get(array_list_of_image.indexOf(imageView)).changeState(false);
                        }
                        arrayList_of_state.set(array_list_of_image.indexOf(imageView), new Integer(0));


                        itemOnOff.setText("ON");
                        System.out.println(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname + " : I am OFF now");
                    } else {
                        if (!isPaused) {
                            array_list_of_devices.get(array_list_of_image.indexOf(imageView)).changeState(true);
                        }
                        arrayList_of_state.set(array_list_of_image.indexOf(imageView), new Integer(1));

                        itemOnOff.setText("OFF");
                        System.out.println(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname + " : I am ON now");
                    }
                    Actualization.updateInfoMenu();

                });

                //On reviens à la fenetre de info

                Text on_off = new Text();


                if (array_list_of_devices.get(array_list_of_image.indexOf(imageView)).isRunning()) {
                    on_off.setText("Power : " + "ON");
                    itemOnOff.setText("OFF");
                } else {
                    on_off.setText("Power : " + "OFF");
                    itemOnOff.setText("ON");
                }

                on_off.setX(5);
                on_off.setY(45);
                if(device.contains("wan")) {
                    on_off.setY(30);
                }
                infoAnchorPane.getChildren().add(on_off);


                Text linkstate = new Text("Free ports : " + array_list_of_devices.get(array_list_of_image.indexOf(imageView)).getFreePorts().size());
                linkstate.setX(5);
                linkstate.setY(75);
                if(device.contains("wan")) {
                    linkstate.setY(60);
                }
                infoAnchorPane.getChildren().add(linkstate);


                Text isverboseactive = new Text("Verbose : OFF");
                isverboseactive.setX(5);
                isverboseactive.setY(60);
                if(device.contains("wan")) {
                    isverboseactive.setY(45);
                }
                infoAnchorPane.getChildren().add(isverboseactive);

                Text surnom = new Text(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname);
                surnom.setX(5);
                surnom.setY(30);
                if(device.contains("wan")) {
                    surnom.setY(15);
                }
                infoAnchorPane.getChildren().add(surnom);






        /*
             Fenetre de configuration
         */
                Stage configStage = new Stage();

                configStage.setOnCloseRequest(event -> {
                    if (bugmode) {
                        event.consume();
                    }
                });

                ScrollPane configScroll = new ScrollPane();
                AnchorPane configPane = new AnchorPane();

                configPane.setOnMouseClicked(event -> {

                    if (DeviceManager.is_bugging()) {
                        Toolkit.getDefaultToolkit().beep();
                        DeviceManager.errorStage.toFront();
                    }
                });


                configScroll.setContent(configPane);
                Scene configScene = null;


                if(device.contains("router")) {
                    configScene = new Scene(configScroll, 700, 500);
                } else if (device.contains("server0")) {
                    configScene = new Scene(configScroll, 560, 500);
                } else {
                    configScene = new Scene(configScroll, 500, 500);
                }

                configPane.setPrefWidth(480);
                if(device.contains("router")) {
                    configPane.setPrefWidth(680);
                }
                configPane.setPrefHeight(480);
                configStage.setTitle(surname);
                configStage.setScene(configScene);
                configStage.sizeToScene();

                configStage.getIcons().add(new Image("file:sprites/arjl-logo.png"));

                arrayList_of_configStage.add(configStage);

                MenuItem itemConfig = new MenuItem("Configuration");


                if (device.contains("pc")) {
                    Button actualiseButton = new Button("Actualise");
                    actualiseButton.setLayoutX(5);
                    actualiseButton.setLayoutY(0);
                    configPane.getChildren().add(actualiseButton);


                    AbstractClient abstractClient = (AbstractClient) array_list_of_devices.get(array_list_of_image.indexOf(imageView));

                    ArrayList<Object> actualiseGraph = new ArrayList<>();

                    itemConfig.setOnAction(event -> {
                        reset("", status, content);
                        try {
                            MenuConfig.updateConfigMenuPc(configStage, configPane, actualiseGraph, abstractClient, surnom);
                        } catch (BadCallException e) {
                            e.printStackTrace();
                        }
                        if (!configStage.isShowing()) {
                            configStage.show();
                        } else {
                            configStage.toFront();
                        }


                    });
                    MenuConfig.updateConfigMenuPc(configStage, configPane, actualiseGraph, abstractClient, surnom);


                    actualiseButton.setOnAction(event -> {
                        if (!bugmode) {
                            try {
                                MenuConfig.updateConfigMenuPc(configStage, configPane, actualiseGraph, abstractClient, surnom);
                            } catch (BadCallException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                }  else if (device.contains("router")) {

                    Button actualiseButton = new Button("Actualise");
                    actualiseButton.setLayoutX(5);
                    actualiseButton.setLayoutY(0);
                    configPane.getChildren().add(actualiseButton);

                    AbstractRouter abstractRouter = (AbstractRouter) array_list_of_devices.get(array_list_of_image.indexOf(imageView));

                    ArrayList<Object> actualiseGraph = new ArrayList<>();
                    itemConfig.setOnAction(event -> {
                        reset("", status, content);

                        MenuConfig.updateConfigMenuRouter(configStage, configPane, actualiseGraph, abstractRouter, surnom);

                        if (!configStage.isShowing()) {
                            configStage.show();
                        } else {
                            configStage.toFront();
                        }


                    });

                    MenuConfig.updateConfigMenuRouter(configStage, configPane, actualiseGraph, abstractRouter, surnom);

                    actualiseButton.setOnAction(event -> {
                        if (!bugmode) {
                            MenuConfig.updateConfigMenuRouter(configStage, configPane, actualiseGraph, abstractRouter, surnom);

                        }
                    });


                } else if (device.contains("server")) {

                    Button actualiseButton = new Button("Actualise");
                    actualiseButton.setLayoutX(5);
                    actualiseButton.setLayoutY(0);
                    configPane.getChildren().add(actualiseButton);

                    AbstractServer abstractServer = (AbstractServer) array_list_of_devices.get(array_list_of_image.indexOf(imageView));

                    ArrayList<Object> actualiseGraph = new ArrayList<>();

                    itemConfig.setOnAction(event -> {
                        reset("", status, content);

                        try {
                            MenuConfig.updateConfigMenuServer(configStage, configPane, actualiseGraph, abstractServer, surnom);
                        } catch (BadCallException e) {
                            e.printStackTrace();
                        }

                        if (!configStage.isShowing()) {
                            configStage.show();
                        } else {
                            configStage.toFront();
                        }


                    });

                    MenuConfig.updateConfigMenuServer(configStage, configPane, actualiseGraph, abstractServer, surnom);


                    actualiseButton.setOnAction(event -> {
                        if (!bugmode) {
                            try {
                                MenuConfig.updateConfigMenuServer(configStage, configPane, actualiseGraph, abstractServer, surnom);
                            } catch (BadCallException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (device.contains("wan")) {

                    Button actualiseButton = new Button("Actualise");
                    actualiseButton.setLayoutX(5);
                    actualiseButton.setLayoutY(0);
                    configPane.getChildren().add(actualiseButton);

                    WANPort wanport = (WANPort) array_list_of_devices.get(array_list_of_image.indexOf(imageView));

                    ArrayList<Object> actualiseGraph = new ArrayList<>();

                    itemConfig.setOnAction(event -> {
                        reset("", status, content);

                        MenuConfig.updateConfigMenuWAN(configStage, configPane, actualiseGraph, wanport, surnom);


                        if (!configStage.isShowing()) {
                            configStage.show();
                        } else {
                            configStage.toFront();
                        }


                    });

                    MenuConfig.updateConfigMenuWAN(configStage, configPane, actualiseGraph, wanport, surnom);


                    actualiseButton.setOnAction(event -> {
                        if (!bugmode) {
                            MenuConfig.updateConfigMenuWAN(configStage, configPane, actualiseGraph, wanport, surnom);
                        }
                    });
                }else {
                    Text errorText = new Text("Error");
                    configPane.getChildren().add(errorText);
                }









        /*
            Initialisation du menu contextuel
         */
                ContextMenu contextMenu = new ContextMenu();

                MenuItem itemDelete = new MenuItem("Delete");
                itemDelete.setOnAction(event -> {

                    if (content.getChildren().contains(infoAnchorPane)) {
                        content.getChildren().remove(infoAnchorPane);
                    }
                    if (content.getChildren().contains(progressBar)) {
                        content.getChildren().remove(progressBar);
                    }
                    if (content.getChildren().contains(progressBarRequest)) {
                        content.getChildren().remove(progressBarRequest);
                    }

                    try {
                        enlevePopupConfiq(content, infoAnchorPane);
                        Supress.suprimerImage(imageView, content);
                    } catch (BadCallException e) {
                        e.printStackTrace();
                    }
                    reset("", status, content);

                });
                MenuItem itemLink = new MenuItem("Create Link");

                itemLink.setOnAction(event -> {
                    reset("addlink", status, content);
                    firstimageView = imageView;
                    firstimagesizeX = imageView.getImage().getWidth();
                    firstimagesizeY = imageView.getImage().getHeight();
                    addlinkmode = 4;
                    status.setText("addLink 4");
                    if (firstimageView.getAccessibleText().contains("pc")) {
                        firstimageView.setImage(imagepcselected);
                    } else if (firstimageView.getAccessibleText().contains("server")) {
                        firstimageView.setImage(imageserverselected);
                    } else if (firstimageView.getAccessibleText().contains("router")) {
                        firstimageView.setImage(imagerouteurselected);
                    } else if (firstimageView.getAccessibleText().contains("switch")) {
                        firstimageView.setImage(imageswitchselected);
                    } else if (firstimageView.getAccessibleText().contains("hub")) {
                        firstimageView.setImage(imagehubselected);
                    } else if (firstimageView.getAccessibleText().contains("wan")) {
                        firstimageView.setImage(imagewanselected);
                    }

                });


                MenuItem itemMultipleLink = new MenuItem("Create Multiple Link");
                lienmultiple.setOnAction(event -> {
                    addlinkmode = 0;
                    reset("", status, content);
                    content.getChildren().remove(lienmultiple);

                });
                itemMultipleLink.setOnAction(event -> {
                    reset("addlink", status, content);
                    firstimageView = imageView;
                    firstimagesizeX = imageView.getImage().getWidth();
                    firstimagesizeY = imageView.getImage().getHeight();
                    addlinkmode = 6;
                    status.setText("addLink 6");
                    lienmultiple.setLayoutX(imageView.getX());
                    lienmultiple.setLayoutY(imageView.getY());
                    content.getChildren().add(lienmultiple);
                    if (firstimageView.getAccessibleText().contains("pc")) {
                        firstimageView.setImage(imagepcselected);
                    } else if (firstimageView.getAccessibleText().contains("server")) {
                        firstimageView.setImage(imageserverselected);
                    } else if (firstimageView.getAccessibleText().contains("router")) {
                        firstimageView.setImage(imagerouteurselected);
                    } else if (firstimageView.getAccessibleText().contains("switch")) {
                        firstimageView.setImage(imageswitchselected);
                    } else if (firstimageView.getAccessibleText().contains("hub")) {
                        firstimageView.setImage(imagehubselected);
                    } else if (firstimageView.getAccessibleText().contains("wan")) {
                        firstimageView.setImage(imagewanselected);
                    }

                });


                MenuItem itemDeleteLink = new MenuItem("Delete Link(s)");
                itemDeleteLink.setOnAction(event -> {
                    reset("", status, content);
                    ArrayList<ElementOfLink> list_element = new ArrayList<>();

                    int n = array_list_of_link.size();
                    for (int i = 0; i < n; ++i) {
                        ElementOfLink element = array_list_of_link.get(i);
                        if (element.img1 == imageView) {
                            list_element.add(element);
                        }
                        if (element.img2 == imageView) {
                            list_element.add(element);
                        }

                    }

                    n = list_element.size();
                    for (int i = 0; i < n; ++i) {
                        try {
                            Supress.suprimerLien(list_element.get(i).line, content, status);
                        } catch (BadCallException e) {
                            e.printStackTrace();
                        }

                    }

                });

                MenuItem itemCharge = new MenuItem("Show charges");
                itemCharge.setOnAction(event -> {
                    reset("", status, content);
                    progressBar.setLayoutX(imageView.getX());
                    progressBar.setLayoutY(imageView.getY() + imageView.getImage().getHeight());
                    progressBar.setPrefWidth(imageView.getImage().getWidth());
                    progressBar.setPrefHeight(1);
                    if (content.getChildren().contains(progressBar)) {
                        content.getChildren().remove(progressBar);
                    } else {
                        content.getChildren().add(progressBar);
                        progressBar.setProgress(0);

                    }
                });
                MenuItem itemVerbose = new MenuItem("Activate Verbose");

                itemVerbose.setOnAction(event -> {
                    reset("", status, content);
                    if (itemVerbose.getText().equals("Activate Verbose")) {
                        itemVerbose.setText("Desactivate Verbose");
                        isverboseactive.setText("Verbose : ON");
                        array_list_of_devices.get(array_list_of_image.indexOf(imageView)).setVerbose();
                        System.out.println(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname
                                + " : I am speaking to you");

                    } else {
                        itemVerbose.setText("Activate Verbose");
                        isverboseactive.setText("Verbose : OFF");
                        array_list_of_devices.get(array_list_of_image.indexOf(imageView)).stopVerbose();
                        System.out.println(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname
                                + " : I will no longer speak to you");
                    }


                });
                contextMenu.getItems().addAll(itemOnOff, itemVerbose, itemCharge);

                if (device.contains("pc")) {
                    MenuItem itemRequestA = new MenuItem("Request Progress");
                    itemRequestA.setOnAction(event -> {
                        reset("", status, content);
                        progressBarRequest.setProgress(0);

                        progressBarRequest.setPrefWidth(imageView.getImage().getWidth());

                        if (content.getChildren().contains(progressBarRequest)) {
                            content.getChildren().remove(progressBarRequest);
                        } else {
                            content.getChildren().add(progressBarRequest);
                            progressBarRequest.setLayoutX(imageView.getX());
                            progressBarRequest.setLayoutY(imageView.getY() - 13);


                        }
                    });
                    contextMenu.getItems().add(itemRequestA);
                }





                if (device.contains("pc") | device.contains("server") | device.contains("router") | device.contains("wan") ) {
                    contextMenu.getItems().addAll(itemConfig);
                }
                if (device.contains("switch")) {
                    MenuItem itemCleanTable = new MenuItem("Clean Table");
                    itemCleanTable.setOnAction(event -> {
                        AbstractSwitch abstractSwitch = (AbstractSwitch) array_list_of_devices.get(array_list_of_image.indexOf(imageView));
                        abstractSwitch.resetTable();
                        System.out.println(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname + " : Table Cleared");
                    });
                    contextMenu.getItems().addAll(itemCleanTable);
                }
                if (device.contains("router") | device.contains("pc") | device.contains("server")) {
                    MenuItem itemClearRoute = new MenuItem("Clear Route");
                    itemClearRoute.setOnAction(event -> {
                        AbstractRouter abstractRouter = (AbstractRouter) array_list_of_devices.get(array_list_of_image.indexOf(imageView));
                        abstractRouter.clearRoutes();
                        System.out.println(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname + " : Routes Cleared");
                    });
                    contextMenu.getItems().addAll(itemClearRoute);
                }
                if (device.contains("router")) {
                    MenuItem itemRIP = new MenuItem("RIP");
                    itemRIP.setOnAction(event -> {
                        AbstractRouter abstractRouter = (AbstractRouter) array_list_of_devices.get(array_list_of_image.indexOf(imageView));
                        if(!abstractRouter.RIP) {
                            abstractRouter.activateRIP();
                            itemRIP.setText("RIP ✓");
                        } else {
                            abstractRouter.desactivateRIP();
                            itemRIP.setText("RIP");
                        }
                    });
                    contextMenu.getItems().addAll(itemRIP);
                }


                /*
                    Menu DHCP
                 */

                Stage stageDHCP = new Stage();
                arrayList_of_stageDHCP.add(stageDHCP);
                if(device.contains("pc") |device.contains("server")) {


                    AnchorPane dhcpPane = new AnchorPane();

                    dhcpPane.setOnMouseClicked(event -> {

                        if (DeviceManager.is_bugging()) {
                            Toolkit.getDefaultToolkit().beep();
                            DeviceManager.errorStage.toFront();
                        }
                    });

                    Scene dhcpScene = new Scene(dhcpPane, 423, 60);
                    stageDHCP.setTitle(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).getName() + " : " + device);
                    stageDHCP.setScene(dhcpScene);
                    stageDHCP.sizeToScene();

                    stageDHCP.setOnCloseRequest(event -> {
                        if (bugmode) {
                            event.consume();
                        }
                    });

                    stageDHCP.getIcons().add(new Image("file:sprites/arjl-logo.png"));

                    Text dhcptext = new Text("DHCP Request PORT :");
                    dhcptext.setLayoutX(5);
                    dhcptext.setLayoutY(15);
                    dhcpPane.getChildren().add(dhcptext);


                    TextField fieldDHCP = new TextField("");


                    fieldDHCP.setLayoutX(5);
                    fieldDHCP.setLayoutY(30);

                    dhcpPane.getChildren().add(fieldDHCP);


                    Button validationButtonDHCP = new Button("OK");
                    validationButtonDHCP.setLayoutY(30);
                    validationButtonDHCP.setLayoutX(200);

                    validationButtonDHCP.setOnAction(event ->{
                        if(!DeviceManager.is_bugging()) {
                            String string = fieldDHCP.getText();

                            try {
                                int port = Integer.parseInt(string);
                                if(device.contains("pc")) {
                                    AbstractClient abstractClient = (AbstractClient) array_list_of_devices.get(array_list_of_image.indexOf(imageView));
                                    abstractClient.DHCPClient(port);
                                } else {
                                    AbstractServer abstractServer = (AbstractServer) array_list_of_devices.get(array_list_of_image.indexOf(imageView));
                                    abstractServer.DHCPClient(port);
                                }
                                stageDHCP.close();

                            } catch (NumberFormatException e) {

                                afficheError("You are not from the INT, aren't you ?");
                            } catch (OverflowException e) {
                                e.printStackTrace();
                            } catch (BadCallException e) {
                                e.printStackTrace();
                            }


                        }
                    });

                    dhcpPane.getChildren().add(validationButtonDHCP);



                    MenuItem itemDHCP = new MenuItem("DHCP Request");
                    itemDHCP.setOnAction(event -> {
                        reset("", status, content);
                        if (!stageDHCP.isShowing()) {
                            stageDHCP.show();
                        } else {
                            stageDHCP.toFront();
                        }


                    });

                    contextMenu.getItems().add(itemDHCP);
                }

                /*
                   Menu requete répétitive
                */

                Stage requestStage = new Stage();
                arrayList_of_requestStage.add(requestStage);

                if (device.contains("pc")) {
                    AbstractClient abstractClient = (AbstractClient) array_list_of_devices.get(array_list_of_image.indexOf(imageView));


                    AnchorPane requestPane = new AnchorPane();

                    requestPane.setOnMouseClicked(event -> {

                        if (DeviceManager.is_bugging()) {
                            Toolkit.getDefaultToolkit().beep();
                            DeviceManager.errorStage.toFront();
                        }
                    });

                    Scene requestScene = new Scene(requestPane, 423, 60);
                    requestStage.setTitle(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).getName() + " : " + device);
                    requestStage.setScene(requestScene);
                    requestStage.sizeToScene();

                    requestStage.setOnCloseRequest(event -> {
                        if (bugmode) {
                            event.consume();
                        }
                    });

                    requestStage.getIcons().add(new Image("file:sprites/arjl-logo.png"));



                    Text textIP = new Text("IP");
                    textIP.setLayoutX(5);
                    textIP.setLayoutY(15);
                    requestPane.getChildren().add(textIP);

                    TextField textFieldIP = new TextField("");
                    textFieldIP.setLayoutY(30);
                    textFieldIP.setLayoutX(5);
                    textFieldIP.setPrefWidth(95);
                    requestPane.getChildren().add(textFieldIP);

                    Text textType = new Text("Type");
                    textType.setLayoutX(105);
                    textType.setLayoutY(15);
                    requestPane.getChildren().add(textType);

                    ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList("FTP", "WEB"));
                    choiceBox.setLayoutX(105);
                    choiceBox.setLayoutY(30);
                    choiceBox.setPrefWidth(95);
                    requestPane.getChildren().add(choiceBox);


                    Text textTime = new Text("Time : ms");
                    textTime.setLayoutX(205);
                    textTime.setLayoutY(15);
                    requestPane.getChildren().add(textTime);

                    TextField textFieldTime = new TextField("");
                    textFieldTime.setLayoutY(30);
                    textFieldTime.setLayoutX(205);
                    textFieldTime.setPrefWidth(95);
                    requestPane.getChildren().add(textFieldTime);

                    Button okButton = new Button("OK");
                    okButton.setLayoutX(305);
                    okButton.setLayoutY(0);
                    requestPane.getChildren().add(okButton);

                    Button stopButton = new Button("STOP");
                    stopButton.setLayoutX(355);
                    stopButton.setLayoutY(0);
                    requestPane.getChildren().add(stopButton);

                    okButton.setOnAction(event -> {
                        if (!bugmode) {
                            if (choiceBox.getSelectionModel().selectedIndexProperty().intValue() == -1) {
                                afficheError("Select a Type");
                            } else if (choiceBox.getSelectionModel().selectedIndexProperty().intValue() == 0) {


                                String string = textFieldIP.getText();

                                IP ip = null;
                                int time = 0;
                                try {


                                    if (abstractClient.getConnectedLinks().get(0) == null) {
                                        throw new NotYetConnectedException();
                                    }


                                    ip = IP.stringToIP(string);
                                    Integer integer = Integer.parseInt(textFieldTime.getText());
                                    time = integer.intValue();
                                    if (time < 0) {
                                        throw new NumberFormatException();
                                    }
                                    System.out.println("OK");
                                    abstractClient.setRepetitiveRequest(PacketTypes.FTP, ip, time);
                                    System.out.println("Launch");
                                } catch (BadCallException e) {
                                    afficheError("Bad Request");

                                } catch (NumberFormatException e) {
                                    afficheError("Temps invalide");
                                } catch (NotYetConnectedException e) {
                                    afficheError("No Link Linked");
                                }

                            } else if (choiceBox.getSelectionModel().selectedIndexProperty().intValue() == 1) {
                                String string = textFieldIP.getText();

                                IP ip = null;
                                int time = 0;
                                try {

                                    if (abstractClient.getConnectedLinks().get(0) == null) {
                                        throw new NotYetConnectedException();
                                    }

                                    ip = IP.stringToIP(string);
                                    Integer integer = Integer.parseInt(textFieldTime.getText());
                                    time = integer.intValue();
                                    if (time < 0) {
                                        throw new NumberFormatException();
                                    }
                                    abstractClient.setRepetitiveRequest(PacketTypes.WEB, ip, time);
                                } catch (BadCallException e) {
                                    afficheError("Bad Request");

                                } catch (NumberFormatException e) {
                                    afficheError("Temps invalide");
                                } catch (NotYetConnectedException e) {
                                    afficheError("No Link Linked");
                                }
                            }
                        }
                    });

                    stopButton.setOnAction(event -> {
                        if (!bugmode) {
                            abstractClient.stopRepetitiveRequest();
                        }
                    });

                    MenuItem itemRequest = new MenuItem("Repetitive Request");
                    itemRequest.setOnAction(event -> {
                        reset("", status, content);
                        if (!requestStage.isShowing()) {
                            requestStage.show();
                        } else {
                            requestStage.toFront();
                        }


                    });

                    contextMenu.getItems().add(itemRequest);
                }




                contextMenu.getItems().addAll(itemLink, itemMultipleLink, itemDeleteLink, itemDelete);


                imageView.setOnContextMenuRequested(event -> {
                    if (!bugmode) {
                        contextMenu.show(imageView, Side.BOTTOM, event.getX() - imageView.getX(), event.getY() - imageView.getY() - imageView.getImage().getHeight());
                    }
                });


                imageView.setOnMousePressed(event -> {
                    if (!bugmode) {
                        if (event.isPrimaryButtonDown()) {
                            if (supressmode) {

                                try {
                                    imageView.setOnMouseExited(eventnull -> {
                                        ;
                                    });
                                    enlevePopupConfiq(anchorPane, infoAnchorPane);
                                    Supress.suprimerImage(imageView, content);
                                } catch (BadCallException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (addlinkmode == 1) {
                                if (firstimageView != null) {
                                    if (firstimageView.equals(imageView)) {

                                    } else {
                                        entier = 0;
                                    }
                                }
                                firstimageView = imageView;
                                firstimagesizeX = imageView.getImage().getWidth();
                                firstimagesizeY = imageView.getImage().getHeight();
                                addlinkmode = 2;

                                if (firstimageView.getAccessibleText().contains("pc")) {
                                    firstimageView.setImage(imagepcselected);
                                } else if (firstimageView.getAccessibleText().contains("server")) {
                                    firstimageView.setImage(imageserverselected);
                                } else if (firstimageView.getAccessibleText().contains("router")) {
                                    firstimageView.setImage(imagerouteurselected);
                                } else if (firstimageView.getAccessibleText().contains("switch")) {
                                    firstimageView.setImage(imageswitchselected);
                                } else if (firstimageView.getAccessibleText().contains("hub")) {
                                    firstimageView.setImage(imagehubselected);
                                } else if (firstimageView.getAccessibleText().contains("wan")) {
                                    firstimageView.setImage(imagewanselected);
                                }

                                status.setText("addLink 1");
                            } else if (addlinkmode == 2) {
                                if (firstimageView != imageView) {
                                    entier = 0;
                                    secondimageView = imageView;
                                    secondimagesizeX = imageView.getImage().getWidth();
                                    secondimagesizeY = imageView.getImage().getHeight();
                                    addlinkmode = 3;
                                    status.setText("addLink 2");
                                    try {
                                        AddLink.addlink(content, status, anchorPane);
                                    } catch (BadCallException e) {
                                        e.printStackTrace();
                                    }

                                    if (firstimageView.getAccessibleText().contains("pc")) {
                                        firstimageView.setImage(imagepc);
                                    } else if (firstimageView.getAccessibleText().contains("server")) {
                                        firstimageView.setImage(imageserver);
                                    } else if (firstimageView.getAccessibleText().contains("router")) {
                                        firstimageView.setImage(imagerouteur);
                                    } else if (firstimageView.getAccessibleText().contains("switch")) {
                                        firstimageView.setImage(imageswitch);
                                    } else if (firstimageView.getAccessibleText().contains("hub")) {
                                        firstimageView.setImage(imagehub);
                                    } else if (firstimageView.getAccessibleText().contains("wan")) {
                                        firstimageView.setImage(imagewan);
                                    }

                                } else {
                                    entier++;
                                    if (entier == 9) {


                                        Actualization.updateLinkDrag(imageView);

                                        reset("", status, content);

                                        afficheError("it's OVER 9000 !!!!!");

                                        imageView.setImage(imagepipeau);
                                        Actualization.updateInfoEtBarre();
                                        Media media = new Media(new File("video/9000.mp4").toURI().toASCIIString());
                                        MediaPlayer player = new MediaPlayer(media);
                                        player.setAutoPlay(true);

                                        MediaView mediaView = new MediaView(player);


                                        anchorPane.getChildren().add(mediaView);


                                        mediaView.setLayoutX(0);
                                        mediaView.setLayoutY(scrollPane.getLayoutY());
                                        player.setOnEndOfMedia(new Runnable() {
                                            @Override
                                            public void run() {
                                                // actions here e.g.:
                                                anchorPane.getChildren().remove(mediaView);
                                            }
                                        });


                                    }
                                    if (entier > 9) {
                                        int a = entier;
                                        if (a < 14) {
                                            Media media = new Media(new File("video/video" + a + ".mp4").toURI().toASCIIString());
                                            MediaPlayer player = new MediaPlayer(media);
                                            player.setAutoPlay(true);

                                            MediaView mediaView = new MediaView(player);

                                            primaryStage.setFullScreen(true);


                                            Image black = new Image("file:sprites/Noir.jpg");
                                            ImageView imageViewBlack = new ImageView(black);
                                            anchorPane.getChildren().add(imageViewBlack);

                                            anchorPane.getChildren().add(mediaView);


                                            mediaView.setFitHeight(primaryStage.getHeight());


                                            mediaView.setLayoutX(0);
                                            mediaView.setLayoutY(0);


                                            player.setOnEndOfMedia(new Runnable() {
                                                @Override
                                                public void run() {
                                                    // actions here e.g.:
                                                    anchorPane.getChildren().remove(mediaView);
                                                    anchorPane.getChildren().remove(imageViewBlack);
                                                    primaryStage.setFullScreen(false);

                                                }
                                            });
                                        }

                                    }

                                }

                            } else if (addlinkmode == 4) {
                                if (firstimageView != imageView) {

                                    secondimageView = imageView;
                                    secondimagesizeX = imageView.getImage().getWidth();
                                    secondimagesizeY = imageView.getImage().getHeight();
                                    addlinkmode = 3;
                                    status.setText("addLink 2");
                                    try {
                                        AddLink.addlink(content, status, anchorPane);
                                    } catch (BadCallException e) {
                                        e.printStackTrace();
                                    }
                                    reset("", status, content);
                                    addlinkmode = 5;
                                    if (firstimageView.getAccessibleText().contains("pc")) {
                                        firstimageView.setImage(imagepc);
                                    } else if (firstimageView.getAccessibleText().contains("server")) {
                                        firstimageView.setImage(imageserver);
                                    } else if (firstimageView.getAccessibleText().contains("router")) {
                                        firstimageView.setImage(imagerouteur);
                                    } else if (firstimageView.getAccessibleText().contains("switch")) {
                                        firstimageView.setImage(imageswitch);
                                    } else if (firstimageView.getAccessibleText().contains("hub")) {
                                        firstimageView.setImage(imagehub);
                                    } else if (firstimageView.getAccessibleText().contains("wan")) {
                                        firstimageView.setImage(imagewan);
                                    }
                                }

                            } else if (addlinkmode == 5) {
                                addlinkmode = 0;

                            } else if (addlinkmode == 6) {
                                if (firstimageView != imageView) {
                                    secondimageView = imageView;
                                    secondimagesizeX = imageView.getImage().getWidth();
                                    secondimagesizeY = imageView.getImage().getHeight();

                                    try {
                                        AddLink.addlink(content, status, anchorPane);
                                    } catch (BadCallException e) {
                                        e.printStackTrace();
                                    }

                                    addlinkmode = 6;
                                    lienmultiple.toFront();
                                }

                            } else if (addlinkmode == 7) {

                                firstimageView = imageView;
                                firstimagesizeX = imageView.getImage().getWidth();
                                firstimagesizeY = imageView.getImage().getHeight();


                                addlinkmode = 6;
                                lienmultiple.setLayoutX(imageView.getX());
                                lienmultiple.setLayoutY(imageView.getY());
                                content.getChildren().add(lienmultiple);
                                if (firstimageView.getAccessibleText().contains("pc")) {
                                    firstimageView.setImage(imagepcselected);
                                } else if (firstimageView.getAccessibleText().contains("server")) {
                                    firstimageView.setImage(imageserverselected);
                                } else if (firstimageView.getAccessibleText().contains("router")) {
                                    firstimageView.setImage(imagerouteurselected);
                                } else if (firstimageView.getAccessibleText().contains("switch")) {
                                    firstimageView.setImage(imageswitchselected);
                                } else if (firstimageView.getAccessibleText().contains("hub")) {
                                    firstimageView.setImage(imagehubselected);
                                } else if (firstimageView.getAccessibleText().contains("wan")) {
                                    firstimageView.setImage(imagewanselected);
                                }


                            }
                        }
                    }
                });
















        /*
            L'image selectionnée mise au premier plan
         */
                imageView.setOnDragDetected(event -> {
                    if (event.isPrimaryButtonDown()) {
                        imageView.toFront();
                        imageViewError.toFront();
                        infoAnchorPane.toFront();
                        lienmultiple.toFront();

                    }
                });

                imageView.setOnMouseDragged(event -> {

                    if (!supressmode && addlinkmode == 0 && event.isPrimaryButtonDown() && !bugmode) {

            /*
                position de la souris
             */
                        double x = event.getSceneX();
                        double y = event.getSceneY() - scrollPane.getLayoutY();



            /*
                Récupération de la position rélative du scrollbar par raport à la fenetre
             */
                        double difx = content.getWidth() - anchorPane.getWidth() + 10;
                        double relx = scrollPane.getHvalue() * difx;
                        double dify = content.getHeight() - anchorPane.getHeight() + scrollPane.getLayoutY() + 10;
                        double rely = scrollPane.getVvalue() * dify;


            /*
                Correction pour ne pas sortir par la gauche et en haut.
             */
                        if (x < (imageView.getImage().getWidth() / 2)) {
                            x = (imageView.getImage().getWidth() / 2);
                        }
                        if (y < (imageView.getImage().getHeight() / 2)) {
                            y = (imageView.getImage().getHeight() / 2);
                        }

            /*
                Correction pour ne pas sortir par la droite et en bas
             */
                        double mx = anchorPane.getWidth();
                        double my = anchorPane.getHeight() - scrollPane.getLayoutY();
                        if (x > mx - imageView.getImage().getWidth() / 2 - 10) {
                            x = mx - imageView.getImage().getWidth() / 2 - 10;
                        }
                        if (y > my - imageView.getImage().getHeight() / 2 - 10) {
                            y = my - imageView.getImage().getHeight() / 2 - 10;
                        }

            /*
                Relocation de l'image
             */

                        imageView.setX(x - imageView.getImage().getWidth() / 2 + relx);
                        imageView.setY(y - imageView.getImage().getHeight() / 2 + rely);
                        infoAnchorPane.setLayoutX(imageView.getX() + imageView.getImage().getWidth());
                        infoAnchorPane.setLayoutY(imageView.getY());
                        progressBar.setLayoutX(imageView.getX());
                        progressBar.setLayoutY(imageView.getY() + imageView.getImage().getHeight());
                        progressBarRequest.setLayoutX(imageView.getX());
                        progressBarRequest.setLayoutY(imageView.getY() - 13);
                        int index = array_list_of_image.indexOf(imageView);
                        arrayListOfX.set(index, imageView.getX() / zoomScale);
                        arrayListOfY.set(index, imageView.getY() / zoomScale);

            /*
                Actualisation des liens
             */
                        Actualization.updateLinkDrag(imageView);
                    }
                });
                createmode = false;

            } else {
                int index = array_list_of_image.indexOf(imageView);
                content.getChildren().remove(imageView);
                array_list_of_image.remove(index);
                content.getChildren().remove(imageViewError);
                arrayList_of_error.remove(index);
                arrayListOfStateError.remove(index);
                WAN=1;
                createmode = false;

                if(loadmode) {
                    afficheError("Corrupted Save");
                } else {
                    afficheError("Only 1 WAN PORT allowed");
                }

            }

        }


    }



}
