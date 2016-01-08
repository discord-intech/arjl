package hardware.router;

import packet.IP;

import java.util.ArrayList;

/**
 * Classe définissant les tables de routages, fonctionne selon la règle du plus petit masque
 */
public class RoutingTable
{

    private ArrayList<IP> subnets = new ArrayList<>();
    private ArrayList<IP> masks = new ArrayList<>();
    private ArrayList<Integer> gateways = new ArrayList<>(); // Ce sont des MACs !
    private ArrayList<Integer> port_numbers = new ArrayList<>();
    private ArrayList<Integer> metric = new ArrayList<>();

    /**
     * Constructeur de la table
     * @param default_route_port le port du routeur pour l'adresse par défaut
     * @param default_gateway la passerelle par défaut (généralement ce qui dirige vers le réseau opérateur)
     */
    public RoutingTable(int default_route_port, int default_gateway)
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
        gateways.add(0);
        metric.add(255);
    }

    /**
     * Route le paquet
     * @param address l'adresse du paquet à router
     * @return une liste { le port de destination ; la mac du NHR }
     */
    public ArrayList<Integer> routeMe(IP address)
    {
        ArrayList<Integer> res = new ArrayList<>();
        ArrayList<Integer> pot_res = new ArrayList<>();

        for(int i=0 ; i < masks.size() ; i++)
        {
            if(subnets.get(i).equals(address.getSubnet(masks.get(i))))
            {
                pot_res.add(i);
            }
        }

        int j=0;

        for(int i : pot_res)
        {
            if(metric.get(i) < metric.get(j))
                j=i;
        }

        res.add(port_numbers.get(j));
        res.add(gateways.get(j));

        return res;
    }
}