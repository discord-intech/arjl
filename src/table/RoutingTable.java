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
import packet.Packet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe définissant les tables de routages, fonctionne selon la règle du plus grand masque
 * @author J. Desvignes
 */
public class RoutingTable implements Serializable
{

    /** les sous-réseaux */
    protected ArrayList<IP> subnets = new ArrayList<>();
    /** les masques de sous-réseau */
    protected ArrayList<IP> masks = new ArrayList<>();
    /** les passerelles (ou NHR) */
    protected ArrayList<IP> gateways = new ArrayList<>();
    /** les numéros de port associés */
    protected ArrayList<Integer> port_numbers = new ArrayList<>();
    /** metrics*/
    protected ArrayList<Integer> metrics = new ArrayList<>();

    /**
     * Constructeur de la table
     * @param default_route_port le port du routeur pour l'adresse par défaut
     * @param default_gateway la passerelle par défaut (généralement ce qui dirige vers le réseau opérateur)
     */
    public RoutingTable(int default_route_port, IP default_gateway)
    {
        port_numbers.add(default_route_port);
        subnets.add(new IP(0,0,0,0));
        masks.add(new IP(0,0,0,0));
        gateways.add(default_gateway);
        metrics.add(0);
    }

    /**
     * Constructeur de la table ; port par défaut sur 0 avec 0.0.0.0 pour IP
     */
    public RoutingTable()
    {
        port_numbers.add(0);
        subnets.add(new IP(0,0,0,0));
        masks.add(new IP(0,0,0,0));
        gateways.add(new IP(0,0,0,0));
        metrics.add(0);
    }

    /**
     * Constructeur copiant les arrays fournis
     */
    public RoutingTable(ArrayList<IP> subnets, ArrayList<IP> masks, ArrayList<IP> gateways, ArrayList<Integer> ports, ArrayList<Integer> metrics)
    {
        this.subnets = (ArrayList<IP>) subnets.clone();
        this.masks = (ArrayList<IP>) masks.clone();
        this.gateways = (ArrayList<IP>) gateways.clone();
        this.port_numbers = (ArrayList<Integer>) ports.clone();
        this.metrics = (ArrayList<Integer>) metrics.clone();
    }

    /**
     * Ajoute une règle
     */
    public synchronized void addRule(int port_number, IP subnet, IP mask, IP gateway)
    {
        port_numbers.add(port_number);
        subnets.add(subnet);
        masks.add(mask);
        gateways.add(gateway);
    }

    /**
     * Route le paquet
     * @param address l'adresse du paquet à router
     * @return une liste { le port de destination ; l'IP du NHR }
     */
    public ArrayList<Object> routeMe(IP address)
    {
        ArrayList<Object> res = new ArrayList<>();
        ArrayList<Integer> pot_res = new ArrayList<>(); // Représente les résultats potentiels

        //On cherche les différentes entrées pour ce sous-réseau
        for(int i=0 ; i < masks.size() ; i++)
        {
            if(subnets.get(i).equals(address.getSubnet(masks.get(i))))
            {
                pot_res.add(i);
            }
        }

        if(pot_res.isEmpty())
            return new ArrayList<Object>(){{add(port_numbers.get(0)); add(gateways.get(0));}};

        int j=pot_res.get(0); // Cette variable représente le numéro d'entrée actuellement sélectionnée

        for(int index : pot_res) //On prend celui au plus grand masque
            if(masks.get(j).isSmallerThan(masks.get(index)))
                j=index;


        res.add(port_numbers.get(j));
        res.add(gateways.get(j));

        return res;
    }

    /**
     * Si le paquet est un broadcast
     * @param p le paquet
     */
    public boolean isBroadcast(Packet p)
    {
        ArrayList<Object> res = this.routeMe(p.dst_addr);

        if(res.get(1).equals(new IP(0,0,0,0)))
            return false;

        IP mask = masks.get(gateways.indexOf(res.get(1)));
        return p.dst_addr.isBroadcast(mask);

    }

    /**
     * Revoie le masque associé à un sous-réseau listé dans la table
     * @param subnet le masque
     */
    public IP getRelatedMask(IP subnet)
    {
        for(IP i : gateways)
        {
            if(i.equals(subnet))
                return masks.get(gateways.indexOf(i));
        }
        return null;
    }

    /**
     * Modifie la passerelle par défaut
     * @param gate l'IP de la passerelle
     * @param port le port associé
     */
    public void setDefaultGateway(IP gate, int port)
    {
        this.gateways.set(0, gate);
        this.port_numbers.set(0, port);
    }


    /**
     * Affiche les routes dans l'Interface graphique
     */
    public void printRoutes()
    {
        System.out.println();
        System.out.println("Sous-réseau   Masque   NHR   Numéro d'interface (port)");
        for(int i=0 ; i<subnets.size() ; i++)
        {
            System.out.println(subnets.get(i)+"   "+masks.get(i)+"   "+gateways.get(i)+"   "+port_numbers.get(i));
        }
    }


    /**
     * Vide la table de routage hors route par défaut
     */
    public void clearRoutes()
    {
        int max = subnets.size();
        for(int i=1 ; i<max ; i++)
        {
            subnets.remove(subnets.size()-1);
            gateways.remove(gateways.size()-1);
            masks.remove(masks.size()-1);
            port_numbers.remove(port_numbers.size()-1);
        }
    }

    /**
     * Revoie les routes sous forme de string
     */
    public ArrayList<String> getRoutes()
    {
        ArrayList<String> res = new ArrayList<>();
        for(int i=0 ; i<subnets.size() ; i++)
        {
            res.add(subnets.get(i).toString()+"   "+masks.get(i).toString()+"   "+gateways.get(i).toString()+"   "+port_numbers.get(i).toString());
        }
        return res;
    }

    /**
     * Renvoie littéralement les routes
     */
    public ArrayList<ArrayList<Object>> getAllRoutes()
    {
        ArrayList<ArrayList<Object>> res = new ArrayList<>();
        for(int i=0 ; i<subnets.size() ; i++)
        {
            ArrayList<Object> temp = new ArrayList<>();
            temp.add(subnets.get(i));
            temp.add(masks.get(i));
            temp.add(gateways.get(i));
            temp.add(port_numbers.get(i));
            res.add(temp);
        }
        return res;
    }

    /**
     * REmplace les routes par le tableau fourni ; non destructif si erreur
     * @throws BadCallException si une des routes est mauvaise
     */
    public void setAllRoutes( ArrayList<ArrayList<Object>> routes, int numberOfPorts) throws BadCallException
    {
        ArrayList<IP> subnet = new ArrayList<>();
        ArrayList<IP> mask = new ArrayList<>();
        ArrayList<IP> gateway = new ArrayList<>();
        ArrayList<Integer> port_number = new ArrayList<>();

        for(ArrayList<Object> line : routes)
        {
            if(line.size() != 4 || !(line.get(0) instanceof IP) || !(line.get(1) instanceof IP)
                    || !(line.get(2) instanceof IP) || !(line.get(3) instanceof Integer))
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
            gateway.add((IP)line.get(2));
            port_number.add((Integer)line.get(3));
            if(port_number.get(port_number.size()-1) >= numberOfPorts)
                throw new BadCallException();
        }

        this.subnets = subnet;
        this.gateways = gateway;
        this.masks = mask;
        this.port_numbers = port_number;
    }

    /**
     * Revie la passerelle par défaut
     */
    public IP getDefaultGateway()
    {
        return this.gateways.get(0);
    }

    /**
     * Renvoie une copie de la table
     */
    public RoutingTable copy()
    {
        return new RoutingTable(this.subnets, this.masks, this.gateways, this.port_numbers, this.metrics);
    }

    /**
     * Configuration automatique des routes pour les sous-réseaux adjacents ; utilisé par RIP
     */
    public void autoConfigure(ArrayList<IP> IPinterfaces, ArrayList<IP> masksInterfaces)
    {
        for(int i=0 ; i<IPinterfaces.size() ; i++)
        {
            subnets.add(IPinterfaces.get(i).getSubnet(masksInterfaces.get(i)));
            masks.add(masksInterfaces.get(i));
            gateways.add(IPinterfaces.get(i));
            port_numbers.add(i);
            metrics.add(0);
        }
    }

    /**
     * Traite une table de routage reçue par RIP
     * @param received la table reçue
     */
    public synchronized void treatTable(RoutingTable received, IP gateway, int port)
    {
        for(int i=0 ; i<received.subnets.size() ; i++)
        {
            if(this.subnets.contains(received.subnets.get(i)) && this.masks.get(subnets.indexOf(received.subnets.get(i))).equals(received.masks.get(i)))
            {
                if(received.metrics.get(i)+1 < this.metrics.get(subnets.indexOf(received.subnets.get(i))))
                {
                    this.gateways.set(subnets.indexOf(received.subnets.get(i)), gateway);
                    this.metrics.set(subnets.indexOf(received.subnets.get(i)), received.metrics.get(i)+1);
                }
            }
            else //Si on ne l'a pas, on le rajoute
            {
                this.subnets.add(received.subnets.get(i));
                this.masks.add(received.masks.get(i));
                this.gateways.add(gateway);
                this.port_numbers.add(port);
                this.metrics.add(received.metrics.get(i)+1);
            }
        }
    }
}