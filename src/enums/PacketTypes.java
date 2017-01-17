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
 * Enul des différents types de paquets avec le nombre nécéssaire pour effecteur une transaction et le temps (ms) avant timeout
 * ceux ayant "null" comme nom sont interdis d'utilisation par l'utilisateur
 * 1 packet = 1024 octets
 * @author J. Desvignes
 */
public enum PacketTypes
{
    NULL("null",1,0), //Placeholder
    DHCP("null",1,0),
    WEB("web",10,5000),
    ARP("null",1,10),
    OSPF("null",3,1500),
    RIP("null",2,1500),
    FTP("ftp",2000,10000);

    /** Le nombre de paquets necessaires pour accomplir une transaction */
    public final int size;
    /** La valeur maximale qu'attends un appareil avant de tomber en timeout */
    public final int time;
    /** Le nom du type de paquet */
    private final String name;

    PacketTypes(String name, int val, int time)
    {
        this.size = val;
        this.time = time;
        this.name = name;
    }

    public static PacketTypes getType(String text)
    {
        for(PacketTypes i : PacketTypes.values())
        {
            if(i.name.equals(text))
                return i;
        }
        return null;
    }

}