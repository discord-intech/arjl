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

package enums;

/**
 * Enum des différentes bandes passantes
 * @author J. Desvignes
 * @author A. Gleye
 */
public enum Bandwidth
{
    NULL(0),        // Placeholder pour la création d'un lien
    ETH_10(131),   //Fast Ethernet 10Mbps
    ETH_100(1317),    // Fast Ethernet 100Mbps
    SERIAL(64),       // Série 64kbps
    ETH_1G(13172),      // Gigabit Ethernet 1Gbps
    ETH_10G(131720);    // Gigabit Ethernet 10Gbps


    /** Valeur de bande passante */
    public final int value;

    Bandwidth(int val)
    {
        this.value = val;
    }

}