package graphics;


import exceptions.BadCallException;

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
                DeviceManager.updateprogressBar();
                try {
                    DeviceManager.updateLink();
                } catch (BadCallException e) {
                    e.printStackTrace();
                }
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
