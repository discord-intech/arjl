/**
 * Copyright (C) 2016 Desvignes Julian, Louis-Baptiste Trailin, Aymeric Gleye, Rémi Dulong
 */

/**
 This file is part of ARJL.

 ARJL is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 ARJL is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with ARJL.  If not, see <http://www.gnu.org/licenses/>

 */

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

public Cisco2811Router(ArrayList<Integer> MACinterfaces, ArrayList<IP> IPinterfaces, ArrayList<IP> MaskInterfaces, IP default_gateway, int default_port) throws BadCallException {

    super(new ArrayList<LinkTypes>(){{
              add(LinkTypes.ETH);
              add(LinkTypes.ETH);
              add (LinkTypes.SERIAL);}},
            new ArrayList<Bandwidth>(){{
                add(Bandwidth.ETH_100);
                add(Bandwidth.ETH_100);
                add(Bandwidth.SERIAL);
}},
            262144, MACinterfaces, IPinterfaces, MaskInterfaces, default_gateway, default_port, ClockSpeed.MEDIUM);
  }
}


