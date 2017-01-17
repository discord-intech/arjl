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

package hardware.server;

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;

/**
 * Classe définissant un serveur FTP classique
 * @author J. Desvignes
 */
public class StandardFTPServer extends AbstractServer
{

    /**
     * Constructeur
     *
     * @param MAC mac de l'appareil
     * @param IP IP de l''appareil (config statique)
     * @param default_gateway passerelle par défaut
     */
    public StandardFTPServer(int MAC, packet.IP IP, packet.IP default_gateway) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 5971, MAC, IP, default_gateway, PacketTypes.FTP, ClockSpeed.MEDIUM);
    }

    public StandardFTPServer(int MAC) throws BadCallException
    {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 5971, MAC, PacketTypes.FTP, ClockSpeed.MEDIUM);
    }
}