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


import exceptions.BadCallException;
import packet.IP;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe définissant la base de données DHCP, incluant les plages adressables par sous-réseau
 * @author J. Desvignes
 */
public class DHCPTable implements Serializable
{
	/** les sous-réseaux */
    private ArrayList<IP> subnets = new ArrayList<>();
    
    /** les plages associées aux sous-réseaux */
    private ArrayList<IP[]> ranges = new ArrayList<>();

    /**
     * Donne une plage pour le sous-réseau donné
     * Renvoie une plage vide si le sous-réseau n'est pas répertorié
     * @param subnet le sous-réseau
     */
    public IP[] gimmeARange(IP subnet)
    {
        if(!subnets.contains(subnet)) //Si la demande est d'un sous-réseau non couvert par le serveur
        {
            IP[] range = new IP[2];
            range[0] = new IP(0,0,0,0);
            range[1] = new IP(0,0,0,0);
            return range;
        }

        return ranges.get(subnets.indexOf(subnet));
    }

    /**
     * Ajoute une plage
     * @param subnet le sous-réseau en bénéficiant
     * @param range la plage associée
     */
    public void addRange(IP subnet, IP[] range)
    {
        this.subnets.add(subnet);
        this.ranges.add(range);
    }

    /**
     * Renvoie littéralement les plages
     * sous-réseau IPmin IPmax
     */
    public ArrayList<ArrayList<IP>> getAllRanges()
    {
        ArrayList<ArrayList<IP>> res = new ArrayList<>();
        for(int i=0 ; i<subnets.size() ; i++)
        {
            ArrayList<IP> temp = new ArrayList<>();
            temp.add(subnets.get(i));
            temp.add(ranges.get(i)[0]);
            temp.add(ranges.get(i)[1]);
            res.add(temp);
        }
        return res;
    }

    /**
     * Remplace les plages par le tableau fourni ; non destructif si erreur
     */
    public void setAllRanges( ArrayList<ArrayList<IP>> ranges) throws BadCallException
    {
        ArrayList<IP> subnet = new ArrayList<>();
        ArrayList<IP[]> range = new ArrayList<>();

        for(ArrayList<IP> line : ranges)
        {
            if(line.size() != 3)
                throw new BadCallException();

            subnet.add(line.get(0));
            IP[] r = new IP[2];
            r[0] = line.get(1);
            r[1] = line.get(2);
            range.add(r);
        }

        this.subnets = subnet;
        this.ranges = range;
    }

}