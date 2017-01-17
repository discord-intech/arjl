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

package multiprocessing;

import exceptions.BadCallException;
import exceptions.BadWANTable;
import packet.Packet;
import table.WANTable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import static multiprocessing.Server.port;

/**
 * Thread d'écoute du réseau (du vrai), utilisé par les ports WAN
 * @author J. Desvignes
 */
public class Listener extends Thread implements Serializable
{

    /** Stack du port WAN */
    ArrayList<Packet> packets;
    /** Socket réseau */
    private Socket connection;

    /** Sortie et Entrée réseau */
    private ObjectOutputStream out;
    private ObjectInputStream in;


    /**
     * Constructeur
     * @param p le stack du port WAN
     * @param IPserv l'ip du serveur ARJL
     * @param table la table des sous-réseaux
     * @throws BadWANTable si il y a conflit de sous-réseaux sur le serveur ARJL
     */
    public Listener(ArrayList<Packet> p, String IPserv, WANTable table) throws BadCallException, BadWANTable, ConnectException
    {
        this.packets = p;
        try {
            InetAddress address = InetAddress.getByName(IPserv);
            this.connection = new Socket(address, port);

            out = new ObjectOutputStream(connection.getOutputStream());

            out.writeObject(table);
            out.flush();

            in = new ObjectInputStream(connection.getInputStream());

            WANTable res = (WANTable) in.readObject();
            if(!res.subnets.isEmpty())
            {
                throw new BadWANTable(res);
            }

        }
        catch (ConnectException e)
        {
            throw e;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            throw new BadCallException();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new BadCallException();
        }
    }

    @Override
    public void run()
    {

        while(true)
        {
            try
            {
                Packet p = (Packet) in.readObject();
               // System.out.println("Received packet from "+p.src_addr.toString());
                synchronized (packets) {
                    p.lastPort = -1;
                    packets.add(p);
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    /**
     * Envoie un paquet au serveur ARJL
     */
    public void sendPacket(Packet p)
    {
        try {
            out.writeObject(p);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Renvoie l'état du listener */
    public boolean isOK()
    {
        return connection.isConnected() && !connection.isClosed();
    }
}
