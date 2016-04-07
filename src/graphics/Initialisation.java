package graphics;


import exceptions.BadCallException;
import exceptions.OverflowException;
import graphics.JeNeSuisPasLa.Chat;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static graphics.DeviceManager.updateLink;

/**
 * Class qui créer la fenetre graphique
 */
public class Initialisation {

ActualizeThread actualizeThread = new ActualizeThread();

    /**
     * init
     * @param primaryStage  Fenetre
     */
    public void init (Stage primaryStage) {





        //AnchorPane principale
        AnchorPane anchorPane=new AnchorPane();
        anchorPane.setPrefHeight(1000);
        anchorPane.setPrefWidth(1000);

        //MenuBar : Café 1€, Croissant 0,80€
        MenuBar menubar=new MenuBar();
        menubar.setPrefHeight(29);
        menubar.setPrefWidth(1000);
        AnchorPane.setLeftAnchor(menubar,0.0);
        AnchorPane.setRightAnchor(menubar,0.0);

        anchorPane.getChildren().add(menubar);

        Menu filemenu = new Menu("File");
        filemenu.setMnemonicParsing(false);

        menubar.getMenus().add(filemenu);

        MenuItem openButton = new MenuItem("Open");
        openButton.setMnemonicParsing(false);
        filemenu.getItems().add(openButton);

        MenuItem saveButton = new MenuItem("Save");
        saveButton.setMnemonicParsing(false);
        saveButton.setDisable(true);
        filemenu.getItems().add(saveButton);

        MenuItem saveAsButton = new MenuItem("Save As");
        saveAsButton.setMnemonicParsing(false);
        filemenu.getItems().add(saveAsButton);

        MenuItem closeButton = new MenuItem("Close");
        closeButton.setMnemonicParsing(false);
        filemenu.getItems().add(closeButton);

        //A supprimer mais pas tout de suite
        MenuItem testButton = new MenuItem("TEST");
        testButton.setMnemonicParsing(false);
        filemenu.getItems().add(testButton);

        Menu editmenu = new Menu("Edit");
        editmenu.setMnemonicParsing(false);

        menubar.getMenus().add(editmenu);



        MenuItem toolboxmenu = new MenuItem("Toolbox");
        toolboxmenu.setMnemonicParsing(false);
        editmenu.getItems().add(toolboxmenu);

        MenuItem addlink = new MenuItem("Create link");
        addlink.setMnemonicParsing(false);
        editmenu.getItems().add(addlink);

        MenuItem delete = new MenuItem("Delete");
        delete.setMnemonicParsing(false);
        editmenu.getItems().add(delete);

        Menu viewmenu = new Menu("View");           // Menu View
        viewmenu.setMnemonicParsing(false);

        Menu textureMenu = new Menu("Texture");
        textureMenu.setMnemonicParsing(false);
        viewmenu.getItems().add(textureMenu);

        MenuItem texture1 = new MenuItem("Pack 1 ✓");
        texture1.setMnemonicParsing(false);
        textureMenu.getItems().add(texture1);

        MenuItem texture2 = new MenuItem("Pack 2");
        texture2.setMnemonicParsing(false);
        textureMenu.getItems().add(texture2);



        menubar.getMenus().add(viewmenu);




        Menu helpmenu = new Menu("Help");           // Menu Help
        helpmenu.setMnemonicParsing(false);

        menubar.getMenus().add(helpmenu);

        MenuItem helpButton = new MenuItem("Help");
        helpButton.setMnemonicParsing(false);
        helpmenu.getItems().add(helpButton);

        MenuItem aboutButton = new MenuItem("About");
        aboutButton.setMnemonicParsing(false);
        helpmenu.getItems().add(aboutButton);



        ToolBar controlebar = new ToolBar();
        controlebar.setLayoutY(29);
        controlebar.setPrefHeight(29);
        controlebar.setPrefWidth(1000);
        AnchorPane.setLeftAnchor(controlebar,0.0);
        AnchorPane.setRightAnchor(controlebar,0.0);
        anchorPane.getChildren().add(controlebar);


        Button playButton = new Button();
        playButton.setStyle("-fx-background-image: url('file:sprites/play.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
        playButton.setPrefWidth(20);
        playButton.setPrefHeight(20);

        playButton.setLayoutY(controlebar.getLayoutY()+1);

        anchorPane.getChildren().add(playButton);

        AnchorPane.setRightAnchor(playButton,0.0);
        playButton.setOnAction(event -> {
            try {
                DeviceManager.dhcpAll();
            } catch (OverflowException e) {
                e.printStackTrace();
            } catch (BadCallException e) {
                e.printStackTrace();
            }
        });
        playButton.toFront();



        ScrollPane scrollPane = new ScrollPane();
        anchorPane.getChildren().add(scrollPane);

        scrollPane.setLayoutX(0);
        scrollPane.setLayoutY(controlebar.getLayoutY()+controlebar.getPrefHeight());
        scrollPane.setPannable(false);
        scrollPane.setPrefHeight(200);
        scrollPane.setPrefWidth(200);
        AnchorPane.setBottomAnchor(scrollPane,0.0);
        AnchorPane.setLeftAnchor(scrollPane,0.0);
        AnchorPane.setRightAnchor(scrollPane,0.0);
        AnchorPane.setTopAnchor(scrollPane,controlebar.getLayoutY()+controlebar.getPrefHeight());




        AnchorPane content = new AnchorPane();
        scrollPane.setContent(content);

        content.setMinHeight(0);
        content.setMinWidth(0);
        content.setPrefWidth(4000);
        content.setPrefHeight(2000);

        //scrollPane.setStyle("-fx-background-image: url('file:sprites/wallpaper.png'); " +
               // "-fx-background-repeat: repeat;");

        scrollPane.setStyle("-fx-background-color: rgba(255, 255, 255, 1)");
        content.setStyle("-fx-background-image: url('file:sprites/wallpaper.png');");



        Label status = new Label("ON");
        //-------------------------------------------------
//On laisse la toolbar pour debug la toolbox


        scrollPane.setPrefHeight(anchorPane.getPrefHeight()-scrollPane.getLayoutY());
        scrollPane.setPrefWidth(anchorPane.getPrefWidth());

        content.setPrefHeight(4000);
        content.setPrefWidth(2000);

        anchorPane.getChildren().add(status);
        status.toFront();
        AnchorPane.setRightAnchor(status,0.0);
        AnchorPane.setTopAnchor(status,4.0);


        delete.setOnAction(event -> {
            DeviceManager.suprimodeactivate(status,content);
        });

        addlink.setOnAction(event -> {
            DeviceManager.addlinkmodeactivate(status,content);
        });


        saveAsButton.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
                SaveAndLoad saveAndLoad = new SaveAndLoad();
                saveAndLoad.Save(primaryStage,anchorPane,saveButton);
            }
        });

        saveButton.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
                SaveAndLoad saveAndLoad = new SaveAndLoad();
                saveAndLoad.SaveControleS(primaryStage,anchorPane);
            }
        });



        //Fonction Validation Sauvegarder
        AnchorPane savePane = new AnchorPane();
        savePane.setPrefHeight(100);
        savePane.setPrefWidth(200);
        savePane.setStyle("-fx-border-color: black;-fx-background-color: white ;");
        Text savetext = new Text();
        savetext.setText("Save ?");

        savetext.setX(80);
        savetext.setY(20);

        Button yessave = new Button("Yes");
        yessave.setLayoutY(60);
        yessave.setLayoutX(40);
        yessave.setOnAction(event -> {
            SaveAndLoad saveAndLoad = new SaveAndLoad();
            saveAndLoad.Save(primaryStage,anchorPane,saveButton);
            DeviceManager.stopThread();
            actualizeThread.stop();
            DeviceManager.closeEvryConfigStage();
            DeviceManager.closeEvryRequestStage();

            primaryStage.close();
        });
        Button nosave = new Button("No");
        nosave.setLayoutY(60);
        nosave.setLayoutX(120);
        nosave.setOnAction(event -> {

            SaveAndLoad saveAndLoad = new SaveAndLoad();
            DeviceManager.stopThread();
            actualizeThread.stop();
            DeviceManager.closeEvryConfigStage();
            DeviceManager.closeEvryRequestStage();
            primaryStage.close();
        });
        savePane.getChildren().addAll(savetext,yessave,nosave);

        //Fenetre validation quitter
        AnchorPane exitPane = new AnchorPane();
        exitPane.setPrefHeight(100);
        exitPane.setPrefWidth(200);
        exitPane.setStyle("-fx-border-color: black;-fx-background-color: white ;");
        Text exittext = new Text();
        exittext.setText("Are you sure ?");

        exittext.setX(60);
        exittext.setY(20);

        Button yesexit = new Button("Yes");
        yesexit.setLayoutY(60);
        yesexit.setLayoutX(40);
        yesexit.setOnAction(event -> {
            anchorPane.getChildren().remove(exitPane);
            savePane.setLayoutX(anchorPane.getWidth()/2-100);
            savePane.setLayoutY((anchorPane.getHeight()-63)/2-50);
            anchorPane.getChildren().add(savePane);
        });
        Button noexit = new Button("No");
        noexit.setLayoutY(60);
        noexit.setLayoutX(120);
        noexit.setOnAction(event -> {
            anchorPane.getChildren().remove(exitPane);
        });
        exitPane.getChildren().addAll(exittext,yesexit,noexit);


        closeButton.setOnAction(event -> {

            if(!DeviceManager.is_bugging()) {
                exitPane.setLayoutX(anchorPane.getWidth() / 2 - 100);
                exitPane.setLayoutY((anchorPane.getHeight() - 63) / 2 - 50);

                if (anchorPane.getChildren().contains(exitPane)) {
                    anchorPane.getChildren().remove(exitPane);
                }
                if (anchorPane.getChildren().contains(savePane)) {

                } else {
                    anchorPane.getChildren().add(exitPane);
                }
            }

        });


        //Fenêtre externe de la toolbox
        AnchorPane toolboxPane = new AnchorPane();
        Scene scene = new Scene(toolboxPane,130,350);
        Stage toolboxstage = new Stage();
        toolboxstage.setTitle("ToolBox");
        toolboxstage.setScene(scene);
        toolboxstage.sizeToScene();





        // Mise en place de la toolbox sur le côté

        AnchorPane toolBox = new AnchorPane();
        toolBox.setStyle("-fx-border-color: black;-fx-background-color: white ;");
        toolBox.setPrefSize(130, 350);
        toolBox.setLayoutY(scrollPane.getLayoutY()+1);

        anchorPane.getChildren().add(toolBox);








        Label toolBarLabel = new Label("Tool Box");
        toolBox.getChildren().add(toolBarLabel);

        toolBarLabel.setLayoutX(toolBox.getPrefWidth()/2-30); //TODO Améliorer cette merde.

        toolBarLabel.setLayoutY(5);

        // Outil PC

        ContextMenu contextMenuPc = new ContextMenu();

        MenuItem typepc0= new MenuItem("Standard ✓");
        contextMenuPc.getItems().add(typepc0);

        Button pcButton = new Button();
        pcButton.setLayoutX(10);
        pcButton.setLayoutY(30);
        pcButton.setPrefSize(50, 50);
        pcButton.setStyle("-fx-background-image: url('file:sprites/pack1/pc-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
        pcButton.toFront();
        toolBox.getChildren().add(pcButton);
        pcButton.setOnAction(event -> {
            try {
                DeviceManager.device("pc"+DeviceManager.getModelepc(),anchorPane,scrollPane,content,status,primaryStage);
            } catch (BadCallException e) {
                e.printStackTrace();
            }
        });

        pcButton.setContextMenu(contextMenuPc);

        // Outil server

        ContextMenu contextMenuServer = new ContextMenu();

        MenuItem typeserver0= new MenuItem("DHCP ✓");
        contextMenuServer.getItems().add(typeserver0);

        MenuItem typeserver1= new MenuItem("FTP");
        contextMenuServer.getItems().add(typeserver1);

        MenuItem typeserver2= new MenuItem("WEB");
        contextMenuServer.getItems().add(typeserver2);

        typeserver0.setOnAction(event -> {
            typeserver0.setText("DHCP ✓");
            typeserver1.setText("FTP");
            typeserver2.setText("WEB");
            DeviceManager.setModeleserver(0);

        });

        typeserver1.setOnAction(event -> {
            typeserver0.setText("DHCP");
            typeserver1.setText("FTP ✓");
            typeserver2.setText("WEB");
            DeviceManager.setModeleserver(1);

        });

        typeserver2.setOnAction(event -> {
            typeserver0.setText("DHCP");
            typeserver1.setText("FTP");
            typeserver2.setText("WEB ✓");
            DeviceManager.setModeleserver(2);

        });


        Button serverButton = new Button();
        serverButton.setLayoutX(70);
        serverButton.setLayoutY(30);
        serverButton.setPrefSize(50, 50);
        serverButton.setStyle("-fx-background-image: url('file:sprites/pack1/server-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
        serverButton.toFront();
        toolBox.getChildren().add(serverButton);
        serverButton.setOnAction(event -> {
            try {
                DeviceManager.device("server"+DeviceManager.getModeleserver(),anchorPane,scrollPane,content,status,primaryStage);
            } catch (BadCallException e) {
                e.printStackTrace();
            }
        });

        serverButton.setContextMenu(contextMenuServer);
        // Outil router


        ContextMenu contextMenuRouter = new ContextMenu();

        MenuItem typerouter0= new MenuItem("Standard 2 Ports ETH ✓");
        contextMenuRouter.getItems().add(typerouter0);

        MenuItem typerouter1= new MenuItem("CISCO CRS1 12 Ports ETH");
        contextMenuRouter.getItems().add(typerouter1);

        MenuItem typerouter2= new MenuItem("CISCO 2811 2 Ports ETH 1 Port SERIAL");
        contextMenuRouter.getItems().add(typerouter2);

        typerouter0.setOnAction(event -> {
            typerouter0.setText("Standard 2 Ports ETH ✓");
            typerouter1.setText("CISCO CRS1 12 Ports ETH");
            typerouter2.setText("CISCO 2811 2 Ports ETH 1 Port SERIAL");
            DeviceManager.setModelerouter(0);

        });

        typerouter1.setOnAction(event -> {
            typerouter0.setText("Standard 2 Ports ETH");
            typerouter1.setText("CISCO CRS1 12 Ports ETH ✓");
            typerouter2.setText("CISCO 2811 2 Ports ETH 1 Port SERIAL");
            DeviceManager.setModelerouter(1);

        });

        typerouter2.setOnAction(event -> {
            typerouter0.setText("Standard 2 Ports ETH");
            typerouter1.setText("CISCO CRS1 12 Ports ETH");
            typerouter2.setText("CISCO 2811 2 Ports ETH 1 Port SERIAL ✓");
            DeviceManager.setModelerouter(2);
        });

        Button routerButton = new Button();
        routerButton.setLayoutX(10);
        routerButton.setLayoutY(85);
        routerButton.setPrefSize(50, 50);
        routerButton.setStyle("-fx-background-image: url('file:sprites/pack1/router2-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
        routerButton.toFront();
        toolBox.getChildren().add(routerButton);
        routerButton.setOnAction(event -> {
            try {
                System.out.println(DeviceManager.getModelerouter());
                DeviceManager.device("router"+DeviceManager.getModelerouter(),anchorPane,scrollPane,content,status,primaryStage);
            } catch (BadCallException e) {
                e.printStackTrace();
            }
        });

        routerButton.setContextMenu(contextMenuRouter);


        // Outil switch
        ContextMenu contextMenuSwitch = new ContextMenu();

        MenuItem typeswitch0= new MenuItem("Standard 24 Ports ETH ✓");
        contextMenuSwitch.getItems().add(typeswitch0);

        Button switchButton = new Button();
        switchButton.setLayoutX(70);
        switchButton.setLayoutY(85);
        switchButton.setPrefSize(50, 50);
        switchButton.setStyle("-fx-background-image: url('file:sprites/pack1/switch-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
        switchButton.toFront();
        toolBox.getChildren().add(switchButton);
        switchButton.setOnAction(event -> {
            try {
                DeviceManager.device("switch"+DeviceManager.getModeleswitch(),anchorPane,scrollPane,content,status,primaryStage);
            } catch (BadCallException e) {
                e.printStackTrace();
            }
        });

        switchButton.setContextMenu(contextMenuSwitch);

        // Changer pack


        texture1.setOnAction(event -> {
            DeviceManager.reset("",status,content);
            Image imagepc=new Image("file:sprites/pack1/pc.png");
            Image imageserver = new Image("file:sprites/pack1/server.png");
            Image imagerouter = new Image("file:sprites/pack1/router2.png");
            Image imageswitch = new Image("file:sprites/pack1/switch.png");

            DeviceManager.setpacktexture(1);

            ArrayList <ImageView> arrayList_of_device = DeviceManager.getArray_list_of_image();
            arrayList_of_device.forEach(imageView -> {
                if(imageView.getAccessibleText().contains("pc")) {
                    imageView.setImage(imagepc);

                } else if(imageView.getAccessibleText().contains("server")) {
                    imageView.setImage(imageserver);

                } else if(imageView.getAccessibleText().contains("router")) {
                    imageView.setImage(imagerouter);

                } else if(imageView.getAccessibleText().contains("switch")) {
                    imageView.setImage(imageswitch);

                }


            });






            pcButton.setStyle("-fx-background-image: url('file:sprites/pack1/pc-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
            routerButton.setStyle("-fx-background-image: url('file:sprites/pack1/router2-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
            serverButton.setStyle("-fx-background-image: url('file:sprites/pack1/server-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
            switchButton.setStyle("-fx-background-image: url('file:sprites/pack1/switch-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");

            texture1.setText("Pack 1 ✓");
            texture2.setText("Pack 2");

            DeviceManager.updateLinkDrag();
            DeviceManager.updateInfoEtBarre();
        });







        texture2.setOnAction(event -> {
            DeviceManager.reset("",status,content);
            Image imagepc=new Image("file:sprites/pack2/pc.png");
            Image imageserver = new Image("file:sprites/pack2/server.png");
            Image imagerouter = new Image("file:sprites/pack2/router2.png");
            Image imageswitch = new Image("file:sprites/pack2/switch.png");

            DeviceManager.setpacktexture(2);

            ArrayList <ImageView> arrayList_of_device = DeviceManager.getArray_list_of_image();
            arrayList_of_device.forEach(imageView -> {
                if(imageView.getAccessibleText().contains("pc")) {
                    imageView.setImage(imagepc);

                } else if(imageView.getAccessibleText().contains("server")) {
                    imageView.setImage(imageserver);

                } else if(imageView.getAccessibleText().contains("router")) {
                    imageView.setImage(imagerouter);

                } else if(imageView.getAccessibleText().contains("switch")) {
                    imageView.setImage(imageswitch);

                }


            });






            pcButton.setStyle("-fx-background-image: url('file:sprites/pack2/pc-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
            routerButton.setStyle("-fx-background-image: url('file:sprites/pack2/router2-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
            serverButton.setStyle("-fx-background-image: url('file:sprites/pack2/server-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
            switchButton.setStyle("-fx-background-image: url('file:sprites/pack2/switch-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");

            texture1.setText("Pack 1");
            texture2.setText("Pack 2 ✓");

            DeviceManager.updateLinkDrag();
            DeviceManager.updateInfoEtBarre();
        });


        // Mode suppression (gomme) :

        Button rubberButton = new Button();
        rubberButton.setLayoutX(70);
        rubberButton.setLayoutY(185);
        rubberButton.setPrefSize(50, 50);
        rubberButton.setStyle("-fx-background-image: url('file:sprites/gomme-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
        rubberButton.toFront();
        toolBox.getChildren().add(rubberButton);
        rubberButton.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
                DeviceManager.suprimodeactivate(status, content);
            }
        });

        // Mode Drag/Drop :

        Button crossButton = new Button();
        crossButton.setLayoutX(10);
        crossButton.setLayoutY(185);
        crossButton.setPrefSize(50, 50);
        crossButton.setStyle("-fx-background-image: url('file:sprites/cross-icon-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
        crossButton.toFront();
        toolBox.getChildren().add(crossButton);
        crossButton.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
                DeviceManager.reset("", status, content);
            }
        });

        // Mode Lien :

        Button linkButton = new Button();
        linkButton.setLayoutX(10);
        linkButton.setLayoutY(245);
        linkButton.setPrefSize(50, 50);
        linkButton.setStyle("-fx-background-image: url('file:sprites/link-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
        linkButton.toFront();
        toolBox.getChildren().add(linkButton);
        linkButton.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
                DeviceManager.addlinkmodeactivate(status, content);
            }
        });

        // Mode Liens multiples :

        Button multiplelinkButton = new Button();
        multiplelinkButton.setLayoutX(70);
        multiplelinkButton.setLayoutY(245);
        multiplelinkButton.setPrefSize(50, 50);
        multiplelinkButton.setStyle("-fx-background-image: url('file:sprites/multiple-link-mini.png');-fx-background-repeat: no-repeat;-fx-background-position: center;");
        multiplelinkButton.toFront();
        toolBox.getChildren().add(multiplelinkButton);
        multiplelinkButton.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
                DeviceManager.addlinkmodeactivate(status, content);
                DeviceManager.setAddlinkmode(7);
                status.setText("addLink 6");
            }

        });


        //





        // Croix de la toolbox
        ImageView exitView = new ImageView();
        Image exit;
        exit = new Image("file:sprites/exit.png");
        Image exit2;
        exit2 = new Image("file:sprites/exit2.png");
        exitView.setImage(exit);
        exitView.setX(toolBox.getPrefWidth()-exit.getWidth());
        exitView.setY(2);
        toolBox.getChildren().add(exitView);

        exitView.setOnMouseEntered(event ->{
            exitView.setImage(exit2);

        });
        exitView.setOnMouseExited(event ->{
            exitView.setImage(exit);

        });

        // Retour interne de la toolbox
        ImageView restoreView = new ImageView();
        Image restore;
        restore = new Image("file:sprites/restore1-mini.png");
        Image restore2;
        restore2 = new Image("file:sprites/restore2-mini.png");
        restoreView.setImage(restore);
        restoreView.setX(toolBox.getPrefWidth()-restore.getWidth()-exit.getWidth()-2);
        restoreView.setY(2);
        toolBox.getChildren().add(restoreView);
        restoreView.setOnMousePressed(event -> {
            if(event.isPrimaryButtonDown() && !DeviceManager.is_bugging()) {
                if (toolboxstage.isShowing()) {
                    toolboxstage.close();
                    toolboxPane.getChildren().remove(toolBox);
                    toolBox.setLayoutX(0);
                    toolBox.setLayoutY(scrollPane.getLayoutY() + 1);
                    toolBox.setPrefSize(130, 350);
                    exitView.setX(toolBox.getPrefWidth() - exit.getWidth());
                    restoreView.setX(toolBox.getPrefWidth() - restore.getWidth() - exit.getWidth() - 2);
                    anchorPane.getChildren().add(toolBox);
                } else {
                    System.out.println("Let's go Outside");
                    toolBox.setLayoutY(0);
                    toolBox.setLayoutX(0);
                    toolBox.setPrefSize(129, 350);
                    restoreView.setX(toolBox.getPrefWidth()-restore.getWidth()-exit.getWidth()-2);

                    exitView.setX(toolBox.getPrefWidth()-exit.getWidth());
                    anchorPane.getChildren().remove(toolBox);
                    toolboxPane.getChildren().add(toolBox);
                    toolboxstage.setX(event.getScreenX());
                    toolboxstage.setY(event.getScreenY());
                    toolboxstage.show();

                }
            }
        });
        restoreView.setOnMouseEntered(event ->{
            restoreView.setImage(restore2);

        });
        restoreView.setOnMouseExited(event ->{
            restoreView.setImage(restore);

        });

        //Drag and drop de la toolbox et fonctions qui ne peuvent pas se trouver plus haut
        exitView.setOnMousePressed(event -> {
            if(event.isPrimaryButtonDown() && !DeviceManager.is_bugging()) {
                if(anchorPane.getChildren().contains(toolBox)) {
                    anchorPane.getChildren().remove(toolBox);
                }
                if (toolboxPane.getChildren().contains(toolBox)) {
                    toolboxstage.close();
                    toolboxPane.getChildren().remove(toolBox);

                }

            }

        });
        toolboxmenu.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
                if (anchorPane.getChildren().contains(toolBox)) {
                    anchorPane.getChildren().remove(toolBox);

                } else if (toolboxPane.getChildren().contains(toolBox)){
                    toolboxPane.getChildren().remove(toolBox);
                } else {
                    toolBox.setLayoutX(0);
                    toolBox.setLayoutY(scrollPane.getLayoutY()+1);
                    toolBox.setPrefSize(130, 350);
                    exitView.setX(toolBox.getPrefWidth()-exit.getWidth());
                    restoreView.setX(toolBox.getPrefWidth()-restore.getWidth()-exit.getWidth()-2);
                    anchorPane.getChildren().add(toolBox);
                }
            }
        });
        toolboxstage.setOnCloseRequest(event -> {
            toolboxPane.getChildren().remove(toolBox);

        });
        /**
         * Voici la raison de ne plus passer par le FXML
         */
        primaryStage.setOnCloseRequest(event2 ->{
            event2.consume();
            exitPane.setLayoutX(anchorPane.getWidth()/2-100);
            exitPane.setLayoutY((anchorPane.getHeight()-63)/2-50);
            if(!DeviceManager.is_bugging()) {
                exitPane.setLayoutX(anchorPane.getWidth() / 2 - 100);
                exitPane.setLayoutY((anchorPane.getHeight() - 63) / 2 - 50);

                if (anchorPane.getChildren().contains(exitPane)) {
                    anchorPane.getChildren().remove(exitPane);
                }
                if (anchorPane.getChildren().contains(savePane)) {

                } else {
                    anchorPane.getChildren().add(exitPane);
                }
                if(DeviceManager.verboseStage.isShowing()) {
                    DeviceManager.verboseStage.close();
                }
            }



        });

        toolBox.setOnMouseDragged(event -> {
            if(!DeviceManager.is_bugging() && event.isPrimaryButtonDown()) {
                double x = event.getSceneX() - toolBox.getPrefWidth() / 2;
                double y = event.getSceneY() - 30;





                if ((x < 0 - 200 || y <= scrollPane.getLayoutY() - 200 || x + toolBox.getWidth() + 16 >= anchorPane.getWidth() + 200 || y + toolBox.getHeight() + 16 >= anchorPane.getHeight() + 200) && anchorPane.getChildren().contains(toolBox)) {
                    System.out.println("Let's go Outside");
                    toolBox.setLayoutY(0);
                    toolBox.setLayoutX(0);
                    toolBox.setPrefSize(129, 350);
                    restoreView.setX(toolBox.getPrefWidth()-restore.getWidth()-exit.getWidth()-2);

                    exitView.setX(toolBox.getPrefWidth()-exit.getWidth());
                    anchorPane.getChildren().remove(toolBox);
                    toolboxPane.getChildren().add(toolBox);
                    toolboxstage.setX(event.getScreenX());
                    toolboxstage.setY(event.getScreenY());
                    toolboxstage.show();


                } else if (anchorPane.getChildren().contains(toolBox)) {
                    if (x < 0) {
                        x = 0;
                    }
                    if (y <= scrollPane.getLayoutY()) {
                        y = scrollPane.getLayoutY() + 1;
                    }


                    if (x + toolBox.getWidth() + 16 >= anchorPane.getWidth()) {
                        x = anchorPane.getWidth() - toolBox.getWidth() - 16;
                    }

                    if (y + toolBox.getHeight() + 16 >= anchorPane.getHeight()) {
                        y = anchorPane.getHeight() - toolBox.getHeight() - 16;
                    }

                    toolBox.relocate(x, y);
                } else if (toolboxPane.getChildren().contains(toolBox)){
                    toolboxstage.setX(event.getScreenX()- toolBox.getPrefWidth() / 2);
                    toolboxstage.setY(event.getScreenY()- 50);
                }
            }
        });
        //Bouton Open
        openButton.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {

                SaveAndLoad saveAndLoad = new SaveAndLoad();
                saveAndLoad.Load(primaryStage,content,anchorPane,status,scrollPane,saveButton);



            }
        });
        //Fonction du bouton TEST
        testButton.setOnAction(event -> {


            try {
                DeviceManager.updateLink();
            } catch (BadCallException e) {
                e.printStackTrace();
            }
            /*
            if(!DeviceManager.verboseStage.isShowing()) {
                DeviceManager.verboseStage.show();
            } else {
                DeviceManager.addLineVerbose("Bite");
            }
            */

        });












        helpButton.setOnAction(event -> { // Le bouton Help envoie vers le sommaire de l'aide

            DesktopApi.open(new File("doc/index.html"));

        });

        aboutButton.setOnAction(event -> { // Et le bouton About vers la section Contact, directement.
            DesktopApi.open(new File("doc/contact.html"));
        });





        DeviceManager.initErrorStage();
        //DeviceManager.initVerboseStage();


        //---------------------------------------------------
        Scene primaryscene = new Scene(anchorPane, 1000, 700);


        primaryStage.setTitle("ARJL : New Project");
        primaryStage.setScene(primaryscene);
        primaryStage.sizeToScene();
        primaryStage.show();
        primaryStage.getIcons().add(new Image("file:sprites/arjl-logo.png"));

        anchorPane.setOnMouseClicked(event -> {

            if(DeviceManager.is_bugging()) {
                Toolkit.getDefaultToolkit().beep();
                DeviceManager.errorStage.toFront();
            }
        });







        //On démarre le Thread d'actualisation
        actualizeThread.start();

    }


}
