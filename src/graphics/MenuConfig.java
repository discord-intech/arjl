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

import exceptions.BadCallException;
import exceptions.BadWANTable;
import exceptions.OverflowException;
import hardware.client.AbstractClient;
import hardware.router.AbstractRouter;
import hardware.router.WANPort;
import hardware.server.AbstractServer;
import hardware.server.DHCPServer;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import packet.IP;

import java.net.ConnectException;
import java.util.ArrayList;

import static graphics.DeviceManager.afficheError;
import static graphics.DeviceManager.mainThread;

/**
 * Created by Louis-Baptiste on 2016-05-12.
 */
public class MenuConfig {

    /**
     * Fonction qui initialise et actualise les menus de confique
     */

    public static void updateConfigMenuPc (Stage configStage, AnchorPane configPane, ArrayList<Object> actualiseGraph, AbstractClient abstractClient, Text surnom) throws BadCallException {



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

        Button surnameButton = new Button ("Ok");
        surnameButton.setLayoutX(300);
        surnameButton.setLayoutY(0);
        configPane.getChildren().add(surnameButton);
        actualiseGraph.add(surnameButton);

        surnameButton.setOnAction(event -> {
            String surname=textFieldsurname.getText();
            abstractClient.nickname=surname;
            surnom.setText(surname);
            configStage.setTitle(surname);

            if(surname.equals("aymeric") | surname.equals("Aymeric")) {

                DeviceManager.afficheError("Je suis un PIPO");


            }
        });

        Text iptext = new Text("Ip : ");
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

        Text masktext = new Text("Mask : ");
        masktext.setLayoutX(155);
        masktext.setLayoutY(45);
        configPane.getChildren().add(masktext);
        actualiseGraph.add(masktext);

        TextField maskField = null;

        try {
            maskField = new TextField(abstractClient.getIntefaceMask(0).toString());
        } catch (BadCallException e) {
            afficheError("Error 23");
        }
        configPane.getChildren().add(maskField);
        actualiseGraph.add(maskField);
        maskField.setLayoutX(155);
        maskField.setLayoutY(60);

        Button validationButton = new Button("Ok");
        validationButton.setLayoutY(60);
        validationButton.setLayoutX(310);
        final TextField finalTextField = textField;
        final TextField finalMaskField = maskField;
        validationButton.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
                String ipstring = finalTextField.getText();
                String maskstring = finalMaskField.getText();

                Boolean isAnIp = true;

                IP ip = null;
                IP mask = null;

                try {
                    ip = IP.stringToIP(ipstring);
                    mask = IP.stringToIP(maskstring);
                } catch (BadCallException e) {
                    afficheError("Wrong Argument : Not an IP");
                    try {
                        finalTextField.setText(abstractClient.getInterfaceIP(0).toString());
                        finalMaskField.setText(abstractClient.getIntefaceMask(0).toString());
                    } catch (BadCallException e1) {
                        e1.printStackTrace();
                    }
                    isAnIp = false;
                }

                if (isAnIp) {
                    abstractClient.configureIP(0, ip);
                    abstractClient.configureMask(0, mask);

                }


            }
         });

        configPane.getChildren().add(validationButton);
        actualiseGraph.add(validationButton);

        Text gatewaytext = new Text("Gateway : ");
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

        Button validationButtongateway = new Button("Ok");
        validationButtongateway.setLayoutY(120);
        validationButtongateway.setLayoutX(200);
        final TextField finalTextFieldgateway = textFieldgateway;
        validationButtongateway.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
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

    public static void updateConfigMenuServer (Stage configStage, AnchorPane configPane, ArrayList<Object> actualiseGraph, AbstractServer abstractServer, Text surnom) throws BadCallException {


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

        Button surnameButton = new Button ("Ok");
        surnameButton.setLayoutX(300);
        surnameButton.setLayoutY(0);
        configPane.getChildren().add(surnameButton);
        actualiseGraph.add(surnameButton);

        surnameButton.setOnAction(event -> {
            String surname=textFieldsurname.getText();
            abstractServer.nickname=surname;
            surnom.setText(surname);
            configStage.setTitle(surname);
            if(surname.equals("aymeric") | surname.equals("Aymeric")) {

                DeviceManager.afficheError("Je suis un PIPO");


            }
        });

        Text iptext = new Text("Ip : ");
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

        Text masktext = new Text("Mask : ");
        masktext.setLayoutX(155);
        masktext.setLayoutY(45);
        configPane.getChildren().add(masktext);
        actualiseGraph.add(masktext);

        TextField maskField = null;

        try {
            maskField = new TextField(abstractServer.getIntefaceMask(0).toString());
        } catch (BadCallException e) {
            afficheError("Error 23");
        }
        configPane.getChildren().add(maskField);
        actualiseGraph.add(maskField);
        maskField.setLayoutX(155);
        maskField.setLayoutY(60);

        Button validationButton = new Button("Ok");
        validationButton.setLayoutY(60);
        validationButton.setLayoutX(310);
        final TextField finalTextField = textField;
        final TextField finalMaskField = maskField;
        validationButton.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
                String ipstring = finalTextField.getText();
                String maskstring = finalMaskField.getText();

                Boolean isAnIp = true;

                IP ip = null;
                IP mask = null;

                try {
                    ip = IP.stringToIP(ipstring);
                    mask = IP.stringToIP(maskstring);
                } catch (BadCallException e) {
                    afficheError("Wrong Argument : Not an IP");
                    try {
                        finalTextField.setText(abstractServer.getInterfaceIP(0).toString());
                        finalMaskField.setText(abstractServer.getIntefaceMask(0).toString());
                    } catch (BadCallException e1) {
                        e1.printStackTrace();
                    }
                    isAnIp = false;
                }

                if (isAnIp) {
                    abstractServer.configureIP(0, ip);
                    abstractServer.configureMask(0, mask);

                }


            }
        });

        configPane.getChildren().add(validationButton);
        actualiseGraph.add(validationButton);

        Text gatewaytext = new Text("Gateway : ");
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

        Button validationButtongateway = new Button("Ok");
        validationButtongateway.setLayoutY(120);
        validationButtongateway.setLayoutX(200);
        final TextField finalTextFieldgateway = textFieldgateway;
        validationButtongateway.setOnAction(event -> Platform.runLater( () -> {
            if(!DeviceManager.is_bugging()) {
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
        }));

        configPane.getChildren().add(validationButtongateway);
        actualiseGraph.add(validationButtongateway);

        if(DHCPServer.class.isInstance(abstractServer)) {
            //TODO
            DHCPServer dhcpServer = (DHCPServer) abstractServer;
            ArrayList <ArrayList<IP>> DHCPTable = dhcpServer.getAllRanges();

            Text dhcptext = new Text("DHCP : ");
            dhcptext.setLayoutX(5);
            dhcptext.setLayoutY(165);
            configPane.getChildren().add(dhcptext);
            actualiseGraph.add(dhcptext);

            Text subnettext = new Text("Subnet : ");
            subnettext.setLayoutX(5);
            subnettext.setLayoutY(195);
            configPane.getChildren().add(subnettext);
            actualiseGraph.add(subnettext);


            Text dhcptext1 = new Text("IP min : ");
            dhcptext1.setLayoutX(155);
            dhcptext1.setLayoutY(195);
            configPane.getChildren().add(dhcptext1);
            actualiseGraph.add(dhcptext1);

            Text dhcptext2 = new Text("IP max : ");
            dhcptext2.setLayoutX(310);
            dhcptext2.setLayoutY(195);
            configPane.getChildren().add(dhcptext2);
            actualiseGraph.add(dhcptext2);

            Button dhcpok = new Button("Ok");
            dhcpok.setLayoutX(465);
            dhcpok.setLayoutY(180);
            configPane.getChildren().add(dhcpok);
            actualiseGraph.add(dhcpok);

            Button dhcpadd = new Button("New");
            dhcpadd.setLayoutX(500);
            dhcpadd.setLayoutY(180);
            configPane.getChildren().add(dhcpadd);
            actualiseGraph.add(dhcpadd);

            ArrayList<TextField> list_of_subnet =  new ArrayList<>();
            ArrayList<TextField> list_of_ipmin = new ArrayList<>();
            ArrayList<TextField> list_of_ipmax = new ArrayList<>();
            ArrayList<Boolean> list_of_ipok = new ArrayList<>();

            int j = DHCPTable.size();
            for(int i=0;i<j;i++) {

                IP subnet = DHCPTable.get(i).get(0);
                IP ipmin = DHCPTable.get(i).get(1);
                IP ipmax = DHCPTable.get(i).get(2);


                TextField subnetfield = new TextField(subnet.toString());
                subnetfield.setLayoutX(5);
                subnetfield.setLayoutY(210 + i * 30);
                subnetfield.setPrefWidth(145);
                configPane.getChildren().add(subnetfield);
                actualiseGraph.add(subnetfield);
                list_of_subnet.add(subnetfield);

                TextField ipminfield = new TextField(ipmin.toString());
                ipminfield.setLayoutX(155);
                ipminfield.setLayoutY(210 + i * 30);
                ipminfield.setPrefWidth(145);
                configPane.getChildren().add(ipminfield);
                actualiseGraph.add(ipminfield);
                list_of_ipmin.add(ipminfield);

                TextField ipmaxfield = new TextField(ipmax.toString());
                ipmaxfield.setLayoutX(310);
                ipmaxfield.setLayoutY(210 + i * 30);
                ipminfield.setPrefWidth(145);
                configPane.getChildren().add(ipmaxfield);
                actualiseGraph.add(ipmaxfield);
                list_of_ipmax.add(ipmaxfield);

                list_of_ipok.add(true);

                Button deleteButton = new Button ("Delete");
                deleteButton.setLayoutX(465);
                deleteButton.setLayoutY(210 + i * 30);
                configPane.getChildren().add(deleteButton);
                actualiseGraph.add(deleteButton);

                final int finalI = i;
                deleteButton.setOnAction(event -> {
                    list_of_ipok.set(finalI,false);

                    configPane.getChildren().remove(subnetfield);
                    actualiseGraph.remove(subnetfield);
                    configPane.getChildren().remove(ipminfield);
                    actualiseGraph.remove(ipminfield);
                    configPane.getChildren().remove(ipmaxfield);
                    actualiseGraph.remove(ipmaxfield);
                    configPane.getChildren().remove(deleteButton);
                    actualiseGraph.remove(deleteButton);

                    Text okdelete = new Text("Ready to be deleted");
                    okdelete.setLayoutX(5);
                    okdelete.setLayoutY(210 + finalI * 30 + 15);
                    configPane.getChildren().add(okdelete);
                    actualiseGraph.add(okdelete);
                });

            }

            final int[] iadd = {j};
            dhcpadd.setOnAction(event -> {


                TextField subnetfield = new TextField();
                subnetfield.setLayoutX(5);
                subnetfield.setLayoutY(210 + iadd[0] * 30);
                subnetfield.setPrefWidth(145);
                configPane.getChildren().add(subnetfield);
                actualiseGraph.add(subnetfield);
                list_of_subnet.add(subnetfield);

                TextField ipminfield = new TextField();
                ipminfield.setLayoutX(155);
                ipminfield.setLayoutY(210 + iadd[0] * 30);
                ipminfield.setPrefWidth(145);
                configPane.getChildren().add(ipminfield);
                actualiseGraph.add(ipminfield);
                list_of_ipmin.add(ipminfield);

                TextField ipmaxfield = new TextField();
                ipmaxfield.setLayoutX(310);
                ipmaxfield.setLayoutY(210 + iadd[0] * 30);
                ipminfield.setPrefWidth(145);
                configPane.getChildren().add(ipmaxfield);
                actualiseGraph.add(ipmaxfield);
                list_of_ipmax.add(ipmaxfield);

                list_of_ipok.add(true);

                Button deleteButton = new Button ("Delete");
                deleteButton.setLayoutX(465);
                deleteButton.setLayoutY(210 + iadd[0] * 30);
                configPane.getChildren().add(deleteButton);
                actualiseGraph.add(deleteButton);

                final int finalI = iadd[0];
                deleteButton.setOnAction(event2 -> {
                    list_of_ipok.set(finalI,false);

                    configPane.getChildren().remove(subnetfield);
                    actualiseGraph.remove(subnetfield);
                    configPane.getChildren().remove(ipminfield);
                    actualiseGraph.remove(ipminfield);
                    configPane.getChildren().remove(ipmaxfield);
                    actualiseGraph.remove(ipmaxfield);
                    configPane.getChildren().remove(deleteButton);
                    actualiseGraph.remove(deleteButton);

                    Text okdelete = new Text("Ready to be deleted");
                    okdelete.setLayoutX(5);
                    okdelete.setLayoutY(210 + finalI * 30 + 15);
                    configPane.getChildren().add(okdelete);
                    actualiseGraph.add(okdelete);
                });


                iadd[0]++;
            });

            dhcpok.setOnAction(event -> {
                int a = list_of_ipmax.size();


                ArrayList<ArrayList<IP>> dhcpTableNew = new ArrayList<ArrayList<IP>>();




                try {
                    for(int i = 0;i<a;i++) {
                        if(list_of_ipok.get(i)){
                            ArrayList<IP> line_of_DHCP = new ArrayList<IP>();
                            IP subnet = null;
                            IP ipmin = null;
                            IP ipmax = null;
                            subnet = IP.stringToIP(list_of_subnet.get(i).getText());
                            ipmin= IP.stringToIP(list_of_ipmin.get(i).getText());
                            ipmax= IP.stringToIP(list_of_ipmax.get(i).getText());
                            line_of_DHCP.add(subnet);
                            line_of_DHCP.add(ipmin);
                            line_of_DHCP.add(ipmax);
                            dhcpTableNew.add(line_of_DHCP);
                        }
                    }




                    dhcpServer.setAllRanges(dhcpTableNew);
                    updateConfigMenuServer(configStage,configPane,actualiseGraph,abstractServer,surnom);
                } catch (BadCallException e) {
                    afficheError("Bad DHCP parameters");
                }
            });

        }



    }

    public static void updateConfigMenuRouter (Stage configStage, AnchorPane configPane, ArrayList<Object> actualiseGraph, AbstractRouter abstractRouter, Text surnom) {



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

        Button surnameButton = new Button ("Ok");
        surnameButton.setLayoutX(300);
        surnameButton.setLayoutY(30);
        configPane.getChildren().add(surnameButton);
        actualiseGraph.add(surnameButton);

        surnameButton.setOnAction(event -> {
            String surname=textFieldsurname.getText();
            abstractRouter.nickname=surname;
            surnom.setText(surname);
            configStage.setTitle(surname);
            if(surname.equals("aymeric") | surname.equals("Aymeric")) {

                DeviceManager.afficheError("Je suis un PIPO");


            }
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
            if(!DeviceManager.is_bugging()) {
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


                Text porttext = new Text("Port " + i + " : ");
                porttext.setLayoutX(5);
                porttext.setLayoutY(45 + 90 * i);
                configPane.getChildren().add(porttext);
                actualiseGraph.add(porttext);

                Text iptext = new Text("Ip : ");
                iptext.setLayoutX(5);
                iptext.setLayoutY(75 + 90 * i);
                configPane.getChildren().add(iptext);
                actualiseGraph.add(iptext);

                TextField textField = new TextField(abstractRouter.getInterfaceIP(i).toString());
                configPane.getChildren().add(textField);
                actualiseGraph.add(textField);
                textField.setLayoutX(5);
                textField.setLayoutY(90 + 90 * i);

                Text masktext = new Text("Mask : ");
                masktext.setLayoutX(155);
                masktext.setLayoutY(75 + 90 * i);
                configPane.getChildren().add(masktext);
                actualiseGraph.add(masktext);

                TextField maskField = new TextField(abstractRouter.getIntefaceMask(0).toString());

                configPane.getChildren().add(maskField);
                actualiseGraph.add(maskField);
                maskField.setLayoutX(155);
                maskField.setLayoutY(90 + 90 * i);









                Button validationButton = new Button("Ok");
                validationButton.setLayoutY(90 + 90 * i);
                validationButton.setLayoutX(310);
                int ilambda = i;
                validationButton.setOnAction(event -> {
                    if(!DeviceManager.is_bugging()) {
                        String ipstring = textField.getText();
                        String maskstring = maskField.getText();

                        Boolean isAnIp = true;

                        IP nIp = null;
                        IP mask = null;

                        try {
                            nIp = IP.stringToIP(ipstring);
                            mask = IP.stringToIP(maskstring);
                        } catch (BadCallException e) {
                            afficheError("Wrong Argument : Not an IP");
                            try {
                                textField.setText(abstractRouter.getInterfaceIP(ilambda).toString());
                                maskField.setText(abstractRouter.getIntefaceMask(ilambda).toString());
                            } catch (BadCallException e1) {
                                e1.printStackTrace();
                            }
                            isAnIp = false;
                        }

                        if (isAnIp) {
                            abstractRouter.configureIP(ilambda, nIp);
                            abstractRouter.configureMask(ilambda,mask);
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
        maskText.setLayoutX(155);
        gatewayText.setLayoutX(305);
        portnumberText.setLayoutX(455);

        configPane.getChildren().addAll(subnetText, maskText, gatewayText, portnumberText);
        actualiseGraph.add(subnetText);
        actualiseGraph.add(maskText);
        actualiseGraph.add(gatewayText);
        actualiseGraph.add(portnumberText);


        Button validationRouteButton = new Button("Ok");
        validationRouteButton.setLayoutY(60 + 90 * (i));
        validationRouteButton.setLayoutX(600);

        if(!abstractRouter.RIP) {
            configPane.getChildren().add(validationRouteButton);
            actualiseGraph.add(validationRouteButton);
        }

        Button addRouteButton = new Button("New");
        addRouteButton.setLayoutY(60 + 90 * (i));
        addRouteButton.setLayoutX(650);

        if(!abstractRouter.RIP) {
            configPane.getChildren().add(addRouteButton);
            actualiseGraph.add(addRouteButton);
        }




        ArrayList <ArrayList <Object>> tableRouteField = new ArrayList<>();

        ArrayList <Boolean> tablerouteok = new ArrayList<>();

        final int[] j = {0};

        while (j[0] < n) {

            TextField subnetTextField = new TextField(allroutes.get(j[0]).get(0).toString());
            TextField maskTextField = new TextField(allroutes.get(j[0]).get(1).toString());
            TextField gatewayTextField = new TextField(allroutes.get(j[0]).get(2).toString());
            TextField portnumberTextField = new TextField(allroutes.get(j[0]).get(3).toString());
            if(abstractRouter.RIP){
                subnetTextField.setDisable(true);
                maskTextField.setDisable(true);
                gatewayTextField.setDisable(true);
                portnumberTextField.setDisable(true);
            }

            Button    deleteButton = new Button("Delete");

            tablerouteok.add(true);
            int j1= j[0];
            int i1=i;
            deleteButton.setOnAction(event -> {
                if(!DeviceManager.is_bugging()) {
                    if(!abstractRouter.RIP) {
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
                    } else {
                        updateConfigMenuRouter(configStage, configPane, actualiseGraph, abstractRouter, surnom);
                    }
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
            maskTextField.setLayoutX(155);
            gatewayTextField.setLayoutX(305);
            portnumberTextField.setLayoutX(455);
            deleteButton.setLayoutX(605);

            subnetTextField.setPrefWidth(145);
            maskTextField.setPrefWidth(145);
            gatewayTextField.setPrefWidth(145);
            portnumberTextField.setPrefWidth(145);


            if(abstractRouter.RIP) {
                configPane.getChildren().addAll(subnetTextField, maskTextField, gatewayTextField, portnumberTextField);
                actualiseGraph.add(subnetTextField);
                actualiseGraph.add(maskTextField);
                actualiseGraph.add(gatewayTextField);
                actualiseGraph.add(portnumberTextField);
            } else {
                configPane.getChildren().addAll(subnetTextField, maskTextField, gatewayTextField, portnumberTextField,deleteButton);
                actualiseGraph.add(subnetTextField);
                actualiseGraph.add(maskTextField);
                actualiseGraph.add(gatewayTextField);
                actualiseGraph.add(portnumberTextField);
                actualiseGraph.add(deleteButton);
            }



            j[0]++;
        }


        final int finalI = i;
        addRouteButton.setOnAction(event ->{
            if(!DeviceManager.is_bugging()) {
                if(!abstractRouter.RIP) {
                    TextField subnetTextField = new TextField("");
                    TextField maskTextField = new TextField("");
                    TextField gatewayTextField = new TextField("");
                    TextField portnumberTextField = new TextField("");
                    Button deleteButton = new Button("Delete");

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
                    maskTextField.setLayoutX(155);
                    gatewayTextField.setLayoutX(305);
                    portnumberTextField.setLayoutX(455);
                    deleteButton.setLayoutX(605);

                    subnetTextField.setPrefWidth(145);
                    maskTextField.setPrefWidth(145);
                    gatewayTextField.setPrefWidth(145);
                    portnumberTextField.setPrefWidth(145);


                    configPane.getChildren().addAll(subnetTextField, maskTextField, gatewayTextField, portnumberTextField, deleteButton);
                    actualiseGraph.add(subnetTextField);
                    actualiseGraph.add(maskTextField);
                    actualiseGraph.add(gatewayTextField);
                    actualiseGraph.add(portnumberTextField);
                    actualiseGraph.add(deleteButton);

                    j[0]++;
                } else {
                    updateConfigMenuRouter(configStage, configPane, actualiseGraph, abstractRouter, surnom);
                }

            }
        });


        validationRouteButton.setOnAction(event -> {

            if(!DeviceManager.is_bugging()) {
                if(!abstractRouter.RIP) {
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
                        updateConfigMenuRouter(configStage, configPane, actualiseGraph, abstractRouter, surnom);
                    } catch (BadCallException e) {
                        afficheError("Route invalide");
                    } catch (NumberFormatException e) {
                        afficheError("Port invalide");
                    }
                } else {
                    updateConfigMenuRouter(configStage, configPane, actualiseGraph, abstractRouter, surnom);
                }

            }

        });
    }

    public static void updateConfigMenuWAN(Stage configStage, AnchorPane configPane, ArrayList<Object> actualiseGraph, WANPort wanPort, Text surnom) {

        int m = actualiseGraph.size();

        for(int a=0;a<m;a++) {
            configPane.getChildren().remove(actualiseGraph.get(a));


        }
        actualiseGraph.clear();




        Text iptext = new Text("Ip : ");
        iptext.setLayoutX(5);
        iptext.setLayoutY(45);
        configPane.getChildren().add(iptext);
        actualiseGraph.add(iptext);
        TextField textField = null;
        try {
            textField = new TextField(wanPort.getInterfaceIP(0).toString());
        } catch (BadCallException e) {
            afficheError("Error 23");
        }
        configPane.getChildren().add(textField);
        actualiseGraph.add(textField);
        textField.setLayoutX(5);
        textField.setLayoutY(60);

        Button validationButton = new Button("Ok");
        validationButton.setLayoutY(60);
        validationButton.setLayoutX(200);
        final TextField finalTextField = textField;
        validationButton.setOnAction(event ->{
            if(!DeviceManager.is_bugging()) {
                String string = finalTextField.getText();



                IP Ip = null;

                try {
                    Ip = IP.stringToIP(string);

                    wanPort.configureIP(0, Ip);



                } catch (BadCallException e) {
                    afficheError("Wrong Argument : Not an IP");
                    try {
                        finalTextField.setText(wanPort.getInterfaceIP(0).toString());
                    } catch (BadCallException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        });

        configPane.getChildren().add(validationButton);
        actualiseGraph.add(validationButton);




        Text gatewaytext = new Text("Gateway : ");
        gatewaytext.setLayoutX(5);
        gatewaytext.setLayoutY(105);
        configPane.getChildren().add(gatewaytext);
        actualiseGraph.add(gatewaytext);
        TextField textFieldgateway = null;

        textFieldgateway = new TextField(wanPort.getGateway().toString());

        configPane.getChildren().add(textFieldgateway);
        actualiseGraph.add(textFieldgateway);
        textFieldgateway.setLayoutX(5);
        textFieldgateway.setLayoutY(120);

        Button validationButtongateway = new Button("Ok");
        validationButtongateway.setLayoutY(120);
        validationButtongateway.setLayoutX(200);
        final TextField finalTextFieldgateway = textFieldgateway;
        validationButtongateway.setOnAction(event -> {
            if(!DeviceManager.is_bugging()) {
                String string = finalTextFieldgateway.getText();

                Boolean isAnIp = true;

                IP Ip = null;

                try {
                    Ip = IP.stringToIP(string);
                } catch (BadCallException e) {
                    afficheError("Wrong Argument : Not an IP");

                    finalTextFieldgateway.setText(wanPort.getGateway().toString());

                    isAnIp = false;
                }

                if (isAnIp) {
                    wanPort.configureGateway(Ip);
                }


            }
        });

        configPane.getChildren().add(validationButtongateway);
        actualiseGraph.add(validationButtongateway);

        Text ip2text = new Text("Server Ip : ");
        ip2text.setLayoutX(5);
        ip2text.setLayoutY(165);
        configPane.getChildren().add(ip2text);
        actualiseGraph.add(ip2text);
        TextField textFieldip2 = null;

        IP ipserver = wanPort.getServerIP();
        textFieldip2 = new TextField("");
        if(ipserver != null) {
            textFieldip2.setText(ipserver.toString());
        }


        configPane.getChildren().add(textFieldip2);
        actualiseGraph.add(textFieldip2);
        textFieldip2.setLayoutX(5);
        textFieldip2.setLayoutY(180);

        Button validationButtonIp2 = new Button("Ok");
        validationButtonIp2.setLayoutY(180);
        validationButtonIp2.setLayoutX(200);
        final TextField finalTextField2 = textFieldip2;


        configPane.getChildren().add(validationButtonIp2);
        actualiseGraph.add(validationButtonIp2);
        //Bite

        int i = 2;

        Text tablerout = new Text("Subnet desserved : ");
        tablerout.setLayoutX(5);
        tablerout.setLayoutY(60 + 90 * i);
        configPane.getChildren().add(tablerout);
        actualiseGraph.add(tablerout);

        ArrayList<ArrayList<Object>> allroutes = wanPort.getTable();
        int n = allroutes.size();

        Text subnetText = new Text("Subnet");
        Text maskText = new Text("Mask");


        subnetText.setLayoutY(75 + 90 * i);
        maskText.setLayoutY(75 + 90 * i);

        subnetText.setLayoutX(5);
        maskText.setLayoutX(155);

        configPane.getChildren().addAll(subnetText, maskText);
        actualiseGraph.add(subnetText);
        actualiseGraph.add(maskText);


        Button validationRouteButton = new Button("Ok");
        validationRouteButton.setLayoutY(60 + 90 * i);
        validationRouteButton.setLayoutX(300);


        configPane.getChildren().add(validationRouteButton);
        actualiseGraph.add(validationRouteButton);


        Button addRouteButton = new Button("New");
        addRouteButton.setLayoutY(60 + 90 * i);
        addRouteButton.setLayoutX(350);


        configPane.getChildren().add(addRouteButton);
        actualiseGraph.add(addRouteButton);





        ArrayList <ArrayList <Object>> tableRouteField = new ArrayList<>();

        ArrayList <Boolean> tablerouteok = new ArrayList<>();

        final int[] j = {0};

        while (j[0] < n) {


            TextField subnetTextField = new TextField(allroutes.get(j[0]).get(0).toString());
            TextField maskTextField = new TextField(allroutes.get(j[0]).get(1).toString());





            Button    deleteButton = new Button("DELETE");

            tablerouteok.add(true);
            int j1= j[0];
            int i1=2;
            deleteButton.setOnAction(event -> {
                if(!DeviceManager.is_bugging()) {

                        tablerouteok.set(j1, false);

                        Text deleteConfirm = new Text("Ready to be deleted");
                        deleteConfirm.setLayoutY(105 + 90 * (i1) + 30 * (j1));
                        deleteConfirm.setLayoutX(5);
                        configPane.getChildren().add(deleteConfirm);
                        actualiseGraph.add(deleteConfirm);

                        configPane.getChildren().remove(subnetTextField);
                        configPane.getChildren().remove(maskTextField);
                        configPane.getChildren().remove(deleteButton);

                        actualiseGraph.remove(subnetTextField);
                        actualiseGraph.remove(maskTextField);
                        actualiseGraph.remove(deleteButton);



                }


            });

            ArrayList <Object> tablerouteI = new ArrayList<>();
            tablerouteI.add(subnetTextField);
            tablerouteI.add(maskTextField);

            tableRouteField.add(tablerouteI);

            subnetTextField.setLayoutY(90 + 90 * (i)+30*(j[0]));
            maskTextField.setLayoutY(90 + 90 * (i)+30*(j[0]));
            deleteButton.setLayoutY(90 + 90 * (i)+30*(j[0]));

            subnetTextField.setLayoutX(5);
            maskTextField.setLayoutX(155);
            deleteButton.setLayoutX(305);

            subnetTextField.setPrefWidth(145);
            maskTextField.setPrefWidth(145);



                configPane.getChildren().addAll(subnetTextField, maskTextField,deleteButton);
                actualiseGraph.add(subnetTextField);
                actualiseGraph.add(maskTextField);
                actualiseGraph.add(deleteButton);




            j[0]++;
        }


        final int finalI = i;
        addRouteButton.setOnAction(event ->{
            if(!DeviceManager.is_bugging()) {

                    TextField subnetTextField = new TextField("");
                    TextField maskTextField = new TextField("");
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
                        configPane.getChildren().remove(deleteButton);

                        actualiseGraph.remove(subnetTextField);
                        actualiseGraph.remove(maskTextField);
                        actualiseGraph.remove(deleteButton);


                    });

                    ArrayList<Object> tablerouteI = new ArrayList<>();
                    tablerouteI.add(subnetTextField);
                    tablerouteI.add(maskTextField);

                    tableRouteField.add(tablerouteI);

                    subnetTextField.setLayoutY(90 + 90 * (finalI) + 30 * (j[0]));
                    maskTextField.setLayoutY(90 + 90 * (finalI) + 30 * (j[0]));
                    deleteButton.setLayoutY(90 + 90 * (finalI) + 30 * (j[0]));

                    subnetTextField.setLayoutX(5);
                    maskTextField.setLayoutX(155);
                    deleteButton.setLayoutX(305);

                    subnetTextField.setPrefWidth(145);
                    maskTextField.setPrefWidth(145);


                    configPane.getChildren().addAll(subnetTextField, maskTextField, deleteButton);
                    actualiseGraph.add(subnetTextField);
                    actualiseGraph.add(maskTextField);
                    actualiseGraph.add(deleteButton);

                    j[0]++;

            }
        });



        validationRouteButton.setOnAction(event -> {

            if (!DeviceManager.is_bugging()) {

                ArrayList<ArrayList<Object>> tablerouteObject = new ArrayList<ArrayList<Object>>();

                int n1 = tableRouteField.size();
                try {
                    for (int i2 = 0; i2 < n1; i2++) {
                        if (tablerouteok.get(i2)) {
                            ArrayList<Object> tablerouteI = new ArrayList<Object>();
                            TextField textField1 = (TextField) tableRouteField.get(i2).get(0);
                            TextField textField2 = (TextField) tableRouteField.get(i2).get(1);


                            tablerouteI.add(IP.stringToIP(textField1.getText()));
                            tablerouteI.add(IP.stringToIP(textField2.getText()));


                            tablerouteObject.add(tablerouteI);
                        }

                    }
                    wanPort.setTable(tablerouteObject);
                    updateConfigMenuWAN(configStage, configPane, actualiseGraph, wanPort, surnom);
                } catch (BadCallException e) {
                    afficheError("Route invalide");
                } catch (NumberFormatException e) {
                    afficheError("Port invalide");
                }
            }



        });

        validationButtonIp2.setOnAction(event ->{
            if(!DeviceManager.is_bugging()) {
                String string = finalTextField2.getText();



                IP Ip = null;

                try {
                    Ip = IP.stringToIP(string);


                    configStage.setTitle("attempting connection");

                    wanPort.setServerIP(Ip.toString());

                    configStage.setTitle("WANPort");


                } catch (BadCallException e) {
                    afficheError("Wrong Argument : Not an IP");


                    IP ipserverv = wanPort.getServerIP();

                    if(ipserver != null) {
                        finalTextField2.setText(ipserverv.toString());
                    }

                } catch (BadWANTable badWANTable) {


                    int n1 = badWANTable.conflicts.subnets.size();

                    ArrayList<ArrayList<Object>> allroutes2 = wanPort.getTable();

                    for(int i1=0;i1<n1;i1++) {
                        IP subnetin = badWANTable.conflicts.subnets.get(i1);


                        int n2 = allroutes2.size();
                        for(int i2=0;i2<n2;i2++) {
                            IP subnetout = (IP) allroutes2.get(i2).get(0);


                            if(subnetout.equals(subnetin)) {
                                TextField conflit1 = (TextField) tableRouteField.get(i2).get(0);
                                TextField conflit2 = (TextField) tableRouteField.get(i2).get(1);
                                conflit1.setStyle("-fx-base: rgb(255, 0, 0);");
                                conflit2.setStyle("-fx-base: rgb(255, 0, 0);");
                            }


                        }

                        configPane.getChildren().remove(validationButtonIp2);
                        actualiseGraph.remove(validationButtonIp2);

                    }

                    IP ip = null;
                    //ip.getSubnet(mask);



                    ArrayList<Integer> arrayListOfStateError = DeviceManager.getArrayListOfStateError();
                    arrayListOfStateError.set(0, new Integer(6000));

                    DeviceManager.afficheError("BadWanTable");

                } catch (ConnectException e) {
                    afficheError("Connection TIMEOUT");
                }

            }
        });



    }
}
