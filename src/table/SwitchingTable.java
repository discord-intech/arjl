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

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe définissant les tables de commutation
 * @author J. Desvignes
 */
public class SwitchingTable implements Serializable
{
    /** Les MACs */
    private final ArrayList<Integer> macs = new ArrayList<>();
    /** Les ports associés */
    private final ArrayList<Integer> ports = new ArrayList<>();

    /**
     * Ajoute une règle
     */
    public synchronized void addRule(int mac, int port)
    {
        if(macs.contains(mac))
            return;
        macs.add(mac);
        ports.add(port);
    }

    /**
     * Cherche la mac dans sa table et renvoie le port correspondant
     * @param mac la mac
     * @return le port correspondant ; -1 si absente de la table
     */
    public int commute(int mac)
    {
        for(int i=0 ; i<macs.size() ; i++)
            if(macs.get(i).equals(mac))
                return ports.get(i);
        return -1;
    }

    /**
     * Supprime toutes les règles associées à un port (en cas de deconnexion)
     * @param port le port
     */
    public void removeRules(int port)
    {
        for(int i=0 ; i<ports.size() ; i++)
        {
            if(port == ports.get(i))
            {
                this.ports.remove(i);
                this.macs.remove(i);
                i--;
            }
        }
    }
}