package hardware.router;


import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import hardware.AbstractHardware;
import hardware.client.AbstractClient;
import hardware.server.AbstractServer;
import hardware.server.DHCPServer;
import packet.IP;
import packet.Packet;
import packet.data.DHCPData;
import table.ARPTable;
import table.RoutingTable;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les routeurs (serveurs et clients en héritant)
 * @author J. Desvignes
 */
public abstract class AbstractRouter extends AbstractHardware
{

    /** La table de routage */
    protected RoutingTable routingTable;
    /** La table ARP */
    protected final ARPTable arp = new ARPTable();
    /** Les paquets en attente de fin du protocole ARP */
    protected ArrayList<Packet> waitingForARP = new ArrayList<>();
    /** Les MACs des interfaces */
    protected ArrayList<Integer> MACinterfaces;
    /** Les IPs des interfaces */
    protected ArrayList<IP> IPinterfaces;
    /** Si ce routeur est un relai DHCP */
    protected boolean isDHCPRelay = false;
    /** L'addresse du serveur DHCP vers lequel relayer */
    protected IP DHCPaddress=null;

    /**
     * ======================================
     *    ATTRIBUTS POUR CLIENTS ET SERVEURS
     * ======================================
     */

    /** Identifiant DHCP */
    protected int[] DHCPidentifier;
    /** Le type de aquet géré (servers only) */
    protected PacketTypes type=PacketTypes.NULL;
    /** L'IP de la machine, c'est IPinterface.get(0) */
    protected IP IP;
    /** Si la machine attends une IP par DHCP  */
    protected boolean[] awaitingForIP;

    /**
     * Constructeur classique
     * @param port_types les types de port
     * @param port_bandwidth les bandes passantes
     * @param overflow la capacté de traitement
     * @param MACinterfaces les MACs des interfaces
     * @param IPinterfaces Les IPs des interfaces
     * @param default_gateway la passerelle par défaut
     * @param default_port le port de la passerelle par défaut
     */
    public AbstractRouter(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth,
                          int overflow, ArrayList<Integer> MACinterfaces,
                          ArrayList<IP> IPinterfaces, IP default_gateway, int default_port, ClockSpeed speed) throws BadCallException {
        super(port_types, port_bandwidth, overflow, speed);
        this.MACinterfaces = MACinterfaces;
        this.IPinterfaces = IPinterfaces;
        this.routingTable = new RoutingTable(default_port, default_gateway);
        this.awaitingForIP= new boolean[IPinterfaces.size()];
        this.DHCPidentifier = new int[IPinterfaces.size()];
    }

    /**
     * Constructeur pour clients et serveurs avec configuration DHCP
     * @param port_types les types de port
     * @param port_bandwidth les bandes passantes
     * @param overflow la capacté de traitement
     * @param MACinterfaces les MACs des interfaces
     * @param IPinterfaces Les IPs des interfaces
     */
    public AbstractRouter(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth,
                          int overflow, ArrayList<Integer> MACinterfaces,
                          ArrayList<IP> IPinterfaces, ClockSpeed speed) throws BadCallException {
        super(port_types, port_bandwidth, overflow, speed);
        this.MACinterfaces = MACinterfaces;
        this.IPinterfaces = IPinterfaces;
        this.routingTable = new RoutingTable();
        this.awaitingForIP= new boolean[IPinterfaces.size()];
        for(int i=0 ; i<awaitingForIP.length ; i++)
            awaitingForIP[i] = true;
        this.DHCPidentifier = new int[IPinterfaces.size()];
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
    public void treat() throws BadCallException, OverflowException
    {
        this.clientRequest();
        if(stack.isEmpty())
            return;
        ArrayList<Object> route; //Sert à stocker la route trouvée par le routage
        int mac; //MAC du NHR ou de la cible
        Packet p;
        synchronized (this.ports) {
            p = stack.get(0);
            stack.remove(0);
        }
        if(p.alreadyTreated) //Si c'était un paquet en attente de libération de la bande passante
        {
            p.alreadyTreated=false;
            this.send(p, p.destinationPort);
            return;
        }
        if(p.getType() == PacketTypes.DHCP) // Si on a un paquet DHCP
        {
            //===================================
            // Fonctions pour client (demandeur)
            //===================================
            if(this.IPinterfaces.get(p.lastPort) == null && p.isResponse && ((DHCPData)p.getData()).identifier == this.DHCPidentifier[p.lastPort]
                    && !((DHCPData)p.getData()).isOK()) //Traitement de la proposition de plage IP
            {
                ArrayList<IP> range = ((DHCPData) p.getData()).getRange();
                ((DHCPData) p.getData()).setChosen(range.get(0));
                p.dst_addr = ((DHCPData)p.getData()).getDHCPaddr();
                p.src_addr = new IP(0,0,0,0);
                p.dst_mac = p.src_mac;
                p.src_mac = this.MACinterfaces.get(0);
                this.send(p, p.lastPort);
                return;
            }
            else if(this.IPinterfaces.get(p.lastPort) == null && p.isResponse && ((DHCPData)p.getData()).identifier == this.DHCPidentifier[p.lastPort]
                    && ((DHCPData)p.getData()).isOK()) //Traitement de la validation du choix (DHCPOK)
            {
                this.IPinterfaces.set(p.lastPort, ((DHCPData) p.getData()).getChosen());
                if(p.lastPort == 0)
                    this.IP = ((DHCPData) p.getData()).getChosen();
                ((DHCPData) p.getData()).setACK();
                if(((DHCPData) p.getData()).getSubnetInfo().get(0) != null)
                    this.routingTable.setDefaultGateway(((DHCPData) p.getData()).getSubnetInfo().get(0), p.lastPort);
                p.dst_addr = ((DHCPData)p.getData()).getDHCPaddr();
                p.src_addr = IPinterfaces.get(0);
                p.dst_mac = p.src_mac;
                p.src_mac = this.MACinterfaces.get(0);
                awaitingForIP[p.lastPort]=false;
                this.send(p, p.lastPort);
                return;
            }
            //===================================
            // Fonctions pour relai DHCP
            //===================================
            else if(this.isDHCPRelay && !(this instanceof DHCPServer)) // On vérifie qu'on est pas un serveur DHCP
            {
                if(((DHCPData)p.getData()).getSubnetInfo().get(1) == null)  // Si le paquet est nu, je me met en premier relai
                {
                    ((DHCPData)p.getData()).setSubnetInfo(this.IPinterfaces.get(p.lastPort),
                            routingTable.getRelatedMask((IP)routingTable.routeMe(this.IPinterfaces.get(p.lastPort)).get(1)));
                    if(p.dst_addr.equals(new IP(255,255,255,255)))
                        p.dst_addr = DHCPaddress;
                    ((DHCPData) p.getData()).setAskerMAC(p.src_mac);
                    ((DHCPData) p.getData()).setDHCPaddr(this.DHCPaddress);
                    p.dst_mac = this.MACinterfaces.get(p.lastPort);
                    //Et on continue vers le routage...
                }
                else if(IPinterfaces.contains(p.dst_addr)) // Si je suis le premier relai, je broadcast pour atteindre le client
                {
                    p.dst_mac = ((DHCPData) p.getData()).getAskerMAC();
                    p.src_mac = MACinterfaces.get(IPinterfaces.indexOf(p.dst_addr));
                    p.src_addr = p.dst_addr;
                    p.setNHR(new IP(0,0,0,0));
                    p.dst_addr = new IP(255,255,255,255);
                    send(p, 0);
                }

            }
        }

        if(awaitingForIP[p.lastPort]) //Si je ne suis pas configuré en IP (attente DHCP), je quitte
            return;

        if(p.getType() == PacketTypes.ARP) //S'il s'agit d'un paquet lié à l'ARP
        {
            if(p.isResponse) //Si c'est une réponse ("IP is at MAC")
            {
                for(int i=0 ; i<waitingForARP.size() ; i++)
                {
                    if(p.src_addr.equals(waitingForARP.get(i).getNHR()))
                    {
                        arp.addRule(p.src_addr, p.src_mac);
                        synchronized (this.ports) {
                            stack.add(waitingForARP.get(i));
                        }
                        waitingForARP.remove(i);
                        i--;
                    }
                }
            }
            else // Si c'est une demande ("who is IP?")
            {
                for(IP i : IPinterfaces)
                {
                    if(i.equals(p.dst_addr))
                    {
                        this.send(new Packet(p.src_addr,
                                i, MACinterfaces.get(IPinterfaces.indexOf(i)), p.src_mac, PacketTypes.ARP, true, false), p.lastPort);
                        break;
                    }
                }
            }
            return;
        }

        if(this instanceof DHCPServer && p.getType() == PacketTypes.DHCP) //Si on est un serveur DHCP, on le traite
        {
            this.treatData(p);
            return;
        }

        if(!MACinterfaces.contains(p.dst_mac) && !MACinterfaces.contains(p.src_mac) ) //S'il ne m'est pas destiné, je l'ignore
            return;

        if(routingTable.isBroadcast(p) && !(this instanceof DHCPServer)) //Si c'est un broadcast, je l'ignore
            return;

        if(IPinterfaces.contains(p.dst_addr)) //S'il m'est destiné (en bout de chaîne)
        {
            this.treatData(p);
            return;
        }

        route = routingTable.routeMe(p.dst_addr); //route={port de redirection ; IP du NHR}
        IP ip; // IP du NHR
        if(route.get(1).equals(new IP(0,0,0,0)) || IPinterfaces.contains(route.get(1))) //Si le gateway vaut 0.0.0.0 c'est qu'on est arrivé
        {
            mac = arp.findARP(p.dst_addr);
            ip = p.dst_addr;
        }
        else
        {
            mac = arp.findARP((IP) route.get(1));
            ip = (IP)route.get(1);
        }

        p.setNHR(ip);
        if(mac == -1) //Si la MAC de la cible/NHR n'est pas connue, on lance le protocole ARP
        {
            waitingForARP.add(p);
            if(route.get(1).equals(new IP(0,0,0,0)) || IPinterfaces.contains(route.get(1))) //Si le gateway vaut 0.0.0.0 c'est qu'on est arrivé
                ip = p.dst_addr;
            else
                ip = (IP)route.get(1);
            this.send(new Packet(ip,
                    IPinterfaces.get((int)route.get(0)),
                    MACinterfaces.get((int)route.get(0)), -1, PacketTypes.ARP, false, false), (int)route.get(0));
            return;
        }

        p.dst_mac = mac;
        p.src_mac = MACinterfaces.get((int)route.get(0));
        this.send(p, (int)route.get(0));

    }

    /**
     * Traitement du paquet à haut-niveau (couche application)
     * @param p le paquet
     */
    protected void treatData(Packet p) throws BadCallException, OverflowException {

    }

    /**
     * Ajoute une règle de routage
     */
    public void addRoutingRule(int port_number, IP subnet, IP mask, IP gateway) throws BadCallException
    {
        if(port_number >= ports.size() || subnet == null || mask == null || gateway == null)
            throw new BadCallException();
        routingTable.addRule(port_number, subnet, mask, gateway);
    }

    /**
     * Configure le routeur comme relai DHCP
     * @param DHCPaddress l'adresse du serveur DHCP
     */
    public void setDHCPRelay(IP DHCPaddress)
    {
        this.isDHCPRelay = true;
        this.DHCPaddress = DHCPaddress;
    }

    /**
     * Coupe le service de relai DHCP
     */
    public void stopDHCPRelay()
    {
        this.isDHCPRelay = false;
        this.DHCPaddress = null;
    }

    /**
     * Lance le propocole DHCP pour trouver une IP (clients et servers only)
     */
    public void DHCPClient(int port) throws BadCallException, OverflowException {
        Packet p =new Packet(new IP(255,255,255,255), new IP(0,0,0,0), this.MACinterfaces.get(0), -1, PacketTypes.DHCP, false, true);
        this.DHCPidentifier[port] = ((DHCPData)p.getData()).identifier;
        this.send(p, port);
    }

    /**
     * Permet de configurer l'IP d'une interface
     * @param port le numéro d'interface
     * @param ip L'IP à mettre
     */
    public void configureIP(int port, IP ip)
    {
        this.IPinterfaces.set(port, ip);
        awaitingForIP[port]=false;
        if(port==0)
        {
            this.IP = ip;
        }
    }

    /**
     * Permet de configurer la route par défaut
     * @param port l'interface par défaut
     * @param ip l'IP du NHR par défaut
     */
    public void configureDefaultRoute(int port, IP ip)
    {
        this.routingTable.setDefaultGateway(ip, port);
    }

    /**
     * Configure la passerelle par défaut, utilisé seulement par les serveurs et clients
     * équivalent à faire configureDefaultRoute(0, ip)
     * @param ip l'IP de la passerelle
     */
    public void configureGateway(IP ip)
    {
        this.routingTable.setDefaultGateway(ip, 0);
    }

    public IP getGateway()
    {
        return this.routingTable.getDefaultGateway();
    }

    @Override
    protected void printMore(int port)
    {
        if(awaitingForIP[port])
            System.out.println("    Pas de configuration IP en place ; veuillez lancer la requête DHCP ou configurer de manière statique");
        else
            System.out.println("    IP : "+IPinterfaces.get(port).toString()+"   MAC : "+MACinterfaces.get(port).toString());
        if(!(this instanceof AbstractServer || this instanceof AbstractClient))
        {
            if(isDHCPRelay)
                System.out.println("    Mode Relai DHCP : oui"+this.DHCPaddress+"");
        }
    }


    /**
     * Imprime les routes pour le verbose/l'interface textuelle
     */
    public void printRoutes()
    {
        this.routingTable.printRoutes();
    }

    /**
     * Donne les routes
     * Sous-réseau   Masque   NHR   Numéro d'interface (port)
     */
    public ArrayList<String> getRoutes()
    {
        return this.routingTable.getRoutes();
    }

    /**
     * Vide la table de routage, hors route par défaut
     */
    public void clearRoutes()
    {
        this.routingTable.clearRoutes();
    }


    /**
     * Donne la table de routage
     */
    public ArrayList<ArrayList<Object>> getAllRoutes () {return this.routingTable.getAllRoutes();}

    /**
     *  Modifie la table
     */
    public void setAllRoutes( ArrayList<ArrayList<Object>> routes) throws BadCallException
    {
        this.routingTable.setAllRoutes(routes, ports.size());
    }

    /**
     * VOIR DOC ABSTRACTCLIENT
     */
    protected void clientRequest(){}

    public IP getInterfaceIP(int port) throws BadCallException {
        if(this.ports.size() <= port || port < 0)
            throw new BadCallException();

        if(this.IPinterfaces.get(port) == null)
            return new IP(0,0,0,0);

        return this.IPinterfaces.get(port);
    }

    public int getInterfaceMAC(int port) throws BadCallException
    {
        if(this.ports.size() <= port || port < 0)
            throw new BadCallException();
        return this.MACinterfaces.get(port);
    }

    public Boolean isDHCPRelay() {
        return isDHCPRelay;
    }

    public IP getDHCPaddress() {
        return DHCPaddress;
    }
}