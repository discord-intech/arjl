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

/**
 * @author A. Gleye
 */

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;
import packet.IP;

import java.util.ArrayList;

/**
 * Classe définissant un routeur Cisco CRS1 avec 24 ports Gigabit ethernet simplifié (http://www.cisco.com/c/en/us/products/collateral/routers/crs-1-16-slot-single-shelf-system/product_data_sheet09186a008022d5f3.html)
 */
public class CiscoCRS1Router extends AbstractRouter {

    /**
     * Constructeur à appeller
     * @param MACinterfaces les MACs par port
     * @param IPinterfaces les IPs par port
     * @param default_gateway la passerelle par défaut (pour le subnet 0.0.0.0)
     * @param default_port le port par défaut (pour le subnet 0.0.0.0)
     */

    public CiscoCRS1Router(ArrayList<Integer> MACinterfaces, ArrayList<IP> IPinterfaces, ArrayList<IP> MaskInterfaces, IP default_gateway, int default_port) throws BadCallException {

        super(new ArrayList<LinkTypes>(){{
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);
                  add(LinkTypes.ETH);}},
                new ArrayList<Bandwidth>(){{
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
                    add(Bandwidth.ETH_10G);
}},
                2097152, MACinterfaces, IPinterfaces, MaskInterfaces, default_gateway, default_port, ClockSpeed.MEDIUM);
    }
}
