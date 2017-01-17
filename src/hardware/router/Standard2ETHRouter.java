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
    public Standard2ETHRouter(ArrayList<Integer> MACinterfaces, ArrayList<IP> IPinterfaces, IP default_gateway, ArrayList<IP> MaskInterfaces, int default_port) throws BadCallException {
        //Pour initialiser les appareils, JAVA me force à utiliser les classes anonymes, c'est juste horrible...
        super(new ArrayList<LinkTypes>(){{
            add(LinkTypes.ETH);
            add(LinkTypes.ETH);}},
                new ArrayList<Bandwidth>(){{
            add(Bandwidth.ETH_1G);
            add(Bandwidth.ETH_1G);}},
                200, MACinterfaces, IPinterfaces, MaskInterfaces, default_gateway, default_port, ClockSpeed.MEDIUM);
    }
}