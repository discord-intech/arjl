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
import hardware.server.AbstractServer;
import hardware.switchs.AbstractSwitch;
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
import javafx.scene.image.PixelFormat;
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

    public synchronized static void addLineVerbose(String str) {


        String text = verboseText.getText();

        text = str +"\n" + text;
        verboseText.setText(text);

        //verbosePane.setVvalue(1);




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


    /**
     * Les différentes images utilisées
     */
    private static Image imagepc = new Image("file:sprites/pack1/pc.png");
    private static Image imageserver = new Image("file:sprites/pack1/server.png");
    private static Image imagerouteur = new Image("file:sprites/pack1/router2.png");
    private static Image imageswitch = new Image("file:sprites/pack1/switch.png");
    private static Image imageerror = new Image("file:sprites/Error.png");

    private static Image imagepcselected = new Image("file:sprites/pack1/pc-selected.png");
    private static Image imageserverselected = new Image("file:sprites/pack1/server-selected.png");
    private static Image imagerouteurselected = new Image("file:sprites/pack1/router2-selected.png");
    private static Image imageswitchselected = new Image("file:sprites/pack1/switch-selected.png");



    public static void setpacktexture(int i) {
        if(i==1) {
            imagepc = new Image("file:sprites/pack1/pc.png");
            imageserver = new Image("file:sprites/pack1/server.png");
            imagerouteur = new Image("file:sprites/pack1/router2.png");
            imageswitch = new Image("file:sprites/pack1/switch.png");
            imagepcselected = new Image("file:sprites/pack1/pc-selected.png");
            imageserverselected = new Image("file:sprites/pack1/server-selected.png");
            imagerouteurselected = new Image("file:sprites/pack1/router2-selected.png");
            imageswitchselected = new Image("file:sprites/pack1/switch-selected.png");


        }
        if(i==2) {
            imagepc = new Image("file:sprites/pack2/pc.png");
            imageserver = new Image("file:sprites/pack2/server.png");
            imagerouteur = new Image("file:sprites/pack2/router2.png");
            imageswitch = new Image("file:sprites/pack2/switch.png");
            imagepcselected = new Image("file:sprites/pack2/pc-selected.png");
            imageserverselected = new Image("file:sprites/pack2/server-selected.png");
            imagerouteurselected = new Image("file:sprites/pack2/router2-selected.png");
            imageswitchselected = new Image("file:sprites/pack2/switch-selected.png");


        }


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
            }
        }

        updateInfoEtBarre();
        updateLinkDrag();

    }


    /**
     * Suppression d'une image
     * @param imageView
     * @param anchorPane
     * @throws exceptions.BadCallException
     */
    public static synchronized void suprimerImage (ImageView imageView,AnchorPane anchorPane) throws exceptions.BadCallException {
        if(!bugmode) {
            int index = array_list_of_image.indexOf(imageView);

            System.out.println("Hard Links :" + array_list_of_links_hard.size());
            System.out.println("Images links :" + array_list_of_link.size());


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

            System.out.println("Array list Index : " + array_list_of_index.toString());
            System.out.println("Hard Links :" + array_list_of_links_hard.size());
            System.out.println("Images links :" + array_list_of_link.size());

            //System.out.println("maman d'aymeric" + array_list_of_index.size());


            for (int i = array_list_of_index.size() - 1; i >= 0; i--) {

                System.out.println(i);
                Actions.disconnect(array_list_of_links_hard.get(array_list_of_index.get(i).intValue()));
                System.out.println(array_list_of_links_hard.remove(array_list_of_index.get(i).intValue()));
            }
            System.out.println("Hard Links :" + array_list_of_links_hard.size());
            System.out.println("Images links :" + array_list_of_link.size());


            array_list_of_devices.get(index).stop();
            System.out.println(array_list_of_devices.remove(index));
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

            n = list_element.size();
            for (int i = 0; i < n; ++i) {

                ElementOfLink element = list_element.get(i);
                anchorPane.getChildren().remove(element.line);
                array_list_of_link.remove(element);
            }

            System.out.println("Hard Links :" + array_list_of_links_hard.size());
            System.out.println("Images links :" + array_list_of_link.size());
            updateInfoMenu();
        }
    }



    /**
     * Suppression d'un lien
     * @param line
     * @param anchorPane
     * @param status
     * @throws exceptions.BadCallException
     */
    public static void suprimerLien (Line line,AnchorPane anchorPane,Label status) throws exceptions.BadCallException {

        if(!bugmode) {
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

            System.out.println(array_list_of_link.remove(index));
            System.out.println(array_list_of_links_hard.remove(index));

            System.out.println("Hard Links : " + array_list_of_links_hard.size());
            System.out.println("Images links : " + array_list_of_link.size());
            updateInfoMenu();
        }
    }



    /**
     * Ajout d'un lien
     * @param content
     * @param status
     * @param anchorPane
     * @throws exceptions.BadCallException
     */
    public static void addlink (AnchorPane content,Label status,AnchorPane anchorPane) throws exceptions.BadCallException {
        if(!bugmode) {
            Line line = new Line();

            line.setStartX(firstimageView.getX() + firstimagesizeX / 2);
            line.setStartY(firstimageView.getY() + firstimagesizeY / 2);
            line.setEndX(secondimageView.getX() + secondimagesizeX / 2);
            line.setEndY(secondimageView.getY() + secondimagesizeY / 2);

            line.setOnMouseClicked(event -> {
                if (supressmode) {
                    try {
                        suprimerLien(line, content, status);
                    } catch (BadCallException e) {
                        reset("", status, content);
                        e.printStackTrace();
                    }
                }
            });


            try {
                int index1 = array_list_of_image.indexOf(firstimageView);
                int index2 = array_list_of_image.indexOf(secondimageView);

                if (!loadmode) {
                    array_list_of_links_hard.add(Actions.connect(array_list_of_devices.get(index1), array_list_of_devices.get(index2), LinkTypes.ETH));
                }
                line.setStrokeWidth(4);
                line.setStroke(Color.web("000000"));
                content.getChildren().add(line);
                line.toBack();


                array_list_of_link.add(new ElementOfLink(firstimageView, secondimageView, line));


                if (!loadmode) {
                    System.out.println(array_list_of_links_hard.get(array_list_of_links_hard.size() - 1).getType());
                    System.out.println(array_list_of_links_hard.get(array_list_of_links_hard.size() - 1).getHardwareConnected());
                }
                addlinkmode = 1;
                status.setText("addLink");
                updateInfoMenu();
            } catch (BadCallException e) {
                reset("", status, content);
                afficheError("Impossible to create link between this two devices");
            }
            if (!loadmode) {
                System.out.println("Hard Links :" + array_list_of_links_hard.size());
                System.out.println("Images links :" + array_list_of_link.size());
            }
        }
    }



    /**
     * Passer en mode DELETE
     * @param status
     * @param content
     */
    public static void suprimodeactivate (Label status,AnchorPane content) {
        if(!bugmode) {
            if (supressmode) {
                reset("", status, content);
            } else {

                status.setText("DELETE");
                supressmode = true;
                reset("delete", status, content);
            }
        }
    }



    /**
     * Passer en mode création de liens
     * @param status
     * @param content
     */
    public static void addlinkmodeactivate (Label status,AnchorPane content) {
        if(!bugmode) {
            if (addlinkmode != 0) {
                reset("", status, content);
            } else {
                status.setText("addLink");
                addlinkmode = 1;
                reset("addlink", status, content);
            }
        }
    }



    /**
     * On update les liens liés à une image pour le drag ans drop
     * @param imageView
     */
    public static void updateLinkDrag(ImageView imageView) {
        if(!bugmode) {
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

        if(!bugmode) {
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
     * On update les liens
     * @throws BadCallException
     */
    public static void updateLink() throws BadCallException {


        int[] linkStats=actions.Actions.linkStats(array_list_of_links_hard);

        int n = linkStats.length;
        for(int i = 0; i<n;i++) {


            ElementOfLink elementOfLink = array_list_of_link.get(i);

            int ratio = linkStats[i];

            
            int red = 255*(ratio/100);
            int green = 255*(1-ratio/100);

            elementOfLink.line.setStroke(Color.rgb(red,green,0));

        }
    }


    /**
     * On actualise le trafic dans les progressBars
     */
    public static void updateprogressBar() {
        if(!loadmode) {
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


        arrayList_of_infoMenu.forEach(infoAnchorPane -> {


            ObservableList<Node> list_of_machin = infoAnchorPane.getChildren();
            Text t0 = new Text();
            t0 = (Text) list_of_machin.get(0);


            if (!t0.getText().equals("Device = Error")) {
                //On update la info d'un PC



                Text t1 = (Text) list_of_machin.get(1);



                if (array_list_of_devices.get(arrayList_of_infoMenu.indexOf(infoAnchorPane)).isRunning()) {
                    t1.setText("Power : " + "ON");
                } else {
                    t1.setText("Power : " + "OFF");
                }

                Text t2 = (Text) list_of_machin.get(2);






                t2.setText("Free ports : " + array_list_of_devices.get(arrayList_of_infoMenu.indexOf(infoAnchorPane)).getFreePorts().size());
            }




        });




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
     * On convertit les ImageView pour la sauvegarde
     * @return
     */
    public static ArrayList<ImageToSave> converterImageView() {
        ArrayList <ImageToSave > array_list_of_imageconverted = new ArrayList<>();


        array_list_of_image.forEach(imageView -> {
            ImageToSave imageToSave = new ImageToSave(imageView);
            array_list_of_imageconverted.add(imageToSave);

        });

        System.out.println(array_list_of_imageconverted);


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
                device(type,anchorPane,scrollPane,content,status,primaryStage);
            } catch (BadCallException e) {
                e.printStackTrace();
            }
            ImageView imageView = array_list_of_image.get(i);
            imageView.setX(x);
            imageView.setY(y);

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
        ArrayList <LinkToSave > array_list_of_linkconverted = new ArrayList<>();


        array_list_of_link.forEach(Element_of_Link-> {
            LinkToSave linkToSave = new LinkToSave(Element_of_Link);
            array_list_of_linkconverted.add(linkToSave);

        });

        System.out.println(array_list_of_linkconverted);


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


        int n = array_list_of_linkconverted.size();
        addlinkmode=1;
        for(int i=0;i<n;i++) {

            LinkToSave linkToSave = (LinkToSave) array_list_of_linkconverted.get(i);
            int indexImage1=linkToSave.indexImage1;
            int indexImage2=linkToSave.indexImage2;

            firstimageView=array_list_of_image.get(indexImage1);
            Image image1=firstimageView.getImage();
            firstimagesizeX=image1.getWidth();
            firstimagesizeY=image1.getHeight();
            secondimageView=array_list_of_image.get(indexImage2);
            Image image2=secondimageView.getImage();
            secondimagesizeX=image2.getWidth();
            secondimagesizeY=image2.getHeight();



            try {
                addlink(content,status,anchorPane);
            } catch (BadCallException e) {
                e.printStackTrace();
            }

        }
        reset("",status,content);




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
     * Fonction qui initialise et actualise les menus de confique
     */

    public static void updateConfigMenuPc (Stage configStage,AnchorPane configPane,ArrayList<Object> actualiseGraph,AbstractClient abstractClient) throws BadCallException {



        int n = actualiseGraph.size();

        for(int i = 0;i<n;i++) {
            configPane.getChildren().remove(actualiseGraph.get(i));
        }

        actualiseGraph.clear();



        TextField textFieldsurname = new TextField(abstractClient.nickname);
        textFieldsurname.setLayoutY(0);
        textFieldsurname.setLayoutX(200);
        textFieldsurname.setPrefWidth(100);
        configPane.getChildren().add(textFieldsurname);
        actualiseGraph.add(textFieldsurname);

        Button surnameButton = new Button ("OK");
        surnameButton.setLayoutX(300);
        surnameButton.setLayoutY(0);
        configPane.getChildren().add(surnameButton);
        actualiseGraph.add(surnameButton);

        surnameButton.setOnAction(event -> {
            String surname=textFieldsurname.getText();
            abstractClient.nickname=surname;
            configStage.setTitle(surname);
        });

        Text iptext = new Text("IP : ");
        iptext.setLayoutX(5);
        iptext.setLayoutY(45);
        configPane.getChildren().add(iptext);
        actualiseGraph.add(iptext);
        TextField textField = null;
        try {
            textField = new TextField(abstractClient.getInterfaceIP(0).toString());
        } catch (BadCallException e) {
            afficheError("Error 23");
        }
        configPane.getChildren().add(textField);
        actualiseGraph.add(textField);
        textField.setLayoutX(5);
        textField.setLayoutY(60);

        Button validationButton = new Button("OK");
        validationButton.setLayoutY(60);
        validationButton.setLayoutX(200);
        final TextField finalTextField = textField;
        validationButton.setOnAction(event ->{
            if(!bugmode) {
                String string = finalTextField.getText();

                Boolean isAnIp = true;

                IP Ip = null;

                try {
                    Ip = IP.stringToIP(string);
                } catch (BadCallException e) {
                    afficheError("Wrong Argument : Not an IP");
                    try {
                        finalTextField.setText(abstractClient.getInterfaceIP(0).toString());
                    } catch (BadCallException e1) {
                        e1.printStackTrace();
                    }
                    isAnIp = false;
                }

                if (isAnIp) {
                    abstractClient.configureIP(0, Ip);
                }


            }
        });

        configPane.getChildren().add(validationButton);
        actualiseGraph.add(validationButton);

        Text gatewaytext = new Text("gateway : ");
        gatewaytext.setLayoutX(5);
        gatewaytext.setLayoutY(105);
        configPane.getChildren().add(gatewaytext);
        actualiseGraph.add(gatewaytext);
        TextField textFieldgateway = null;

        textFieldgateway = new TextField(abstractClient.getGateway().toString());

        configPane.getChildren().add(textFieldgateway);
        actualiseGraph.add(textFieldgateway);
        textFieldgateway.setLayoutX(5);
        textFieldgateway.setLayoutY(120);

        Button validationButtongateway = new Button("OK");
        validationButtongateway.setLayoutY(120);
        validationButtongateway.setLayoutX(200);
        final TextField finalTextFieldgateway = textFieldgateway;
        validationButtongateway.setOnAction(event ->{
            if(!bugmode) {
                String string = finalTextFieldgateway.getText();

                Boolean isAnIp = true;

                IP Ip = null;

                try {
                    Ip = IP.stringToIP(string);
                } catch (BadCallException e) {
                    afficheError("Wrong Argument : Not an IP");

                        finalTextFieldgateway.setText(abstractClient.getGateway().toString());

                    isAnIp = false;
                }

                if (isAnIp) {
                    abstractClient.configureGateway(Ip);
                }


            }
        });

        configPane.getChildren().add(validationButtongateway);
        actualiseGraph.add(validationButtongateway);




    }

    public static void updateConfigMenuServer (Stage configStage,AnchorPane configPane,ArrayList<Object> actualiseGraph,AbstractServer abstractServer) throws BadCallException {


        int n = actualiseGraph.size();

        for(int i = 0;i<n;i++) {
            configPane.getChildren().remove(actualiseGraph.get(i));
        }

        actualiseGraph.clear();


        TextField textFieldsurname = new TextField(abstractServer.nickname);
        textFieldsurname.setLayoutY(0);
        textFieldsurname.setLayoutX(200);
        textFieldsurname.setPrefWidth(100);
        configPane.getChildren().add(textFieldsurname);
        actualiseGraph.add(textFieldsurname);

        Button surnameButton = new Button ("OK");
        surnameButton.setLayoutX(300);
        surnameButton.setLayoutY(0);
        configPane.getChildren().add(surnameButton);
        actualiseGraph.add(surnameButton);

        surnameButton.setOnAction(event -> {
            String surname=textFieldsurname.getText();
            abstractServer.nickname=surname;
            configStage.setTitle(surname);
        });

        Text iptext = new Text("IP : ");
        iptext.setLayoutX(5);
        iptext.setLayoutY(45);
        configPane.getChildren().add(iptext);
        actualiseGraph.add(iptext);
        TextField textField = null;
        try {
            textField = new TextField(abstractServer.getInterfaceIP(0).toString());
        } catch (BadCallException e) {
            afficheError("Error 23");
        }
        configPane.getChildren().add(textField);
        actualiseGraph.add(textField);
        textField.setLayoutX(5);
        textField.setLayoutY(60);

        Button validationButton = new Button("OK");
        validationButton.setLayoutY(60);
        validationButton.setLayoutX(200);
        final TextField finalTextField = textField;
        validationButton.setOnAction(event ->{
            if(!bugmode) {
                String string = finalTextField.getText();

                Boolean isAnIp = true;

                IP Ip = null;

                try {
                    Ip = IP.stringToIP(string);
                } catch (BadCallException e) {
                    afficheError("Wrong Argument : Not an IP");
                    try {
                        finalTextField.setText(abstractServer.getInterfaceIP(0).toString());
                    } catch (BadCallException e1) {
                        e1.printStackTrace();
                    }
                    isAnIp = false;
                }

                if (isAnIp) {
                    abstractServer.configureIP(0, Ip);
                }


            }


        });

        configPane.getChildren().add(validationButton);
        actualiseGraph.add(validationButton);

        Text gatewaytext = new Text("gateway : ");
        gatewaytext.setLayoutX(5);
        gatewaytext.setLayoutY(105);
        configPane.getChildren().add(gatewaytext);
        actualiseGraph.add(gatewaytext);
        TextField textFieldgateway = null;

        textFieldgateway = new TextField(abstractServer.getGateway().toString());

        configPane.getChildren().add(textFieldgateway);
        actualiseGraph.add(textFieldgateway);
        textFieldgateway.setLayoutX(5);
        textFieldgateway.setLayoutY(120);

        Button validationButtongateway = new Button("OK");
        validationButtongateway.setLayoutY(120);
        validationButtongateway.setLayoutX(200);
        final TextField finalTextFieldgateway = textFieldgateway;
        validationButtongateway.setOnAction(event ->{
            if(!bugmode) {
                String string = finalTextFieldgateway.getText();

                Boolean isAnIp = true;

                IP Ip = null;

                try {
                    Ip = IP.stringToIP(string);
                } catch (BadCallException e) {
                    afficheError("Wrong Argument : Not an IP");

                    finalTextFieldgateway.setText(abstractServer.getGateway().toString());

                    isAnIp = false;
                }

                if (isAnIp) {
                    abstractServer.configureGateway(Ip);
                }


            }
        });

        configPane.getChildren().add(validationButtongateway);
        actualiseGraph.add(validationButtongateway);

    }

    public static void updateConfigMenuRouter (Stage configStage,AnchorPane configPane,ArrayList<Object> actualiseGraph,AbstractRouter abstractRouter) {

        int m = actualiseGraph.size();

        for(int a=0;a<m;a++) {
            configPane.getChildren().remove(actualiseGraph.get(a));


        }
        actualiseGraph.clear();

        TextField textFieldsurname = new TextField(abstractRouter.nickname);
        textFieldsurname.setLayoutY(30);
        textFieldsurname.setLayoutX(200);
        textFieldsurname.setPrefWidth(100);
        configPane.getChildren().add(textFieldsurname);
        actualiseGraph.add(textFieldsurname);

        Button surnameButton = new Button ("OK");
        surnameButton.setLayoutX(300);
        surnameButton.setLayoutY(30);
        configPane.getChildren().add(surnameButton);
        actualiseGraph.add(surnameButton);

        surnameButton.setOnAction(event -> {
            String surname=textFieldsurname.getText();
            abstractRouter.nickname=surname;
            configStage.setTitle(surname);
        });

        int i = 0;



        TextField textFieldIPDHCP = new TextField();

        if (abstractRouter.getDHCPaddress()!=null) {
            textFieldIPDHCP.setText(abstractRouter.getDHCPaddress().toString());
        }

        textFieldIPDHCP.setLayoutX(250);
        textFieldIPDHCP.setLayoutY(0);
        configPane.getChildren().add(textFieldIPDHCP);
        actualiseGraph.add(textFieldIPDHCP);

        CheckBox checkBox = new CheckBox("DHCP Relay");
        checkBox.setLayoutX(150);
        checkBox.setLayoutY(0);
        configPane.getChildren().add(checkBox);
        actualiseGraph.add(checkBox);
        checkBox.setSelected(abstractRouter.isDHCPRelay());

        checkBox.setOnAction(event -> {
                    if(!bugmode) {
                        if(checkBox.selectedProperty().getValue()) {
                            try {
                                String stringDHCP = textFieldIPDHCP.getText();
                                IP ipDHCP = IP.stringToIP(stringDHCP);
                                abstractRouter.setDHCPRelay(ipDHCP);

                            } catch (BadCallException e) {
                                checkBox.setSelected(false);
                                afficheError("Bad DHCP IP");
                            }
                        } else {
                            abstractRouter.stopDHCPRelay();
                            
                        }
                    } else {
                        if(checkBox.selectedProperty().getValue()) {
                            checkBox.setSelected(false);
                        } else {
                            checkBox.setSelected(true);
                        }
                    }
        });





        while (true) {
            try {
                abstractRouter.getInterfaceIP(i);


                Text porttext = new Text("PORT" + i + " : ");
                porttext.setLayoutX(5);
                porttext.setLayoutY(45 + 90 * i);
                configPane.getChildren().add(porttext);
                actualiseGraph.add(porttext);

                Text iptext = new Text("IP : ");
                iptext.setLayoutX(5);
                iptext.setLayoutY(75 + 90 * i);
                configPane.getChildren().add(iptext);
                actualiseGraph.add(iptext);

                TextField textField = new TextField(abstractRouter.getInterfaceIP(i).toString());
                configPane.getChildren().add(textField);
                actualiseGraph.add(textField);
                textField.setLayoutX(5);
                textField.setLayoutY(90 + 90 * i);
                Button validationButton = new Button("OK");
                validationButton.setLayoutY(90 + 90 * i);
                validationButton.setLayoutX(200);
                int ilambda = i;
                validationButton.setOnAction(event -> {
                    if(!bugmode) {
                        String string = textField.getText();

                        Boolean isAnIp = true;

                        IP nIp = null;

                        try {
                            nIp = IP.stringToIP(string);
                        } catch (BadCallException e) {
                            afficheError("Wrong Argument : Not an IP");
                            try {
                                textField.setText(abstractRouter.getInterfaceIP(ilambda).toString());
                            } catch (BadCallException e1) {
                                e1.printStackTrace();
                            }
                            isAnIp = false;
                        }

                        if (isAnIp) {
                            abstractRouter.configureIP(ilambda, nIp);
                        }
                    }

                });

                configPane.getChildren().add(validationButton);
                actualiseGraph.add(validationButton);
            } catch (BadCallException e) {
                break;
            }
            i++;


        }



        Text tablerout = new Text("Routing table : ");
        tablerout.setLayoutX(5);
        tablerout.setLayoutY(60 + 90 * (i));
        configPane.getChildren().add(tablerout);
        actualiseGraph.add(tablerout);

        ArrayList<ArrayList<Object>> allroutes = abstractRouter.getAllRoutes();
        int n = allroutes.size();

        Text subnetText = new Text("Subnet");
        Text maskText = new Text("Mask");
        Text gatewayText = new Text("Gateway");
        Text portnumberText = new Text("Port Number");

        subnetText.setLayoutY(75 + 90 * (i));
        maskText.setLayoutY(75 + 90 * (i));
        gatewayText.setLayoutY(75 + 90 * (i));
        portnumberText.setLayoutY(75 + 90 * (i));

        subnetText.setLayoutX(5);
        maskText.setLayoutX(105);
        gatewayText.setLayoutX(205);
        portnumberText.setLayoutX(305);

        configPane.getChildren().addAll(subnetText, maskText, gatewayText, portnumberText);
        actualiseGraph.add(subnetText);
        actualiseGraph.add(maskText);
        actualiseGraph.add(gatewayText);
        actualiseGraph.add(portnumberText);

        Button validationRouteButton = new Button("OK");
        validationRouteButton.setLayoutY(60 + 90 * (i));
        validationRouteButton.setLayoutX(400);


        configPane.getChildren().add(validationRouteButton);
        actualiseGraph.add(validationRouteButton);


        Button addRouteButton = new Button("New");
        addRouteButton.setLayoutY(60 + 90 * (i));
        addRouteButton.setLayoutX(450);
        configPane.getChildren().add(addRouteButton);
        actualiseGraph.add(addRouteButton);




        ArrayList <ArrayList <Object>> tableRouteField = new ArrayList<>();

        ArrayList <Boolean> tablerouteok = new ArrayList<>();

        final int[] j = {0};

        while (j[0] < n) {

            TextField subnetTextField = new TextField(allroutes.get(j[0]).get(0).toString());
            TextField maskTextField = new TextField(allroutes.get(j[0]).get(1).toString());
            TextField gatewayTextField = new TextField(allroutes.get(j[0]).get(2).toString());
            TextField portnumberTextField = new TextField(allroutes.get(j[0]).get(3).toString());
            Button    deleteButton = new Button("DELETE");

            tablerouteok.add(true);
            int j1= j[0];
            int i1=i;
            deleteButton.setOnAction(event -> {
                if(!bugmode) {
                    tablerouteok.set(j1, false);

                    Text deleteConfirm = new Text("Ready to be deleted");
                    deleteConfirm.setLayoutY(105 + 90 * (i1) + 30 * (j1));
                    deleteConfirm.setLayoutX(5);
                    configPane.getChildren().add(deleteConfirm);
                    actualiseGraph.add(deleteConfirm);

                    configPane.getChildren().remove(subnetTextField);
                    configPane.getChildren().remove(maskTextField);
                    configPane.getChildren().remove(gatewayTextField);
                    configPane.getChildren().remove(portnumberTextField);
                    configPane.getChildren().remove(deleteButton);

                    actualiseGraph.remove(subnetTextField);
                    actualiseGraph.remove(maskTextField);
                    actualiseGraph.remove(gatewayTextField);
                    actualiseGraph.remove(portnumberTextField);
                    actualiseGraph.remove(deleteButton);
                }


            });

            ArrayList <Object> tablerouteI = new ArrayList<>();
            tablerouteI.add(subnetTextField);
            tablerouteI.add(maskTextField);
            tablerouteI.add(gatewayTextField);
            tablerouteI.add(portnumberTextField);

            tableRouteField.add(tablerouteI);

            subnetTextField.setLayoutY(90 + 90 * (i)+30*(j[0]));
            maskTextField.setLayoutY(90 + 90 * (i)+30*(j[0]));
            gatewayTextField.setLayoutY(90 + 90 * (i)+30*(j[0]));
            portnumberTextField.setLayoutY(90 + 90 * (i)+30*(j[0]));
            deleteButton.setLayoutY(90 + 90 * (i)+30*(j[0]));

            subnetTextField.setLayoutX(5);
            maskTextField.setLayoutX(105);
            gatewayTextField.setLayoutX(205);
            portnumberTextField.setLayoutX(305);
            deleteButton.setLayoutX(405);

            subnetTextField.setPrefWidth(95);
            maskTextField.setPrefWidth(95);
            gatewayTextField.setPrefWidth(95);
            portnumberTextField.setPrefWidth(95);


            configPane.getChildren().addAll(subnetTextField, maskTextField, gatewayTextField, portnumberTextField,deleteButton);
            actualiseGraph.add(subnetTextField);
            actualiseGraph.add(maskTextField);
            actualiseGraph.add(gatewayTextField);
            actualiseGraph.add(portnumberTextField);
            actualiseGraph.add(deleteButton);

            j[0]++;
        }


        final int finalI = i;
        addRouteButton.setOnAction(event ->{
                    if(!bugmode) {
                        TextField subnetTextField = new TextField("");
                        TextField maskTextField = new TextField("");
                        TextField gatewayTextField = new TextField("");
                        TextField portnumberTextField = new TextField("");
                        Button deleteButton = new Button("DELETE");

                        tablerouteok.add(true);
                        int j1 = j[0];
                        int i1 = finalI;
                        deleteButton.setOnAction(event1 -> {

                            tablerouteok.set(j1, false);

                            Text deleteConfirm = new Text("Ready to be deleted");
                            deleteConfirm.setLayoutY(105 + 90 * (i1) + 30 * (j1));
                            deleteConfirm.setLayoutX(5);
                            configPane.getChildren().add(deleteConfirm);
                            actualiseGraph.add(deleteConfirm);

                            configPane.getChildren().remove(subnetTextField);
                            configPane.getChildren().remove(maskTextField);
                            configPane.getChildren().remove(gatewayTextField);
                            configPane.getChildren().remove(portnumberTextField);
                            configPane.getChildren().remove(deleteButton);

                            actualiseGraph.remove(subnetTextField);
                            actualiseGraph.remove(maskTextField);
                            actualiseGraph.remove(gatewayTextField);
                            actualiseGraph.remove(portnumberTextField);
                            actualiseGraph.remove(deleteButton);


                        });

                        ArrayList<Object> tablerouteI = new ArrayList<>();
                        tablerouteI.add(subnetTextField);
                        tablerouteI.add(maskTextField);
                        tablerouteI.add(gatewayTextField);
                        tablerouteI.add(portnumberTextField);

                        tableRouteField.add(tablerouteI);

                        subnetTextField.setLayoutY(90 + 90 * (finalI) + 30 * (j[0]));
                        maskTextField.setLayoutY(90 + 90 * (finalI) + 30 * (j[0]));
                        gatewayTextField.setLayoutY(90 + 90 * (finalI) + 30 * (j[0]));
                        portnumberTextField.setLayoutY(90 + 90 * (finalI) + 30 * (j[0]));
                        deleteButton.setLayoutY(90 + 90 * (finalI) + 30 * (j[0]));

                        subnetTextField.setLayoutX(5);
                        maskTextField.setLayoutX(105);
                        gatewayTextField.setLayoutX(205);
                        portnumberTextField.setLayoutX(305);
                        deleteButton.setLayoutX(405);

                        subnetTextField.setPrefWidth(95);
                        maskTextField.setPrefWidth(95);
                        gatewayTextField.setPrefWidth(95);
                        portnumberTextField.setPrefWidth(95);


                        configPane.getChildren().addAll(subnetTextField, maskTextField, gatewayTextField, portnumberTextField, deleteButton);
                        actualiseGraph.add(subnetTextField);
                        actualiseGraph.add(maskTextField);
                        actualiseGraph.add(gatewayTextField);
                        actualiseGraph.add(portnumberTextField);
                        actualiseGraph.add(deleteButton);

                        j[0]++;

                    }
        });


        validationRouteButton.setOnAction(event -> {

                    if(!bugmode) {
                        ArrayList<ArrayList<Object>> tablerouteObject = new ArrayList<ArrayList<Object>>();

                        int n1 = tableRouteField.size();
                        try {
                            for (int i2 = 0; i2 < n1; i2++) {
                                if (tablerouteok.get(i2)) {
                                    ArrayList<Object> tablerouteI = new ArrayList<Object>();
                                    TextField textField1 = (TextField) tableRouteField.get(i2).get(0);
                                    TextField textField2 = (TextField) tableRouteField.get(i2).get(1);
                                    TextField textField3 = (TextField) tableRouteField.get(i2).get(2);
                                    TextField textField4 = (TextField) tableRouteField.get(i2).get(3);


                                    tablerouteI.add(IP.stringToIP(textField1.getText()));
                                    tablerouteI.add(IP.stringToIP(textField2.getText()));
                                    tablerouteI.add(IP.stringToIP(textField3.getText()));
                                    tablerouteI.add(Integer.parseInt(textField4.getText()));


                                    tablerouteObject.add(tablerouteI);
                                }

                            }
                            abstractRouter.setAllRoutes(tablerouteObject);
                            updateConfigMenuRouter(configStage,configPane, actualiseGraph, abstractRouter);
                        } catch (BadCallException e) {
                            afficheError("Route invalide");
                        } catch (NumberFormatException e) {
                            afficheError("Port invalide");
                        }

                    }

        });
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
            if (device.contains("pc")) {
                image = imagepc;
                if (!loadmode) {
                    if(device.contains("0")) {
                        array_list_of_devices.add(factory.newStandardPC());
                    }

                    AbstractClient abstractClient = (AbstractClient) array_list_of_devices.get(array_list_of_devices.size() - 1);

                    System.out.println(abstractClient.getName());
                    System.out.println(abstractClient.getId());
                    System.out.println(abstractClient.getInterfaceMAC(0));
                    array_list_of_devices.get(array_list_of_devices.size() - 1).start();
                }
            } else if (device.contains("server")) {
                image = imageserver;
                if (!loadmode) {
                    if(device.contains("0")) {
                        System.out.println("Server DHCP");
                        array_list_of_devices.add(factory.newDHCPServer());
                    } else if (device.contains("1")) {
                        System.out.println("Server FTP");
                        array_list_of_devices.add(factory.newStandardFTPServer());
                    } else if (device.contains("2")) {
                        System.out.println("Server WEB");
                        array_list_of_devices.add(factory.newStandardWEBServer());
                    }
                    System.out.println(array_list_of_devices.get(array_list_of_devices.size() - 1).getName());
                    System.out.println(array_list_of_devices.get(array_list_of_devices.size() - 1).getId());
                    array_list_of_devices.get(array_list_of_devices.size() - 1).start();
                }
            } else if (device.contains("router")) {
                image = imagerouteur;
                if (!loadmode) {
                    if(device.contains("0")) {
                        array_list_of_devices.add(factory.newStandard2ETHRouter());
                    } else if(device.contains("1")) {
                        array_list_of_devices.add(factory.newCiscoCRS1Router());
                    } else if(device.contains("2")) {
                        array_list_of_devices.add(factory.newCisco2811Router());
                    }
                    System.out.println(array_list_of_devices.get(array_list_of_devices.size() - 1).getName());
                    System.out.println(array_list_of_devices.get(array_list_of_devices.size() - 1).getId());

                    array_list_of_devices.get(array_list_of_devices.size() - 1).start();


                }
            } else if (device.contains("switch")) {
                image = imageswitch;
                if (!loadmode) {
                    if(device.contains("0")) {
                        array_list_of_devices.add(factory.newStandard24ETHSwitch());
                    }
                    System.out.println(array_list_of_devices.get(array_list_of_devices.size() - 1).getName());
                    System.out.println(array_list_of_devices.get(array_list_of_devices.size() - 1).getId());
                    array_list_of_devices.get(array_list_of_devices.size() - 1).start();
                }
            } else {
                image = imageerror;
            }




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


            //Surnom de l'appareil
            String surname = "";
            if(!loadmode) {
                surname = array_list_of_devices.get(array_list_of_image.indexOf(imageView)).getName() + " : " + device;
                array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname=surname;
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
                if(device.contains("0")) {
                    text1 = new Text("PC Standard");
                } else {
                    text1 = new Text ("Error");
                }

            } else if (device.contains("server")) {
                if(device.contains("0")) {
                    text1 = new Text("Server DHCP");
                } else if(device.contains("1")) {
                    text1 = new Text("Server FTP");
                } else if(device.contains("2")) {
                    text1 = new Text("Server WEB");
                } else {
                    text1 = new Text("Error");
                }

            } else if (device.contains("router")) {
                if(device.contains("0")) {
                    text1 = new Text("Router 2 Ports ETH");
                } else if(device.contains("1")) {
                    text1 = new Text("Router CISCO CRS1 12 Ports ETH");
                } else if(device.contains("2")) {
                    text1 = new Text("Router CISCO 2811 2 Ports ETH 1 Port SERIAL");
                } else {
                    text1 = new Text("Error");
                }

            } else if (device.contains("switch")) {
                if(device.contains("0")) {
                    text1 = new Text("Switch 24 Ports");
                } else {
                    text1 = new Text("Error");
                }
            } else {
                text1 = new Text("Error");
            }
            text1.setX(5);
            text1.setY(15);

            infoAnchorPane.getChildren().add(text1);


            imageView.setOnMouseEntered(event -> {
                if(!bugmode) {
                    infoAnchorPane.setLayoutX(imageView.getX() + imageView.getImage().getWidth());
                    infoAnchorPane.setLayoutY(imageView.getY());

                    affichePopupInfo(content, infoAnchorPane);


                }
            });

            imageView.setOnMouseExited(event -> {


                enlevePopupConfiq(content,infoAnchorPane);






            });


            //Item du menu contextuelle deplacé ici car on lui fait référence juste en dessous
            MenuItem itemOnOff = new MenuItem("ON");
            itemOnOff.setOnAction(event ->{
                reset("", status, content);

                if(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).isRunning()) {
                    array_list_of_devices.get(array_list_of_image.indexOf(imageView)).changeState(false);
                    itemOnOff.setText("ON");
                    System.out.println(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname +  " : I am OFF now");
                }else {
                    array_list_of_devices.get(array_list_of_image.indexOf(imageView)).changeState(true);
                    itemOnOff.setText("OFF");
                    System.out.println(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).nickname + " : I am ON now");
                }
                updateInfoMenu();

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
                on_off.setY(30);
                infoAnchorPane.getChildren().add(on_off);



                Text linkstate = new Text("Free ports : " + array_list_of_devices.get(array_list_of_image.indexOf(imageView)).getFreePorts().size());
                linkstate.setX(5);
                linkstate.setY(60);
                infoAnchorPane.getChildren().add(linkstate);


                Text isverboseactive = new Text("Verbose : OFF");
                isverboseactive.setX(5);
                isverboseactive.setY(45);
                infoAnchorPane.getChildren().add(isverboseactive);




        /*
             Fenetre de configuration
         */
            Stage configStage = new Stage();

            configStage.setOnCloseRequest(event -> {
                if(bugmode) {
                    event.consume();
                }
            });

            ScrollPane configScroll = new ScrollPane();
            AnchorPane configPane = new AnchorPane();

            configPane.setOnMouseClicked(event -> {

                if(DeviceManager.is_bugging()) {
                    Toolkit.getDefaultToolkit().beep();
                    DeviceManager.errorStage.toFront();
                }
            });


            configScroll.setContent(configPane);
            Scene configScene = new Scene(configScroll,500,500);

            configPane.setPrefWidth(480);
            configPane.setPrefHeight(480);
            configStage.setTitle(surname);
            configStage.setScene(configScene);
            configStage.sizeToScene();

            configStage.getIcons().add(new Image("file:sprites/arjl-logo.png"));

            arrayList_of_configStage.add(configStage);

            if(device.contains("pc")) {
                Button actualiseButton = new Button("Actualise");
                actualiseButton.setLayoutX(5);
                actualiseButton.setLayoutY(0);
                configPane.getChildren().add(actualiseButton);




                AbstractClient abstractClient = (AbstractClient) array_list_of_devices.get(array_list_of_image.indexOf(imageView));

                ArrayList <Object> actualiseGraph = new ArrayList<>();




                updateConfigMenuPc(configStage,configPane, actualiseGraph, abstractClient);


                actualiseButton.setOnAction(event -> {
                    if(!bugmode) {
                        try {
                            updateConfigMenuPc(configStage,configPane, actualiseGraph, abstractClient);
                        } catch (BadCallException e) {
                            e.printStackTrace();
                        }
                    }
                });


            } else if(device.contains("switch")) {
                AbstractSwitch abstractSwitch = (AbstractSwitch) array_list_of_devices.get(array_list_of_image.indexOf(imageView));

            } else if (device.contains("router")) {

                    Button actualiseButton = new Button("Actualise");
                    actualiseButton.setLayoutX(5);
                    actualiseButton.setLayoutY(0);
                    configPane.getChildren().add(actualiseButton);

                    AbstractRouter abstractRouter = (AbstractRouter) array_list_of_devices.get(array_list_of_image.indexOf(imageView));

                    ArrayList <Object> actualiseGraph = new ArrayList<>();

                    updateConfigMenuRouter(configStage,configPane,actualiseGraph,abstractRouter);

                    actualiseButton.setOnAction(event -> {
                        if(!bugmode) {
                            updateConfigMenuRouter(configStage,configPane, actualiseGraph, abstractRouter);

                        }
                    });


            } else if (device.contains("server")) {

                Button actualiseButton = new Button("Actualise");
                actualiseButton.setLayoutX(5);
                actualiseButton.setLayoutY(0);
                configPane.getChildren().add(actualiseButton);

                AbstractServer abstractServer = (AbstractServer) array_list_of_devices.get(array_list_of_image.indexOf(imageView));

                ArrayList <Object> actualiseGraph = new ArrayList<>();




                updateConfigMenuServer(configStage,configPane, actualiseGraph, abstractServer);


                actualiseButton.setOnAction(event -> {
                    if(!bugmode) {
                        try {
                            updateConfigMenuServer(configStage,configPane, actualiseGraph, abstractServer);
                        } catch (BadCallException e) {
                            e.printStackTrace();
                        }
                    }
                });
                } else {
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
                    enlevePopupConfiq(content,infoAnchorPane);
                    suprimerImage(imageView, content);
                } catch (BadCallException e) {
                    e.printStackTrace();
                }
                reset("", status, content);

            });
            MenuItem itemLink = new MenuItem("Créer Lien");

            itemLink.setOnAction(event -> {
                reset("addlink", status, content);
                firstimageView = imageView;
                firstimagesizeX = imageView.getImage().getWidth();
                firstimagesizeY = imageView.getImage().getHeight();
                addlinkmode = 4;
                status.setText("addLink 4");
                if(firstimageView.getAccessibleText().equals("pc")) {
                    firstimageView.setImage(imagepcselected);
                } else if (firstimageView.getAccessibleText().equals("server")){
                    firstimageView.setImage(imageserverselected);
                } else if (firstimageView.getAccessibleText().equals("router")){
                    firstimageView.setImage(imagerouteurselected);
                } else if (firstimageView.getAccessibleText().equals("switch")){
                    firstimageView.setImage(imageswitchselected);
                }

            });


            MenuItem itemMultipleLink = new MenuItem("Créer Lien Multiple");
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
                if(firstimageView.getAccessibleText().equals("pc")) {
                    firstimageView.setImage(imagepcselected);
                } else if (firstimageView.getAccessibleText().equals("server")){
                    firstimageView.setImage(imageserverselected);
                } else if (firstimageView.getAccessibleText().equals("router")){
                    firstimageView.setImage(imagerouteurselected);
                } else if (firstimageView.getAccessibleText().equals("switch")){
                    firstimageView.setImage(imageswitchselected);
                }

            });






            MenuItem itemDeleteLink = new MenuItem("Supprimer liens");
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
                        suprimerLien(list_element.get(i).line,content,status);
                    } catch (BadCallException e) {
                        e.printStackTrace();
                    }

                }

            });

            MenuItem itemCharge = new MenuItem("Afficher charge");
            itemCharge.setOnAction(event ->{
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

            itemVerbose.setOnAction(event ->{
                reset("", status, content);
                if(itemVerbose.getText().equals("Activate Verbose")){
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
            contextMenu.getItems().addAll(itemOnOff,itemVerbose,itemCharge);

            if(device.contains("pc")) {
                MenuItem itemRequestA = new MenuItem("Avancement request");
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



            MenuItem itemConfig = new MenuItem("Configuration");
            itemConfig.setOnAction(event -> {
                reset("", status, content);
                if(!configStage.isShowing()) {
                    configStage.show();
                } else {
                    configStage.toFront();
                }


            });










                    contextMenu.getItems().addAll(itemConfig);

            /*
                   Menu requete répétitive
             */

            if(device.contains("pc")) {
                AbstractClient abstractClient = (AbstractClient) array_list_of_devices.get(array_list_of_image.indexOf(imageView));

                Stage requestStage = new Stage();
                AnchorPane requestPane = new AnchorPane();

                requestPane.setOnMouseClicked(event -> {

                    if(DeviceManager.is_bugging()) {
                        Toolkit.getDefaultToolkit().beep();
                        DeviceManager.errorStage.toFront();
                    }
                });

                Scene requestScene = new Scene(requestPane,423,60);
                requestStage.setTitle(array_list_of_devices.get(array_list_of_image.indexOf(imageView)).getName() + " : " + device);
                requestStage.setScene(requestScene);
                requestStage.sizeToScene();

                requestStage.setOnCloseRequest(event -> {
                    if(bugmode) {
                        event.consume();
                    }
                });

                requestStage.getIcons().add(new Image("file:sprites/arjl-logo.png"));

                arrayList_of_requestStage.add(requestStage);


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
                    if(!bugmode) {
                        if (choiceBox.getSelectionModel().selectedIndexProperty().intValue() == -1) {
                            afficheError("Select a Type");
                        } else if (choiceBox.getSelectionModel().selectedIndexProperty().intValue() == 0) {


                            String string = textFieldIP.getText();

                            IP ip = null;
                            int time = 0;
                            try {


                                if(abstractClient.getConnectedLinks().get(0)==null) {
                                    throw new NotYetConnectedException();
                                }


                                ip = IP.stringToIP(string);
                                Integer integer = Integer.parseInt(textFieldTime.getText());
                                time= integer.intValue();
                                if(time<0) {
                                    throw new NumberFormatException();
                                }
                                System.out.println("OK");
                                abstractClient.setRepetitiveRequest(PacketTypes.FTP,ip,time);
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
                            System.out.println(string);
                            IP ip = null;
                            int time = 0;
                            try {

                                if(abstractClient.getConnectedLinks().get(0)==null) {
                                    throw new NotYetConnectedException();
                                }

                                ip = IP.stringToIP(string);
                                Integer integer = Integer.parseInt(textFieldTime.getText());
                                time= integer.intValue();
                                if(time<0) {
                                    throw new NumberFormatException();
                                }
                                abstractClient.setRepetitiveRequest(PacketTypes.WEB,ip,time);
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
                    if(!bugmode){
                        abstractClient.stopRepetitiveRequest();
                    }
                });

                MenuItem itemRequest = new MenuItem("Repetitive Request");
                itemRequest.setOnAction(event -> {
                    reset("", status, content);
                    if(!requestStage.isShowing()) {
                        requestStage.show();
                    } else {
                        requestStage.toFront();
                    }


                });

                contextMenu.getItems().add(itemRequest);
            }


            contextMenu.getItems().addAll(itemLink, itemMultipleLink, itemDeleteLink, itemDelete);


            imageView.setOnContextMenuRequested(event -> {
                if(!bugmode) {
                    contextMenu.show(imageView, Side.BOTTOM, event.getX() - imageView.getX(), event.getY() - imageView.getY() - imageView.getImage().getHeight());
                }
            });



            imageView.setOnMousePressed(event -> {
                if(!bugmode) {
                    if (event.isPrimaryButtonDown()) {
                        if (supressmode) {

                            try {
                                imageView.setOnMouseExited(eventnull -> {
                                    ;
                                });
                                enlevePopupConfiq(anchorPane,infoAnchorPane);
                                suprimerImage(imageView, content);
                            } catch (BadCallException e) {
                                e.printStackTrace();
                            }
                        }
                        if (addlinkmode == 1) {
                            if(firstimageView!=null) {
                            if(firstimageView.equals(imageView)) {

                            }} else {
                                entier = 0;
                            }
                            firstimageView = imageView;
                            firstimagesizeX = imageView.getImage().getWidth();
                            firstimagesizeY = imageView.getImage().getHeight();
                            addlinkmode = 2;

                            if(firstimageView.getAccessibleText().contains("pc")) {
                                firstimageView.setImage(imagepcselected);
                            } else if (firstimageView.getAccessibleText().contains("server")){
                                firstimageView.setImage(imageserverselected);
                            } else if (firstimageView.getAccessibleText().contains("router")){
                                firstimageView.setImage(imagerouteurselected);
                            } else if (firstimageView.getAccessibleText().contains("switch")){
                                firstimageView.setImage(imageswitchselected);
                            }

                            status.setText("addLink 1");
                        } else if (addlinkmode == 2) {
                            if (firstimageView != imageView) {
                                entier=0;
                                secondimageView = imageView;
                                secondimagesizeX = imageView.getImage().getWidth();
                                secondimagesizeY = imageView.getImage().getHeight();
                                addlinkmode = 3;
                                status.setText("addLink 2");
                                try {
                                    addlink(content, status, anchorPane);
                                } catch (BadCallException e) {
                                    e.printStackTrace();
                                }

                                if(firstimageView.getAccessibleText().contains("pc")) {
                                    firstimageView.setImage(imagepc);
                                } else if (firstimageView.getAccessibleText().contains("server")){
                                    firstimageView.setImage(imageserver);
                                } else if (firstimageView.getAccessibleText().contains("router")){
                                    firstimageView.setImage(imagerouteur);
                                } else if (firstimageView.getAccessibleText().contains("switch")){
                                    firstimageView.setImage(imageswitch);
                                }

                            } else {
                                entier++;
                                if (entier == 9) {


                                    updateLinkDrag(imageView);

                                    reset("",status,content);

                                    afficheError("it's OVER 9000 !!!!!");
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
                                    imageView.setImage(imagepipeau);
                                    updateInfoEtBarre();


                                }
                                if (entier > 9) {
                                    int a = entier;
                                    if (a<14) {
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
                                    addlink(content, status, anchorPane);
                                } catch (BadCallException e) {
                                    e.printStackTrace();
                                }
                                reset("", status, content);
                                addlinkmode = 5;
                                if(firstimageView.getAccessibleText().contains("pc")) {
                                    firstimageView.setImage(imagepc);
                                } else if (firstimageView.getAccessibleText().contains("server")){
                                    firstimageView.setImage(imageserver);
                                } else if (firstimageView.getAccessibleText().contains("router")){
                                    firstimageView.setImage(imagerouteur);
                                } else if (firstimageView.getAccessibleText().contains("switch")){
                                    firstimageView.setImage(imageswitch);
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
                                    addlink(content, status, anchorPane);
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
                            if(firstimageView.getAccessibleText().contains("pc")) {
                                firstimageView.setImage(imagepcselected);
                            } else if (firstimageView.getAccessibleText().contains("server")){
                                firstimageView.setImage(imageserverselected);
                            } else if (firstimageView.getAccessibleText().contains("router")){
                                firstimageView.setImage(imagerouteurselected);
                            } else if (firstimageView.getAccessibleText().contains("switch")){
                                firstimageView.setImage(imageswitchselected);
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

            /*
                Actualisation des liens
             */
                    updateLinkDrag(imageView);
                }
            });

            createmode=false;
        }


    }



}
