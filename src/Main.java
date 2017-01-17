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

import exceptions.BadCallException;
import graphics.*;
import hardware.AbstractHardware;
import javafx.application.Application;
import javafx.stage.Stage;
import script.ScriptEngine;

/**
 * Classe MAIN
 * @author J. Desvignes
 */
public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{

        System.out.println("Thread graphique lancé");
        Initialisation initialisation = new Initialisation();
        initialisation.init(primaryStage);


    }


    public static void main(String[] args) throws BadCallException {

        if(args.length == 0)
        {
            try {
                AbstractHardware.setSpeed((float)0.1);
            } catch (BadCallException e) {
                e.printStackTrace();
            }
            launch(args);
        }
        else if(args[0].equals("nogui"))
        {
            ScriptEngine engine = new ScriptEngine();
            engine.start();
            while(engine.isAlive());
        }
        else
        {
            launch(args);
        }


    }
}
