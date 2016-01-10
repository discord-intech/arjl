package tests;

import hardware.router.Standard2ETHRouter;
import org.junit.Before;
import packet.IP;

import java.util.ArrayList;

public class JUnit_networking
{
    @Before
    public void setUp()
    {
        Standard2ETHRouter central = new Standard2ETHRouter(new ArrayList<Integer>(){{add(1);add(2);}},
                new ArrayList<IP>(){{add(new IP(192,168,0,1));add(new IP(192,168,1,1));}},
                new ArrayList<IP>(){{add(new IP(255,255,255,0));add(new IP(255,255,255,0));}});
    }

}