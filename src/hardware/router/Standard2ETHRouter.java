package hardware.router;


import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;
import packet.IP;

import java.util.ArrayList;

/**
 * Classe définissant un routeur basique à 2 ports Gigabit Ethernet
 * @author J. Desvignes
 */
public class Standard2ETHRouter extends AbstractRouter
{

    /**
     * Constructeur à appeller
     * @param MACinterfaces les MACs par port
     * @param IPinterfaces les IPs par port
     * @param default_gateway la passerelle par défaut (pour le subnet 0.0.0.0)
     * @param default_port le port par défaut (pour le subnet 0.0.0.0)
     */
    public Standard2ETHRouter(ArrayList<Integer> MACinterfaces, ArrayList<IP> IPinterfaces, IP default_gateway, int default_port) throws BadCallException {
        //Pour initialiser les appareils, JAVA me force à utiliser les classes anonymes, c'est juste horrible...
        super(new ArrayList<LinkTypes>(){{
            add(LinkTypes.ETH);
            add(LinkTypes.ETH);}},
                new ArrayList<Bandwidth>(){{
            add(Bandwidth.ETH_1G);
            add(Bandwidth.ETH_1G);}},
                200, MACinterfaces, IPinterfaces, default_gateway, default_port, ClockSpeed.MEDIUM);
    }
}