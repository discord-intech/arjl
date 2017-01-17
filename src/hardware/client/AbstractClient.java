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

package hardware.client;

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import hardware.router.AbstractRouter;
import packet.IP;
import packet.Packet;
import table.WaitingTable;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les clients (end-user)
 * @author J. Desvignes
 */
public abstract class AbstractClient extends AbstractRouter
{
    /**
     * La MAC de l'appareil
     */
    protected int MAC;

    /**
     * Table d'attente de réponses
     */
    protected WaitingTable waitingRequests = new WaitingTable();

    /** Si le client est en mode répétition de demande */
    protected boolean repetitiveRequest = false;

    /** Type de demande à répéter */
    protected PacketTypes requestType;

    /** Le serveur vers qui envoyer la demande */
    protected IP server;

    /** Temps d'attente (ms) entre chaque demande réussie */
    protected int waitingTime;


    /**
     * Constructeur à appeller
     *
     * @param port_type le type de connectique
     * @param port_bandwidth la bande passante de la connectique
     * @param overflow       maximum de paquets supportables dans son tampon de traitement
     * @param MAC la mac de l'appareil
     * @param IP l'IP statique
     * @param default_gateway la passerelle par défaut
     */
    public AbstractClient(LinkTypes port_type, Bandwidth port_bandwidth, int overflow, int MAC,
                          IP IP, IP default_gateway, ClockSpeed speed) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}}, new ArrayList<IP>(){{add(IP);}}, new ArrayList<IP>(){{add(new IP(0,0,0,0));}},
                default_gateway, 0, speed);
        this.IP = IP;
        this.MAC = MAC;
    }

    /**
     * Constructeur à appeller
     *
     * @param port_type le type de connectique
     * @param port_bandwidth la bande passante de la connectique
     * @param overflow       maximum de paquets supportables dans son tampon de traitement
     * @param MAC  la mac de l'appareil
     */
    public AbstractClient(LinkTypes port_type, Bandwidth port_bandwidth, int overflow, int MAC, ClockSpeed speed) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}},
                new ArrayList<IP>(){{add(null);}}, new ArrayList<IP>(){{add(null);}}, speed);
        this.MAC = MAC;
    }

    @Override
    public void receive(Packet packet, int port) throws OverflowException {
        if(this.stack.size() >= this.overflowValue)
            throw new OverflowException(this);
        packet.lastPort = port;
        synchronized (this.ports) {
            this.stack.add(packet);
        }
    }

    @Override
    protected void treatData(Packet p) throws BadCallException {
        if (p.isResponse)
        {
            waitingRequests.doIWaitForIt(p.getType(), p.src_addr);
        }
    }

    /**
     * Lance une requête spécifiée
     * @param type le type de paquet, donc le type de requête
     * @param destination l'IP du destinataire
     */
    public synchronized void launchRequest(PacketTypes type, IP destination)
    {
        if(this.awaitingForIP[0])
        {
            return;
        }
        if(type == PacketTypes.WEB)
        {
            stack.add(new Packet(destination, this.IPinterfaces.get(0), this.MACinterfaces.get(0), -1, PacketTypes.WEB, false, false));
            waitingRequests.addWaiting(PacketTypes.WEB, destination);
            //System.out.println(this.IP+" : Envoi requête WEB vers "+destination);
        }
        else if(type == PacketTypes.FTP)
        {
            stack.add(new Packet(destination, this.IPinterfaces.get(0), this.MACinterfaces.get(0), -1, PacketTypes.FTP, false, false));
            waitingRequests.addWaiting(PacketTypes.FTP, destination);
        }
    }

    /**
     * Renvoie true si l'appareil attends la réponse d'un appareil
     */
    public boolean waitsForSomething()
    {
        return waitingRequests.AmIWaitingForSomething();
    }

    /**
     * Vérifie les timeout et les décrémente, supprime une requête si elle a timeout
     * @return true si on a eu un timeout, false sinon
     */
    public boolean timeoutCheck()
    {
        return this.waitingRequests.isThereATimeout();
    }

    public int progress()
    {
        return this.waitingRequests.progress();
    }

    /**
     * Permet de lancer une requête si elle a été mise en répétition, dans la boucle treat
     */
    @Override
    protected void clientRequest()
    {
        if(this.repetitiveRequest && !waitsForSomething())
        {
            try {
                Thread.sleep(waitingTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.launchRequest(requestType, server);
        }
    }

    /**
     * Lance une requête répétitive ; une seule par client
     * @param type le type de requête
     * @param server l'IP du serveur cible
     * @param waitingTime le temps de traitement en ms à chaque transaction réussie
     */
    public void setRepetitiveRequest(PacketTypes type, IP server, int waitingTime)
    {
        this.server = server;
        this.requestType = type;
        this.waitingTime = waitingTime;
        this.repetitiveRequest = true;
    }

    /**
     * Arrête une requête répétitive
     */
    public void stopRepetitiveRequest()
    {
        this.repetitiveRequest = false;
        this.waitingRequests.clear();
    }

}