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

package hardware.client;


import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;

/**
 * Classe définissant un PC classique
 * @author J. Desvignes
 */
public class StandardPC extends AbstractClient
{
    /**
     * Constructeur à appeller
     *
     * @param MAC la MAC de l'appareil
     * @param IP l'IP statique
     * @param default_gateway la passerelle par défaut
     */
    public StandardPC(int MAC, packet.IP IP, packet.IP default_gateway) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 5971, MAC, IP, default_gateway, ClockSpeed.MEDIUM);
    }

    /**
     * Construteur pour configuration IP par DHCP
     * @param MAC la MAC de l'appareil
     */
    public StandardPC(int MAC) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 5971, MAC, ClockSpeed.MEDIUM);
    }
}