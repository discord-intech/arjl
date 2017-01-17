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


import javafx.scene.media.MediaView;
import javafx.stage.Stage;

public class VideoThread extends Thread {
    private MediaView mediaView;
    private Stage stage;



    public VideoThread(MediaView mediaView,Stage stage){
        this.mediaView=mediaView;
        this.stage=stage;

    }

    @Override
    public void run(){
        while(42==42){


            mediaView.setLayoutX((stage.getWidth()-4*stage.getHeight()/3)/2);
            mediaView.setFitHeight(stage.getHeight());

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}
