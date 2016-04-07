package tests;

import actions.Actions;
import enums.LinkTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import hardware.Link;
import hardware.hub.Standard24ETHHub;
import hardware.switchs.Standard24ETHSwitch;
import org.junit.Test;
import packet.Packet;

/**
 * JUnit pour la présentation de l'architecture
 * @author J. Desvignes
 */
public class JUnit_ShowOff
{
    @Test
    public void show() throws BadCallException
    {
        System.out.println((int)0.2);
        Standard24ETHSwitch A = new Standard24ETHSwitch();
        Standard24ETHHub B = new Standard24ETHHub();
        Standard24ETHSwitch C = new Standard24ETHSwitch();

        Link link = Actions.connect(A, B, LinkTypes.ETH);
        Actions.connect(B, C, LinkTypes.ETH);

        System.out.println(link.getHardwareConnected());
        A.start();
        B.start();
        C.start();
        try {
            A.send(new Packet(), 0);
        } catch (OverflowException e) {
            e.printStackTrace();
        }

        while(true);
    }
}

