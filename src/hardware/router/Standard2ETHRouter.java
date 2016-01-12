package hardware.router;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import packet.IP;

import java.util.ArrayList;

public class Standard2ETHRouter extends AbstractRouter
{

    /**
     * Constructeur à appeller avec super()
     * @param MACinterfaces
     * @param IPinterfaces
     */
    public Standard2ETHRouter(ArrayList<Integer> MACinterfaces, ArrayList<IP> IPinterfaces, IP default_gateway, int default_port) throws BadCallException {
        //Pour initialiser les appareils, JAVA me force à utiliser les classes anonymes, c'est juste horrible...
        super(new ArrayList<LinkTypes>(){{
            add(LinkTypes.ETH);
            add(LinkTypes.ETH);}},
                new ArrayList<Bandwidth>(){{
            add(Bandwidth.ETH_1G);
            add(Bandwidth.ETH_1G);}},
                50, MACinterfaces, IPinterfaces, default_gateway, default_port);
    }
}