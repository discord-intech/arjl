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

package packet;

import exceptions.BadCallException;

import java.io.Serializable;

/**
 * Classe servant de template pour les adresses IPv4
 * @author J. Desvignes
 */

public class IP implements Serializable
{

    /** Les différents octets */
    public final int o1;
    public final int o2;
    public final int o3;
    public final int o4;


    public IP(int o1, int o2, int o3, int o4)
    {

        this.o1 = o1;
        this.o2 = o2;
        this.o3 = o3;
        this.o4 = o4;

    }

    /**
     * Renvoie le sous-réseau avec le masque fourni
     * @param address le masque
     * @return l'IP du sous-réseau
     */
    public IP getSubnet(IP address)
    {
        return new IP(address.o1 & this.o1,
                address.o2 & this.o2,
                address.o3 & this.o3,
                address.o4 & this.o4);
    }

    /**
     * Vérifie que l'adresse fournie est plus grande que celle-ci
     * @param other l'adresse à comparer
     */
    public boolean isSmallerThan(IP other)
    {
        if(other.o1 > this.o1)
            return true;
        else if (other.o1 < this.o2)
            return false;
        else //égalité
        {
            if(other.o2 > this.o2)
                return true;
            else if (other.o2 < this.o2)
                return false;
            else //égalité
            {
                if(other.o3 > this.o3)
                    return true;
                else if (other.o3 < this.o3)
                    return false;
                else //égalité
                {
                    if(other.o4 > this.o4)
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Convertit un string représentant une ipv4 en un objet IP
     * @param text le string représentant l'IP
     * @return l'IP
     * @throws BadCallException si l'IP envoyée est mauvaise
     */
    public static IP stringToIP(String text) throws BadCallException
    {
        String[] vals = text.toLowerCase().replaceAll(" ", "").split("\\.");
        if(vals.length != 4)
            throw new BadCallException();
        try
        {
            int o1 = Integer.parseInt(vals[0]);
            int o2 = Integer.parseInt(vals[1]);
            int o3 = Integer.parseInt(vals[2]);
            int o4 = Integer.parseInt(vals[3]);
            if( (o1>255) || (o1<0) || (o2>255) || (o2<0) || (o3>255) || (o3<0) || (o4>255) || (o4<0) )
                throw new BadCallException();
            return new IP(o1, o2, o3, o4);
        }
        catch(NumberFormatException e)
        {
            throw new BadCallException();
        }
    }

    /**
     * A ne faire que sur des subnets, vérifie que le masque donné est bon
     * @param mask le masque à appliquer
     * @return true/false selon la validité
     */
    public boolean checkMask(IP mask)
    {
        return getSubnet(mask).equals(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if(o instanceof IP)
        {
            IP other = (IP) o;
            return ((this.o1 == other.o1) && (this.o2 == other.o2) && (this.o3 == other.o3) && (this.o4 == other.o4));
        }
        return false;
    }

    @Override
    public String toString()
    {
        return o1+"."+o2+"."+o3+"."+o4;
    }

    /**
     * Vérifie s'il s'agit d'une IP de broadcast
     * @param mask masque du sous-réseau
     */
    public boolean isBroadcast(IP mask)
    {
        return ((o1 | mask.o1) & (o2 | mask.o2) & (o3 | mask.o3) & (o4 | mask.o4)) == 255;
    }
}