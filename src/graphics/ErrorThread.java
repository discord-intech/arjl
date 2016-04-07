package graphics;

import hardware.AbstractHardware;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;


/**
 * Created by Louis-Baptiste on 24/03/2016.
 */
public class ErrorThread extends Thread {

    AbstractHardware abstractHardware = null;

    public ErrorThread(AbstractHardware abstractHardware){
        this.abstractHardware=abstractHardware;
    }

    @Override
    public void run(){

        ArrayList <AbstractHardware> abstractHardwareArrayList = DeviceManager.getArray_list_of_devices();
        ArrayList <ImageView> imageViewArrayList = DeviceManager.getArray_list_of_image();

        Image image = new Image("file:sprites/alert.png");
        ImageView imageView = new ImageView();
        imageView.setLayoutX(imageViewArrayList.get(abstractHardwareArrayList.indexOf(abstractHardware)).getLayoutX());
        imageView.setLayoutY(imageViewArrayList.get(abstractHardwareArrayList.indexOf(abstractHardware)).getLayoutY());
        imageView.setImage(image);


        /*AnchorPane anchorPane = DeviceManager.getContentforerror();
        if(!anchorPane.getChildren().contains(imageView)) {
            try {
                anchorPane.getChildren().add(imageView);
            } catch (IllegalStateException e) {
                //TODO trouver comment corriger ce throw
                //System.out.println("TODO Overflow");
            }
        }
        */


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //anchorPane.getChildren().remove(imageView);

    }

}
