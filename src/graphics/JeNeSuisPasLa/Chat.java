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

package graphics.JeNeSuisPasLa;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.File;

/**
 * Ligne à ajouté dans l'Initialisation
 *
 * Chat chat = new Chat();
 * chat.Lapin(primaryStage,anchorPane,filemenu);
 *
 * LIGNE à ajouté dans le DeviceManager
 *
 * private static int entier=0;
 */
/*
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
 */

    /*
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
     */

public class Chat {

    public void Lapin(Stage primaryStage, AnchorPane anchorPane, Menu filemenu) {
        MenuItem nePasCliquerButton = new MenuItem("Ne pas cliquer");
        nePasCliquerButton.setMnemonicParsing(false);
        filemenu.getItems().add(nePasCliquerButton);

        nePasCliquerButton.setOnAction(event -> {
            double x = Math.random();
            Media media = new Media(new File("video/video1.mp4").toURI().toASCIIString());


            if (x > 0.5) {
                media = new Media(new File("video/video1.mp4").toURI().toASCIIString());
            } else {
                media = new Media(new File("video/video2.mp4").toURI().toASCIIString());
            }

            MediaPlayer player = new MediaPlayer(media);
            player.setAutoPlay(true);

            MediaView mediaView = new MediaView(player);


            primaryStage.setFullScreen(true);


            Image black = new Image("file:sprites/Noir.jpg");
            ImageView imageViewBlack = new ImageView(black);
            anchorPane.getChildren().add(imageViewBlack);

            anchorPane.getChildren().add(mediaView);


            double ratio = primaryStage.getWidth() / primaryStage.getHeight();


            mediaView.setLayoutX((primaryStage.getWidth() - 4 * primaryStage.getHeight() / 3) / 2);
            mediaView.setLayoutY(0);

            mediaView.setFitHeight(primaryStage.getHeight());

            VideoThread videoThread = new VideoThread(mediaView, primaryStage);
            videoThread.start();

            player.setOnEndOfMedia(new Runnable() {
                @Override
                public void run() {
                    // actions here e.g.:
                    anchorPane.getChildren().remove(mediaView);
                    anchorPane.getChildren().remove(imageViewBlack);
                    primaryStage.setFullScreen(false);
                    videoThread.stop();

                }
            });
        });

        /*
        ImageView pipeau = new ImageView();
        Image image1 = new Image("file:sprites/pipeau.png");
        Image image2 = new Image("file:sprites/pipfire.gif");
        pipeau.setImage(image1);
        pipeau.setX(200);
        pipeau.setY(200);
        anchorPane.getChildren().add(pipeau);






        Button pip = new Button();
        pip.setText("Pipeau ?");
        pip.setLayoutX(400);
        pip.setLayoutY(200);
        anchorPane.getChildren().add(pip);

        Button oui = new Button();
        oui.setText("oui");
        oui.setLayoutX(400);
        oui.setLayoutY(250);
        anchorPane.getChildren().add(oui);

        Button non = new Button();
        non.setText("non");
        non.setLayoutX(400);
        non.setLayoutY(300);
        anchorPane.getChildren().add(non);

        Button bravo = new Button();
        bravo.setText("BRAVO !!!!");
        bravo.setLayoutX(4000000);
        bravo.setLayoutY(3000000);
        anchorPane.getChildren().add(bravo);


        oui.setOnAction(event -> {
            pip.setLayoutX(4000000);
            pip.setLayoutY(2000000);
            oui.setLayoutX(4000000);
            oui.setLayoutY(2500000);
            non.setLayoutX(4000000);
            non.setLayoutY(3000000);
            pipeau.setImage(image2);
            bravo.setLayoutX(300);
            bravo.setLayoutY(400);



        double imagx = non.getWidth();
        double imagy = non.getHeight();

        non.setOnMouseEntered(event -> {

            double x=event.getSceneX();
            double y=event.getSceneY();
            x=x+200;
            y=y+200;
            double mx = anchorPane.getWidth();
            double my = anchorPane.getHeight();

            if(x>mx) {x=imagx;}
            if(y>my) {y=imagx;}
            non.relocate(x-imagx/2, y-imagy/2);
        });
        */
    }

    /*
    public void Lievre() {

                    if (addlinkmode == 1) {
                        if(firstimageView.equals(imageView)) {
                            entier++;
                        } else {
                            entier = 0;
                        }
                        firstimageView = imageView;
                        firstimagesizeX = imageView.getImage().getWidth();
                        firstimagesizeY = imageView.getImage().getHeight();
                        addlinkmode = 2;

                        if(firstimageView.getAccessibleText().equals("pc")) {
                            firstimageView.setImage(imagepcselected);
                        } else if (firstimageView.getAccessibleText().equals("server")){
                            firstimageView.setImage(imageserverselected);
                        } else if (firstimageView.getAccessibleText().equals("router")){
                            firstimageView.setImage(imagerouteurselected);
                        } else if (firstimageView.getAccessibleText().equals("switch24")){
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

                            if(firstimageView.getAccessibleText().equals("pc")) {
                                firstimageView.setImage(imagepc);
                            } else if (firstimageView.getAccessibleText().equals("server")){
                                firstimageView.setImage(imageserver);
                            } else if (firstimageView.getAccessibleText().equals("router")){
                                firstimageView.setImage(imagerouteur);
                            } else if (firstimageView.getAccessibleText().equals("switch24")){
                                firstimageView.setImage(imageswitch);
                            }

                        } else {
                            entier++;
                            if (entier == 9) {
                                System.out.println("chat");

                                updateLinkDrag(imageView);

                                reset("",status,content);

                                afficheError("it's OVER 9000 !!!!!", anchorPane);
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
                            if (entier == 18) {
                                Media media = new Media(new File("video/video1.mp4").toURI().toASCIIString());
                                MediaPlayer player = new MediaPlayer(media);
                                player.setAutoPlay(true);

                                MediaView mediaView = new MediaView(player);

                                primaryStage.setFullScreen(true);



                                Image black = new Image("file:sprites/Noir.jpg");
                                ImageView imageViewBlack = new ImageView(black);
                                anchorPane.getChildren().add(imageViewBlack);

                                anchorPane.getChildren().add(mediaView);




                                double ratio=primaryStage.getWidth()/primaryStage.getHeight();




                                mediaView.setLayoutX((primaryStage.getWidth()-4*primaryStage.getHeight()/3)/2);
                                mediaView.setLayoutY(0);

                                mediaView.setFitHeight(primaryStage.getHeight());

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
    }
    */
}
