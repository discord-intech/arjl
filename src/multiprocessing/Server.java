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

import enums.PacketTypes;
import packet.Packet;
import table.WANTable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

/**
 * Classe principale d'une connexion au serveur + main du serveur ARJL
 *
 * @author J. Desvignes
 */
public class Server implements Runnable
{
    /** Socket de la connexion */
    private Socket connection;
    /** Flux de sortie et entrée */
    private ObjectOutputStream out;
    private ObjectInputStream in;
    /** L'identifiant de la connexion */
    public int ID;

    /** Port attribué aux connexions ARJL */
    final static int port = 12547;

    /** Table des sous-réseaux */
    private static WANTable table = new WANTable();

    /** Liste des clients */
    private static ArrayList<Server> clients = new ArrayList<>();



    /**
     * MAIN
     */
    public static void main(String[] args) {
        int count = 0;
        try
        {
            ServerSocket socket1 = new ServerSocket(port);
            System.out.println("Serveur ARJL lancé ; En attente de connexions");
            while (true)
            {
                Socket connection = socket1.accept();
                Runnable runnable = new Server(connection, ++count);
                Thread thread = new Thread(runnable);
                thread.start();
                System.out.println("Connexion avec "+connection.getInetAddress().toString()+" effectuée ! ID : "+count);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie le client s'il peut se connecter
     */
    private boolean checkClient(Socket connection)
    {
        try {
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        try
        {
            WANTable tab = (WANTable) in.readObject();
            WANTable isOK =  table.addSubnets(tab, this.ID);

            out = new ObjectOutputStream(connection.getOutputStream());

            out.writeObject(isOK);
            out.flush();

            return isOK.subnets.isEmpty();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return false;

    }


    Server(Socket s, int i) {
        this.connection = s;
        this.ID = i;
    }

    @Override
    public void run()
    {
        if(!checkClient(connection))
        {
            System.out.println("Doublon de subnet pour "+connection.getInetAddress().toString());
            return;
        }

        Server.clients.add(this);

        while(true)
        {
            try {
               // System.out.println("En attente !");

                Packet p = (Packet) in.readObject();
                Server.route(p, this.ID);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        }
        Server.remove(this);

    }

    /**
     * Supprime un client
     */
    private synchronized static void remove(Server instance)
    {
        table.removeID(instance.ID);
        Server.clients.remove(instance);
    }

    /**
     * Route le paquet vers un client ARJL
     */
    private synchronized static void route(Packet p, int sourceid) throws IOException {
        int id = Server.table.route(p.dst_addr);
        if(id == -1 && !(p.getType() == PacketTypes.RIP || p.getType() == PacketTypes.ARP))
            return;

        try {
            for (int i = 0; i < Server.clients.size(); i++) {
                if (Server.clients.get(i).ID == id || ((p.getType() == PacketTypes.RIP || p.getType() == PacketTypes.ARP)
                        && Server.clients.get(i).ID != sourceid )) {
                    Server.clients.get(i).sendPacket(p);
                    return;
                }
            }
        } catch (IndexOutOfBoundsException e) { //Si à ce moment un client décide de se connecter
            route(p,sourceid);
        }
    }

    /**
     * Envoie un paquet vers un client ARJL
     */
    public synchronized void sendPacket(Packet p) throws IOException
    {
        if(this.connection.isClosed())
        {
            throw new IOException();
        }
        try {

            out.writeObject(p);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
