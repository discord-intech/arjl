package hardware.router;

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;
import packet.IP;

import java.util.ArrayList;

/**
 * @author A. Gleye
 */

/**
 * Classe définissant un routeur Cisco 2811 avec 2 ports Ethernet et 1 port série comme ceux utilisés en TP (http://www.cisco.com/c/en/us/products/collateral/routers/2800-series-integrated-services-routers-isr/product_data_sheet0900aecd8016fa68.html)
 */
public class Cisco2811Router extends AbstractRouter {
    /**
 * Constructeur à appeller
 * @param MACinterfaces les MACs par port
 * @param IPinterfaces les IPs par port
 * @param default_gateway la passerelle par défaut (pour le subnet 0.0.0.0)
 * @param default_port le port par défaut (pour le subnet 0.0.0.0)
 */

public Cisco2811Router(ArrayList<Integer> MACinterfaces, ArrayList<IP> IPinterfaces, IP default_gateway, int default_port) throws BadCallException {

    super(new ArrayList<LinkTypes>(){{
              add(LinkTypes.ETH);
              add(LinkTypes.ETH);
              add (LinkTypes.SERIAL);}},
            new ArrayList<Bandwidth>(){{
                add(Bandwidth.ETH_100);
                add(Bandwidth.ETH_100);
                add(Bandwidth.SERIAL);
}},
            262144, MACinterfaces, IPinterfaces, default_gateway, default_port, ClockSpeed.MEDIUM);
  }
}


