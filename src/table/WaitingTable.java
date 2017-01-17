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

import enums.PacketTypes;
import hardware.AbstractHardware;
import packet.IP;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Cette table représente les attentes d'un client en terme de réponse d'un serveur
 * Elle représente les transactions en attente et le temps écoulé depuis l'envoi de la demande
 * Elle est surtout utile pour représenter le "TimeOut" d'une requête
 * @author J. Desvignes
 */
public class WaitingTable implements Serializable
{
    /** Représente le type de requète envoyé */
    private final ArrayList<PacketTypes> types = new ArrayList<>();

    /** L'adresse IP du serveur dont on attends la réponse*/
    private final ArrayList<IP> servers = new ArrayList<>();

    /** L'instant auquel la demande a été formulée */
    private final ArrayList<Long> timing = new ArrayList<>();

    /**
     * Nombres de paquets attendus restants
     */
    private final ArrayList<Integer> numberOfPackets = new ArrayList<>();

    /**
     * Supprime les attentes ayant timeout
     * Incrémente les autres attentes de 1
     * @return true s'il y a eu un timeout, false sinon
     */
    public synchronized boolean isThereATimeout()
    {
        boolean res = false;
        long now = System.currentTimeMillis();
        for(int i=0 ; i<types.size() ; i++)
        {
            if(now-timing.get(i) >= (int)(types.get(i).time* AbstractHardware.TIMESPEED))
            {
                timing.remove(i);
                types.remove(i);
                servers.remove(i);
                numberOfPackets.remove(i);
                i--;
                res = true;
            }
        }
        return res;
    }

    /**
     * Ajoute une attente
     * @param type le type de paquet en attente
     * @param dst le serveur dont on attend la réponse
     */
    public synchronized void addWaiting(PacketTypes type, IP dst)
    {
        this.types.add(type);
        this.servers.add(dst);
        this.timing.add(System.currentTimeMillis());
        this.numberOfPackets.add(type.size);
    }

    /**
     * Supprime une attente (réponse reçue)
     * @param type le type reçu
     * @param dst le serveur répondant
     */
    public synchronized void removeWaiting(PacketTypes type, IP dst)
    {
        for(int i=0 ; i<types.size() ; i++)
        {
            if(types.get(i) == type && servers.get(i).equals(dst))
            {
                types.remove(i);
                servers.remove(i);
                timing.remove(i);
                numberOfPackets.remove(i);
                return;
            }
        }
    }

    /**
     * Es-ce que ce j'attends une réponse de ce serveur?
     * Décrémente le nombre de paquets attendus si oui
     * Si ce dernier atteind 0, la requête est supprimée
     * @param type le type reçu
     * @param dst le serveur ayant répondu
     * @return true si oui, false sinon
     */
    public synchronized boolean doIWaitForIt(PacketTypes type, IP dst)
    {
        for(int i=0 ; i<types.size() ; i++)
        {
            if(types.get(i) == type && servers.get(i).equals(dst))
            {
                numberOfPackets.set(i, numberOfPackets.get(i)-1);
                if(numberOfPackets.get(i) == 0)
                {
                    this.removeWaiting(type, dst);
                    System.out.println("Requête "+type.name()+" réussie");
                }
                else
                    timing.set(i, System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    /**
     * Es-ce que j'attends une réponse ?
     */
    public synchronized boolean AmIWaitingForSomething()
    {
        return !types.isEmpty();
    }

    /**
     * Vide la table d'attente
     */
    public synchronized void clear()
    {
        this.types.clear();
        this.servers.clear();
        this.timing.clear();
        this.numberOfPackets.clear();
    }

    /**
     * Progrès de la requête principale en cours
     * @return le % d'avancement
     */
    public synchronized int progress()
    {
        if(!numberOfPackets.isEmpty())
            return (int)((this.types.get(0).size - (double)this.numberOfPackets.get(0)) / this.types.get(0).size * 100);
        return 0;
    }
}