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
    private ArrayList<IP> subnets = new ArrayList<>();
    /** les masques de sous-réseau */
    private ArrayList<IP> masks = new ArrayList<>();
    /** les passerelles (ou NHR) */
    private ArrayList<IP> gateways = new ArrayList<>();
    /** les numéros de port associés */
    private ArrayList<Integer> port_numbers = new ArrayList<>();

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

    public IP getDefaultGateway()
    {
        return this.gateways.get(0);
    }
}