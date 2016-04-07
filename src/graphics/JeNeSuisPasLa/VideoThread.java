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
