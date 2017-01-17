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

package packet.data;


import packet.IP;

import java.util.ArrayList;

/**
 * Contenu d'un paquet DHCP
 * Ordre de fonctionnement :
 *  - Le demandeur fait une demande en broadcast
 *  - Le relai dans le même sous-réseau route la demande jusqu'au serveur (sauf s'il est dejà dans le sous-reéseau)
 *  - le serveur réponds avec une plage de propositions
 *  - Le client en choisit une et envoie sa demande au serveur
 *  - Le serveur la valide (ou non) et renvoie un OK (ou renvoie une plage modifiée)
 *  - Le client ACK et utilise l'IP
 *  - FIN DU DHCP
 *  @author J. Desvignes
 */
public class DHCPData
{
    /** La plage d'IP proposée par le serveur DHCP */
    private ArrayList<IP> range = new ArrayList<>();
    /** le masque du sous-réseau du demandeur */
    private IP subnetMask = null;
    /** l'identifiant de la demande */
    public final int identifier;
    /** IP du premier relai DHCP rencontré, utile pour connaître le sous-réseau cible et pour broadcast à ce PC sans IP */
    private IP firstRelay;
    /** IP choisie par le demandeur */
    private IP chosen = null;
    /** Validation du choix par le serveur */
    private boolean ok=false;
    /** Acknowledge du demandeur (j'ai bien pris l'IP) */
    private boolean ack=false;
    /** MAC du demandeur */
    private int askerMAC;
    /** Adresse IP du serveur, set par le premier relai */
    private IP DHCPaddr;

    /**
     * Constructeur
     * @param identifier identifiant
     * @param mac mac du demandeur
     */
    public DHCPData(int identifier, int mac)
    {
        this.identifier=identifier;
        this.askerMAC = mac;
    }

    /**
     * =================================================
     *               GETTERS ET SETTERS
     * =================================================
     */

    public ArrayList<IP> getSubnetInfo()
    {
        return new ArrayList<IP>(){{add(firstRelay);add(subnetMask);}};
    }

    public void setSubnetInfo(IP firstrelay ,IP subnetmask)
    {
        this.firstRelay=firstrelay;
        this.subnetMask=subnetmask;
    }

    public ArrayList<IP> getRange()
    {
        return this.range;
    }

    public void setRange(ArrayList<IP> range)
    {
        this.range = range;
    }

    public boolean setChosen(IP ip)
    {
        if(range.contains(ip))
        {
            chosen = ip;
            return true;
        }
        return false;
    }

    public IP getChosen()
    {
        return chosen;
    }

    public void setOK()
    {
        this.ok = true;
    }

    public boolean isOK()
    {
        return this.ok;
    }

    public void setACK()
    {
        this.ack = true;
    }

    public boolean isACK()
    {
        return this.ack;
    }

    public void setAskerMAC(int mac)
    {
        this.askerMAC = mac;
    }

    public int getAskerMAC()
    {
        return this.askerMAC;
    }
    public IP getDHCPaddr()
    {
        return DHCPaddr;
    }
    public void setDHCPaddr(IP addr)
    {
        this.DHCPaddr = addr;
    }

}