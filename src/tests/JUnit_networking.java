package tests;

import actions.Actions;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import hardware.AbstractHardware;
import hardware.client.StandardPC;
import hardware.hub.Standard24ETHHub;
import hardware.router.Standard2ETHRouter;
import hardware.server.DHCPServer;
import hardware.server.StandardWEBServer;
import hardware.switchs.Standard24ETHSwitch;
import org.junit.Test;
import packet.IP;

import java.util.ArrayList;

/**
 * JUnit de test réseau
 * @author J. Desvignes
 */
public class JUnit_networking
{
    /**
     * A -- SRA -- central -- SRB -- B   (SR = switch)
     */
    @Test
    public void setUp()
    {
        try {
            Standard2ETHRouter central = new Standard2ETHRouter(new ArrayList<Integer>(){{add(1);add(2);}},
                new ArrayList<IP>(){{add(new IP(192,168,0,1));add(new IP(192,168,1,1));}},
                    new IP(192,168,0,2), 0);

            Standard2ETHRouter A = new Standard2ETHRouter(new ArrayList<Integer>(){{add(3);add(4);}},
                new ArrayList<IP>(){{add(new IP(192,168,0,2));add(new IP(192,168,3,1));}},
                    new IP(192,168,0,1), 0);

            Standard2ETHRouter B = new Standard2ETHRouter(new ArrayList<Integer>(){{add(5);add(6);}},
                new ArrayList<IP>(){{add(new IP(192,168,1,2));add(new IP(192,168,4,1));}},
                    new IP(192,168,1,1), 0);

            Standard2ETHRouter C = new Standard2ETHRouter(new ArrayList<Integer>(){{add(7);add(8);}},
                    new ArrayList<IP>(){{add(new IP(192,168,1,3));add(new IP(192,168,5,1));}},
                    new IP(192,168,1,1), 0);

            Standard2ETHRouter D = new Standard2ETHRouter(new ArrayList<Integer>(){{add(9);add(10);}},
                    new ArrayList<IP>(){{add(new IP(192,168,0,3));add(new IP(192,168,6,1));}},
                    new IP(192,168,0,1), 0);

            StandardPC jeanLuc_PC = new StandardPC(11, new IP(192,168,0,5), new IP(192,168,0,1));
            StandardPC raymondPC = new StandardPC(13, new IP(192,168,1,6), new IP(192,168,1,1));

            StandardWEBServer WEB = new StandardWEBServer(12, new IP(192,168,1,5), new IP(192,168,1,1));
            DHCPServer dhcp = new DHCPServer(22, new IP(192,168,1,8), new IP(192,168,1,1));

            Standard24ETHSwitch srA = new Standard24ETHSwitch();
            Standard24ETHHub srB = new Standard24ETHHub();

            central.addRoutingRule(0, new IP(192,168,0,0), new IP(255,255,255,0), new IP(192,168,0,1));
            central.addRoutingRule(1, new IP(192,168,1,0), new IP(255,255,255,0), new IP(192,168,1,1));

            Actions.connect(A, srA, LinkTypes.ETH);
            Actions.connect(B, srB, LinkTypes.ETH);
            Actions.connect(C, srB, LinkTypes.ETH);
            Actions.connect(D, srA, LinkTypes.ETH);
            Actions.connect(central, srA, LinkTypes.ETH);
            Actions.connect(central, srB, LinkTypes.ETH);
            Actions.connect(jeanLuc_PC, srA, LinkTypes.ETH);
            Actions.connect(raymondPC, srB, LinkTypes.ETH);
            Actions.connect(WEB, srB, LinkTypes.ETH);
            Actions.connect(dhcp, srB, LinkTypes.ETH);

            dhcp.addRange(new IP(192,168,0,0), new IP(192,168,0,30), new IP(192,168,0,60));
            dhcp.addRange(new IP(192,168,1,0), new IP(192,168,1,30), new IP(192,168,1,60));


            central.setDHCPRelay(new IP(192,168,1,8));

            StandardPC DHCPtester = new StandardPC(21);
            DHCPtester.setVerbose("DHCPserv");
            jeanLuc_PC.setVerbose("JeanLuc");
            A.setVerbose("Routeur A");
            WEB.setVerbose("WEB");
            Actions.connect(DHCPtester, srA, LinkTypes.ETH);
            DHCPtester.DHCPClient(0);

            AbstractHardware.setSpeed((float)0.1);

            A.start();
            B.start();
            D.start();
            C.start();
            srA.start();
            srB.start();
            central.start();
            jeanLuc_PC.start();
            raymondPC.start();
            WEB.start();
            DHCPtester.start();
            dhcp.start();

            A.changeState(true);
            B.changeState(true);
            D.changeState(true);
            C.changeState(true);
            srA.changeState(true);
            srB.changeState(true);
            central.changeState(true);
            jeanLuc_PC.changeState(true);
            raymondPC.changeState(true);
            WEB.changeState(true);
            DHCPtester.changeState(true);
            dhcp.changeState(true);

            while(true)
            {
                if (!jeanLuc_PC.waitsForSomething()) {
                    jeanLuc_PC.launchRequest(PacketTypes.WEB, new IP(192, 168, 1, 5));
                }
                if (!raymondPC.waitsForSomething()) {
                    raymondPC.launchRequest(PacketTypes.WEB, new IP(192, 168, 1, 5));
                }
                if (!DHCPtester.waitsForSomething()) {
                    DHCPtester.launchRequest(PacketTypes.WEB, new IP(192, 168, 1, 5));
                }
            }

        } catch (BadCallException e) {
            e.printStackTrace();
        } catch (OverflowException e) {
            e.printStackTrace();
        }
    }


}