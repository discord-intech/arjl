import exceptions.BadCallException;
import graphics.*;
import hardware.AbstractHardware;
import javafx.application.Application;
import javafx.stage.Stage;
import script.ScriptEngine;

/**
 * Classe MAIN du programme
 * @author J. Desvignes
 *
 * Propositions de noms :
 *  - ARJL
 */
public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{

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
