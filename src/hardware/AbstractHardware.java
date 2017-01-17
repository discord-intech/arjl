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

package hardware;


import enums.Bandwidth;
import enums.ClockSpeed;
import enums.Hardware;
import enums.LinkTypes;
import exceptions.BadCallException;
import exceptions.NoFreePortsException;
import exceptions.OverflowException;
import graphics.DeviceManager;
import hardware.client.AbstractClient;
import hardware.router.AbstractRouter;
import hardware.router.WANPort;
import hardware.server.AbstractServer;
import javafx.application.Platform;
import packet.Packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Classe abstraite définissant les appareils connectés
 * Elle dérive de Thread car chaque noeud du graphe est un thread
 * Se lance avec la méthode start()
 * @author J. Desvignes
 */
public abstract class AbstractHardware extends Thread implements Serializable {
    /** Liste des liens connectés */
    protected final ArrayList<Link> ports = new ArrayList<>();

    /** Liste des types de ports disponibles sur l'appareil */
    protected final ArrayList<LinkTypes> port_types;

    /** Liste des bandes passantes par port (ETH-100M / ETH-1G / ...) */
    protected final ArrayList<Bandwidth> port_bandwidth;

    /** Liste de paquets représentant le tampon de l'appareil */
    protected ArrayList<Packet> stack = new ArrayList<>();

    /** Représente le maximum supportable par l'appareil en terme de paquets dans son tampon (stack) */
    protected final int overflowValue;

    /** Vitesse d'horloge de l'appareil */
    protected final ClockSpeed treatmentSpeed;

    /** Utilisé pour simulation de collisions */
    public static final Random RNG = new Random(System.currentTimeMillis());

    /** Valeur de probabilité de collision (en mille-pour-cent) */
    protected static final int collisionRate = 0;

    /** Permet de compter les paquets pour simuler la bande passante */
    protected int[] packetsSent;

    /** Permet de savoir quand reset les compteurs pour la gestion de la bande passante */
    private long lastResetTime;

    /** Si l'appareil est allumé ou non */
    private boolean isON = false;

    /** Permet de mettre l'appareil en verbose */
    protected boolean isVerbose = false;
    public String nickname;

    /** Vitesse de simulation */
    public static float TIMESPEED = 1;

    /** Taux d'utilisation de la bande passante de ce lien en % */
    public int[] bandwidthUse;

    /**
     * Constructeur à appeller avec super()
     * @param port_types liste des types de liens connectables
     * @param port_bandwidth listes des bandes passantes (couplée avec port_types !)
     * @param overflow maximum de paquets supportables dans son tampon de traitement
     */
    public AbstractHardware(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth, int overflow, ClockSpeed speed) throws BadCallException {
        if(port_bandwidth.size() != port_types.size())
            throw new BadCallException();
        this.port_types = port_types;
        this.port_bandwidth = port_bandwidth;
        this.overflowValue = overflow;
        this.treatmentSpeed = speed;
        this.packetsSent = new int[port_bandwidth.size()];
        this.bandwidthUse = new int[port_bandwidth.size()];
        for(int i=0;i<port_bandwidth.size();i++)
            ports.add(null);
        this.lastResetTime = System.currentTimeMillis();
        Thread.currentThread().setPriority(1);
    }

    /**
     * Permet de calculer les statistiques d'utilisation de l'appareil (file d'attente)
     */
    private void stats()
    {
        for(int i=0 ; i<port_bandwidth.size() ; i++)
        {
            if(ports.get(i) == null || ports.get(i).getOtherHardware(this).getConnectedLinks().indexOf(ports.get(i)) == -1)
                continue;

            AbstractHardware other = ports.get(i).getOtherHardware(this);
            this.bandwidthUse[i] = (int)((packetsSent[i]+other.packetsSent[other.getConnectedLinks().indexOf(ports.get(i))])/(double)port_bandwidth.get(i).value*100);
        }
    }

    /**
     * Donne les statistiques d'utilisation d'un lien connecté à l'appareil
     * @param link le lien connecté à l'appareil
     * @return le % d'utilisation
     * @throws BadCallException si le lien n'est pas connecté à l'appareil
     */
    public int getLinkStats(Link link) throws BadCallException
    {
        if(!ports.contains(link))
            throw new BadCallException();
        return bandwidthUse[ports.indexOf(link)];
    }

    /**
     * Renvoie le ù d'utilisation de la file d'attente de l'appareil
     * @return
     */
    public int getStats()
    {
        return (int)(stack.size()/(double)overflowValue*100);
    }

    /**
     *  Boucle de traitement de l'appareil
     */
    @Override
    public void run()
    {
        long time = System.currentTimeMillis();
        while(true)
        {
            try
            {
                this.stats();
                this.treat();

                if(this instanceof AbstractClient)
                {
                    if(((AbstractClient)this).timeoutCheck() && isVerbose) {
                        System.out.println(nickname + " : TIMEOUT !!");



                        if(DeviceManager.graphic) {
                            Platform.runLater ( () -> {
                                ArrayList<AbstractHardware> abstractHardwareArrayList = DeviceManager.getArray_list_of_devices();
                                ArrayList<Integer> arrayListOfStateError = DeviceManager.getArrayListOfStateError();


                                arrayListOfStateError.set(abstractHardwareArrayList.indexOf(this), new Integer(1000));
                            });
                        }


                    }

                }

                if(this instanceof AbstractRouter && ((AbstractRouter)this).RIP && !(this instanceof AbstractServer || this instanceof AbstractClient || this instanceof WANPort))
                {
                    //TODO RIPv2
                    if(System.currentTimeMillis() - time >= 30000)
                    {
                        ((AbstractRouter)this).sendRoutingTable();
                        time = System.currentTimeMillis();
                    }
                }

                //Boucle d'attente pour simuler la vitesse de traitement (infinie si appareil est éteint)
                do
                {
                    Thread.sleep((int)(this.treatmentSpeed.speed * AbstractHardware.TIMESPEED));
                } while(!isON);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (OverflowException|BadCallException e)
            {
                if(DeviceManager.graphic)
                {
                    Platform.runLater ( () -> {
                        ArrayList<AbstractHardware> abstractHardwareArrayList = DeviceManager.getArray_list_of_devices();
                        ArrayList<Integer> arrayListOfStateError = DeviceManager.getArrayListOfStateError();


                        arrayListOfStateError.set(abstractHardwareArrayList.indexOf(this), new Integer(1000));
                    });
                }

            }

        }
    }

    /**
     * Permet d'éteindre et allumer l'appareil
     * @param state true = ON ; false = OFF
     */
    public void changeState(boolean state)
    {
        this.isON = state;
    }

    /**
     * Si l'appareil est allumé et en fonctionnement
     */
    public boolean isRunning()
    {
        return this.isON;
    }

    /**
     * Renvoie les liens connectés à l'appareil
     */
    public ArrayList<Link> getConnectedLinks()
    {
        return ports;
    }

    /**
     * Renvoie une liste des types de ports disponibles
     */
    public ArrayList<LinkTypes> getFreePorts()
    {
        ArrayList<LinkTypes> free = new ArrayList<>();
        // On vérifie les ports libres, s'ils le sont, on envoie leur type
        for(int i=0 ; i < ports.size() ; i++)
        {
            if(ports.get(i) == null)
                free.add(port_types.get(i));
        }
        return free;
    }

    /**
     * Permet de connecter un lien
     * @param link le lien
     * @throws NoFreePortsException si aucun port libre
     */
    public void connectLink(Link link) throws NoFreePortsException
    {
        // A chaque port libre, on vérifie s'il est du bon type et de la bonne bande passante
        for(int i=0 ; i < ports.size() ; i++)
        {
            if(ports.get(i) == null)
            {
                if(port_types.get(i) == link.getType() && port_bandwidth.get(i) == link.getBandwidth())
                {
                    ports.set(i, link);
                    return;
                }
            }
        }

        // Echec : on rééssaie sans prendre en compte la bande passante (ex : ETH-100M sur un ETH-1G)
        for(int i=0 ; i < ports.size() ; i++)
        {
            if(ports.get(i) == null)
            {
                if(port_types.get(i) == link.getType())
                {
                    ports.set(i, link);
                    return;
                }
            }
        }

        //Echec critique : impossible de connecter
        throw new NoFreePortsException();
    }

    /**
     * Renvoie la bande passante la plus élevée disponible
     * @param linkType le type de lien
     */
    public Bandwidth getBestBandwidth(LinkTypes linkType) throws BadCallException {
        Bandwidth fastest = Bandwidth.NULL;
        for(int i=0 ; i<port_types.size() ; i++)
        {
            if(port_types.get(i) == linkType)
            {
                if(port_bandwidth.get(i).value > fastest.value)
                    fastest = port_bandwidth.get(i);
            }
        }

        if(fastest == Bandwidth.NULL)
            throw new BadCallException();

        return fastest;

    }

    /**
     * Déconnecte un lien
     * @param link le lien
     */
    public void disconnect(Link link) throws BadCallException {
        for(int i=0 ; i<ports.size() ; i++)
        {
            if(ports.get(i) == link) {
                ports.set(i, null);
                return;
            }
        }
        throw new BadCallException();
    }

    /**
     * Revoie le port sur lequel le lien donné est connecté
     * @param link le lien
     * @return le port ou -1 si le lien n'est pas connecté
     */
    public int whichPort(Link link)
    {
        for(int i=0 ; i<ports.size() ; i++)
        {
            if(ports.get(i) == link)
                return i;
        }
        return -1;
    }

    /**
     * Ces méthodes envoient et reçoivent des paquets, c'est la couche physique
     */
    public abstract void receive(Packet packet, int port) throws OverflowException;

    public void send(Packet packet, int port) throws BadCallException, OverflowException {
        AbstractHardware other = ports.get(port).getOtherHardware(this);
        if(!other.isON)
            return;
        if(System.currentTimeMillis() - this.lastResetTime >= 1000) //Si on a dépassé une seconde on reset le compteur
        {
            for(int i=0 ; i<packetsSent.length ; i++)
                packetsSent[i] = 0;
        }
        if(packetsSent[port]+other.packetsSent[other.getConnectedLinks().indexOf(ports.get(port))] > port_bandwidth.get(port).value) //Gestion de la bande passante
        {
            packet.alreadyTreated = true;
            packet.destinationPort=port;
            synchronized (this.ports) {
                stack.add(packet);
            }
            return;
        }
        if(RNG.nextInt(10001) < collisionRate) //Si on perd le paquet dans une collision
            return;
        if(packet.TTLdown())
            return;
        Link link = ports.get(port);
        link.getOtherHardware(this).receive(packet, ports.get(port).getOtherHardware(this).whichPort(link));
        if(isVerbose)
            System.out.println(this.nickname+" : sent "+packet.getType()+" to "+packet.dst_addr+" with NHR="+packet.getNHR()+" isResponse="+packet.isResponse);
    }

    /**
     * Cette méthode lance le traitement du tampon, couches liaison de données et réseau
     */
    public abstract void treat() throws BadCallException, OverflowException;

    /**
     * Active la verbose sur cet appareil
     * @param nickname le surnom qu'il affichera
     */
    public void setVerbose(String nickname)
    {
        this.isVerbose = true;
        this.nickname = nickname;
    }

    /**
     * Active la verbose sur cet appareil
     *
     */
    public void setVerbose()
    {
        this.isVerbose = true;

    }
    /**
     * Arrête la verbose
     */
    public void stopVerbose()
    {
        this.isVerbose = false;
        this.nickname = null;
    }

    /**
     * Set une vitesse de simulation
     * @param speed la vitesse comprise entre 0.1 et 100
     * @throws BadCallException
     */
    public static void setSpeed(float speed) throws BadCallException
    {
        if(speed < 0.1 || speed > 100)
            throw new BadCallException();
        AbstractHardware.TIMESPEED = 1 / speed;
    }

    /**
     * Affiche les informations de l'appareil
     */
    public void print() throws BadCallException
    {
        System.out.println();
        System.out.println("Type d'appareil : "+ Hardware.getName(this));
        for(int i=0 ; i<ports.size() ; i++)
        {
            System.out.println();
            System.out.println("Port "+i+":");
            System.out.println("    Type : "+port_types.get(i).name()+"   Bande passante : "+port_bandwidth.get(i).name());
            if(ports.get(i) == null)
                System.out.println("    Connecté : Non");
            else
                System.out.println("    Connecté : Oui");
            this.printMore(i);
        }
    }

    /**
     * Utilisé pour les routeurs et autres pour afficher leurt configuration IP
     * @param port le port actuellement traité
     */
    protected void printMore(int port) //Utilisé pour indiquer l'IP ou autre
    {

    }

}