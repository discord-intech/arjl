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
 * Table des sous-réseaux adaptée à une connexion multiprocess
 * Utilisée par les clients et serveurs ARJL, mais pas de la même manière
 * @author J. Desvignes
 */
public class WANTable implements Serializable
{
    /** les sous-réseaux */
    public ArrayList<IP> subnets = new ArrayList<>();
    /** les masques de sous-réseau */
    public ArrayList<IP> masks = new ArrayList<>();
    /** les ids des clients ARJL ; utilisés uniquement par le serveur*/
    public ArrayList<Integer> ids = new ArrayList<>();


    public WANTable(ArrayList<IP> sub, ArrayList<IP> mask)
    {
        this.subnets = sub;
        this.masks = mask;
    }

    public WANTable()
    {

    }

    /**
     * Ajoute les subnets reçus par le serveur ; renvoie la liste des doublons sinon
     * @param tab table WAN
     */
    public WANTable addSubnets(WANTable tab, int id)
    {
        if(!AND(tab.subnets).isEmpty())
        {
            ArrayList<IP> m = new ArrayList<>();
            ArrayList<IP> r = AND(tab.subnets);

            for(IP i : AND(tab.subnets))
            {
                m.add(masks.get(subnets.indexOf(i)));
            }

            return new WANTable(AND(tab.subnets), m);
        }

        this.subnets.addAll(tab.subnets);
        this.masks.addAll(tab.masks);
        while(subnets.size() - ids.size() > 0)
            ids.add(id);
        return new WANTable();
    }

    /**
     * Set la table écrite sur interface graphique
     */
    public void setTable(ArrayList<ArrayList<Object>> in) throws BadCallException
    {
        ArrayList<IP> subnet = new ArrayList<>();
        ArrayList<IP> mask = new ArrayList<>();

        for(ArrayList<Object> line : in)
        {
            if(line.size() != 2 || !(line.get(0) instanceof IP) || !(line.get(1) instanceof IP))
            {
                throw new BadCallException();
            }

            // En cas de mauvais masque
            if(!((IP)line.get(0)).checkMask((IP)line.get(1)))
            {
                throw new BadCallException();
            }

            subnet.add((IP)line.get(0));
            mask.add((IP)line.get(1));
        }

        this.subnets = subnet;
        this.masks = mask;
    }

    /**
     * Revoie la table complète
     */
    public ArrayList<ArrayList<Object>> getTable()
    {
        ArrayList<ArrayList<Object>> res = new ArrayList<>();

        for(int i=0 ; i<subnets.size() ; i++)
        {
            ArrayList<Object> line = new ArrayList<>();
            line.add(subnets.get(i));
            line.add(masks.get(i));
            res.add(line);
        }

        return res;
    }

    /**
     * Renvoie les doublons entre la table this et la table donnée
     */
    public ArrayList<IP> AND(ArrayList<IP> sub)
    {
        ArrayList<IP> res = new ArrayList<>();

        for(IP i : sub)
            if(subnets.contains(i))
                res.add(i);
        return res;
    }

    /**
     * Ajoute une entrée
     */
    public void addSubnet(IP subnet, IP mask)
    {
        if(subnets.contains(subnet))
            return;
        subnets.add(subnet);
        masks.add(mask);
    }

    /**
     * Imprime les sous-réseaux
     */
    public void printSubnets()
    {
        System.out.println("Subnet         Mask");
        for (int i=0 ; i<subnets.size() ; i++)
        {
            System.out.println(subnets.get(i).toString()+"  "+masks.get(i).toString());
        }
    }

    /**
     * Route le paquet vers le bon client ARJL
     */
    public int route(IP dst_addr)
    {
        ArrayList<Integer> potres = new ArrayList<>();

        for(int i=0 ; i<subnets.size() ; i++)
        {
            if (dst_addr.getSubnet(masks.get(i)).equals(subnets.get(i)))
            {
                potres.add(i);
            }
        }

        if(potres.isEmpty())
            return -1;
        int pos = potres.get(0);

        for(int i = 0 ; i<potres.size() ; i++)
        {
            if(masks.get(pos).isSmallerThan(masks.get(potres.get(i))))
                pos = potres.get(i);
        }

        return ids.get(pos);
    }

    /**
     * Retire les sous-réseaux réservés par un client dont l'id est donnée (en cas de déconnexion)
     */
    public void removeID(int id)
    {
        for(int i=0 ; i<subnets.size() ; i++)
        {
            if(ids.get(i) == id)
            {
                subnets.remove(i);
                ids.remove(i);
                masks.remove(i);
                i--;
            }
        }
    }
}
