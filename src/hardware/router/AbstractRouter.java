package hardware.router;


import enums.Bandwidth;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import hardware.AbstractHardware;
import packet.IP;
import packet.Packet;
import packet.data.DHCPData;
import table.ARPTable;
import table.RoutingTable;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les routeurs (serveurs et clients en héritant)
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
    protected IP DHCPaddress;

    /**
     * ======================================
     *    ATTRIBUTS POUR CLIENTS ET SERVEURS
     * ======================================
     */

    /** Identifiant DHCP */
    protected int DHCPidentifier;
    /** Le type de aquet géré (servers only) */
    protected PacketTypes type=PacketTypes.NULL;
    /** L'IP de la machine, c'est IPinterface.get(0) */
    protected IP IP;
    /** Si la machine attends une IP par DHCP  */
    protected boolean awaitingForIP = false;

    /**
     * Constructeur classique
     * @param port_types les types de port
     * @param port_bandwidth les banddes passantes
     * @param overflow la capacté de traitement
     * @param MACinterfaces les MACs des interfaces
     * @param IPinterfaces Les IPs des interfaces
     * @param default_gateway la passerelle par défaut
     * @param default_port le port de la passerelle par défaut
     */
    public AbstractRouter(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth,
                          int overflow, ArrayList<Integer> MACinterfaces,
                          ArrayList<IP> IPinterfaces, IP default_gateway, int default_port) throws BadCallException {
        super(port_types, port_bandwidth, overflow);
        this.MACinterfaces = MACinterfaces;
        this.IPinterfaces = IPinterfaces;
        this.routingTable = new RoutingTable(default_port, default_gateway);
    }

    /**
     * Constructeur pour clients et serveurs avec configuration DHCP
     * @param port_types les types de port
     * @param port_bandwidth les banddes passantes
     * @param overflow la capacté de traitement
     * @param MACinterfaces les MACs des interfaces
     * @param IPinterfaces Les IPs des interfaces
     */
    public AbstractRouter(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth,
                          int overflow, ArrayList<Integer> MACinterfaces,
                          ArrayList<IP> IPinterfaces) throws BadCallException {
        super(port_types, port_bandwidth, overflow);
        this.MACinterfaces = MACinterfaces;
        this.IPinterfaces = IPinterfaces;
        this.routingTable = new RoutingTable();
    }



    @Override
    public void receive(Packet packet, int port)
    {
        packet.lastPort = port;
        this.futureStack.add(packet);
    }

    @Override
    public void treat() throws BadCallException {

        ArrayList<Object> route; //Sert à stocker la route trouvée par le routage
        int mac; //MAC du NHR ou de la cible
        for(Packet p : stack) //On parcours le tampon
        {
            if(p.alreadyTreated) //Si c'était un paquet en attente de libération de la bande passante
            {
                p.alreadyTreated=false;
                this.send(p, p.destinationPort);
                continue;
            }
            if(p.getType() == PacketTypes.DHCP) // Si on a un paquet DHCP
            {
                //===================================
                // Fonctions pour client (demandeur)
                //===================================
                if(this.IPinterfaces.isEmpty() && p.isResponse && ((DHCPData)p.getData()).identifier == this.DHCPidentifier
                        && !((DHCPData)p.getData()).isOK()) //Traitement de la proposition de plage IP
                {
                    ArrayList<IP> range = ((DHCPData) p.getData()).getRange();
                    ((DHCPData) p.getData()).setChosen(range.get(0));
                    p.dst_addr = ((DHCPData)p.getData()).getDHCPaddr();
                    p.src_addr = new IP(0,0,0,0);
                    p.dst_mac = p.src_mac;
                    p.src_mac = this.MACinterfaces.get(0);
                    this.send(p, p.lastPort);
                    continue;
                }
                else if(this.IPinterfaces.isEmpty() && p.isResponse && ((DHCPData)p.getData()).identifier == this.DHCPidentifier
                        && ((DHCPData)p.getData()).isOK()) //Traitement de la validation du choix (DHCPOK)
                {
                    this.IPinterfaces.add(((DHCPData) p.getData()).getChosen());
                    this.IP = ((DHCPData) p.getData()).getChosen();
                    ((DHCPData) p.getData()).setACK();
                    this.routingTable.setDefaultGateway(((DHCPData) p.getData()).getSubnetInfo().get(0));
                    p.dst_addr = ((DHCPData)p.getData()).getDHCPaddr();
                    p.src_addr = IPinterfaces.get(0);
                    p.dst_mac = p.src_mac;
                    p.src_mac = this.MACinterfaces.get(0);
                    this.send(p, p.lastPort);
                    awaitingForIP=false;
                    continue;
                }
                //===================================
                // Fonctions pour relai DHCP
                //===================================
                else if(this.isDHCPRelay && this.type != PacketTypes.DHCP) // On vérifie qu'on est pas un serveur DHCP
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

            if(awaitingForIP) //Si je ne suis pas configuré en IP (attente DHCP), je quitte
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
                            futureStack.add(waitingForARP.get(i));
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
                continue;
            }

            if(!MACinterfaces.contains(p.dst_mac) && !MACinterfaces.contains(p.src_mac) ) //S'il ne m'est pas destiné, je l'ignore
                continue;

            if(routingTable.isBroadcast(p)) //Si c'est un broadcast, je l'ignore
                continue;

            if(IPinterfaces.contains(p.dst_addr)) //S'il m'est destiné (en bout de chaîne)
            {
                this.treatData(p);
                continue;
            }

            route = routingTable.routeMe(p.dst_addr); //route={port de redirection ; IP du NHR}
            IP ip; // IP du NHR
            if(IPinterfaces.contains(route.get(1))) //Si le gateway vaut 0.0.0.0 c'est qu'on est arrivé
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
                if(IPinterfaces.contains(route.get(1))) //Si le gateway vaut 0.0.0.0 c'est qu'on est arrivé
                    ip = p.dst_addr;
                else
                    ip = (IP)route.get(1);
                this.send(new Packet(ip,
                        IPinterfaces.get((int)route.get(0)),
                        MACinterfaces.get((int)route.get(0)), -1, PacketTypes.ARP, false, false), (int)route.get(0));
                continue;
            }

            p.dst_mac = mac;
            p.src_mac = MACinterfaces.get((int)route.get(0));
            this.send(p, (int)route.get(0));

        }
    }

    /**
     * Traitement du paquet à haut-niveau (couche application)
     * @param p le paquet
     */
    protected void treatData(Packet p) throws BadCallException {

    }

    /**
     * Ajoute une règle de routage
     */
    public void addRoutingRule(int port_number, IP subnet, IP mask, IP gateway, int metric)
    {
        routingTable.addRule(port_number, subnet, mask, gateway, metric);
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
     * Lance le propocole DHCP pour trouver une IP (clients et servers only)
     */
    public void DHCPClient() throws BadCallException {
        this.awaitingForIP=true;
        Packet p =new Packet(new IP(255,255,255,255), new IP(0,0,0,0), this.MACinterfaces.get(0), -1, PacketTypes.DHCP, false, true);
        this.DHCPidentifier = ((DHCPData)p.getData()).identifier;
        this.send(p, 0);
    }
}