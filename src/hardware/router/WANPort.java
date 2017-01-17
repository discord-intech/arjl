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

package hardware.router;

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;
import exceptions.BadWANTable;
import exceptions.OverflowException;
import packet.IP;
import packet.Packet;
import multiprocessing.Listener;
import table.WANTable;

import java.net.ConnectException;
import java.util.ArrayList;

/**
 * Classe définissant les ports WAN permettant la communication inter-process de ARJL
 * @author J. Desvignes
 */
public class WANPort extends AbstractRouter
{
    /** Le thread d'écoute du vrai réseau */
    private Listener listener;

    /** La table des sous-réseaux */
    protected WANTable table = new WANTable();

    /** IP du serveur ARJL */
    protected IP IPserv;


    /**
     * Constructeur
     * @param MAC la MAC
     * @param IP l'IP
     * @param default_gateway la passerelle par défaut
     */
    public WANPort(int MAC, IP IP, IP default_gateway) throws BadCallException {
        //Pour initialiser les appareils, JAVA me force à utiliser les classes anonymes, c'est juste horrible...
        super(new ArrayList<LinkTypes>(){{
                  add(LinkTypes.ETH);}},
                new ArrayList<Bandwidth>(){{
                    add(Bandwidth.ETH_1G);}},
                200, new ArrayList<Integer>(){{add(MAC);}}, new ArrayList<IP>(){{add(IP);}},new ArrayList<IP>(){{add(new IP(0,0,0,0));}}, default_gateway, 0, ClockSpeed.HIGH);

    }




    @Override
    protected void treatData(Packet p) throws BadCallException, OverflowException
    {
        if(listener != null && listener.isOK())
            listener.sendPacket(p);
    }

    /**
     * Configure l'IP du serveur ARJL et lance la connexion
     * @param ip l'ip du serveur
     * @throws BadWANTable Si la connexion a échoué pour doublon de sous-réseau
     */
    public void setServerIP(String ip) throws BadCallException, BadWANTable, ConnectException
    {
        if(listener != null)
            listener.stop();

        listener = new Listener(stack,ip,table);
        listener.start();
        this.IPserv = packet.IP.stringToIP(ip);
    }

    /**
     * Renvoie l'IP du serveur auquel le port est connecté
     */
    public IP getServerIP()
    {
        return this.IPserv;
    }

    /**
     * Ajoute un sous-réseau
     * @param subnet le sous-réseau
     * @param mask le masque
     */
    public void addSubnet(IP subnet, IP mask)
    {
        table.addSubnet(subnet, mask);
    }

    /**
     * Affiche les sous-réseaux
     */
    public void printSubnets()
    {
        table.printSubnets();
    }

    /**
     * Remplace entièrement la table WAN
     */
    public void setTable(ArrayList<ArrayList<Object>> sub) throws BadCallException
    {
        this.table.setTable(sub);
    }

    /**
     * Renvoie la table WAN
     */
    public ArrayList<ArrayList<Object>> getTable()
    {
        return this.table.getTable();
    }
}

