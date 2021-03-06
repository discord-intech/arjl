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
import javafx.application.Platform;

/**
 * Le thread d'actualisation
 */
public class ActualizeThread extends Thread {



    public ActualizeThread(){
        ;
    }

    @Override
    public void run(){

        while(42==42){

            if(!DeviceManager.getLoadmode() & !DeviceManager.getCreatemode()) {
                Platform.runLater(() -> {
                    Actualization.updateprogressBar();
                    try {
                        Actualization.updateLink();
                        Actualization.updateError();
                    } catch (BadCallException e) {
                        e.printStackTrace();
                    }

                });
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
