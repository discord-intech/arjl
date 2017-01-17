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

package table;


import packet.IP;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe définissant une table ARP
 * @author J. Desvignes
 */
public class ARPTable implements Serializable
{
	/** Les IPs */
    private ArrayList<IP> ips = new ArrayList<>();
    
    /** Les MACs corrélées aux IPs */
    private ArrayList<Integer> macs = new ArrayList<>();

    /**
     * Ajoute une règle ARP
     * @param address l'adresse IP
     * @param mac la MAC associée
     */
    public synchronized void addRule(IP address, int mac)
    {
        if(macs.contains(mac))
            return;
        ips.add(address);
        macs.add(mac);
    }

    /**
     * Recherche l'adresse MAC dans sa base de données
     * @param address l'adresse IP à rechercher
     * @return l'adresse MAC ; -1 si non trouvée
     */
    public int findARP(IP address)
    {
        for(int i=0 ; i < ips.size() ; i++)
        {
            if(ips.get(i).equals(address))
                return macs.get(i);
        }
        return -1;
    }
}