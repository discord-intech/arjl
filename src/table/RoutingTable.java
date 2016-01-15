package table;

import packet.IP;
import packet.Packet;

import java.util.ArrayList;

/**
 * Classe définissant les tables de routages, fonctionne selon la règle du plus petit masque
 */
public class RoutingTable
{

    private ArrayList<IP> subnets = new ArrayList<>();
    private ArrayList<IP> masks = new ArrayList<>();
    private ArrayList<IP> gateways = new ArrayList<>(); // Ce sont des MACs !
    private ArrayList<Integer> port_numbers = new ArrayList<>();
    private ArrayList<Integer> metric = new ArrayList<>();

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
        metric.add(255);
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
        metric.add(255);
    }

    public synchronized void addRule(int port_number, IP subnet, IP mask, IP gateway, int metric)
    {
        port_numbers.add(port_number);
        subnets.add(subnet);
        masks.add(mask);
        gateways.add(gateway);
        this.metric.add(metric);
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

        int j=0; // Cette variable représente le numéro d'entrée actuellement sélectionnée

        //On prends le résultat avec la plus petite métrique
        for(int i : pot_res)
        {
            if(metric.get(i) < metric.get(j))
                j=i;
        }

        ArrayList<Integer> more_pot_res = new ArrayList<>();

        //On vérifie que l'on a pas plusieurs résultat avec la même métrique
        for(int i=0 ; i < metric.size() ; i++)
        {
            if(pot_res.contains(i) && (metric.get(i) == metric.get(j)) && (i != j))
                more_pot_res.add(i);
        }

        //Si c'est le cas, on prends celle avec le plus petit masque
        if(!more_pot_res.isEmpty())
        {
            for(int index : more_pot_res)
                if(!masks.get(j).isSmallerThan(masks.get(index)))
                    j=index;
        }

        res.add(port_numbers.get(j));
        res.add(gateways.get(j));

        return res;
    }

    public boolean isBroadcast(Packet p)
    {
        ArrayList<Object> res = this.routeMe(p.dst_addr);

        if(res.get(1).equals(new IP(0,0,0,0)))
            return false;

        IP mask = masks.get(gateways.indexOf((IP)res.get(1)));
        if(p.dst_addr.isBroadcast(mask))
            return true;
        return false;

    }

    public IP getRelatedMask(IP subnet)
    {
        for(IP i : gateways)
        {
            if(i.equals(subnet))
                return masks.get(gateways.indexOf(i));
        }
        return null;
    }
}