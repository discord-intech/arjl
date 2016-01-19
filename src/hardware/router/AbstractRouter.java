package hardware.router;


import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import enums.Bandwidth;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import packet.data.DHCPData;
import table.ARPTable;
import hardware.AbstractHardware;
import hardware.Link;
import packet.IP;
import packet.Packet;
import table.RoutingTable;

import java.util.ArrayList;

public abstract class AbstractRouter extends AbstractHardware
{

    protected RoutingTable routingTable;
    protected ARPTable arp = new ARPTable();

    protected ArrayList<Packet> waitingForARP = new ArrayList<Packet>();

    protected ArrayList<Integer> MACinterfaces;
    protected ArrayList<IP> IPinterfaces;

    protected int DHCPidentifier;
    protected boolean isDHCPRelay = false;
    protected IP DHCPaddress;

    protected PacketTypes type=PacketTypes.NULL;
    protected IP IP;

    protected boolean awaitingForIP = false;

    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
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
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
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
    public void send(Packet packet, int port) throws BadCallException
    {
        if(packet.TTLdown())
            return;
        Link link = ports.get(port);
        link.getOtherHardware(this).receive(packet, ports.get(port).getOtherHardware(this).whichPort(link));
        if(packet.tracked)
            System.out.println(this.IPinterfaces.get(port).toString()+" : sent "+packet.getType()+" to "+packet.dst_addr+" with NHR="+packet.getNHR()+" isResponse="+packet.isResponse);
    }

    @Override
    public void treat() throws BadCallException {
        ArrayList<Packet> newStack = new ArrayList<>(); //Permet de garder les paquets non envoyables
        int[] packetsSent = new int[ports.size()]; //Permet de compter les paquets pour simuler la bande passante
        for(Integer i : packetsSent) //On initialise la liste à 0
            i = 0;

        ArrayList<Object> route;
        int mac;
        for(Packet p : stack)
        {

            if(p.getType() == PacketTypes.DHCP)
            {
                if(this.IPinterfaces.isEmpty() && p.isResponse && ((DHCPData)p.getData()).identifier == this.DHCPidentifier
                        && !((DHCPData)p.getData()).isOK())
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
                        && ((DHCPData)p.getData()).isOK())
                {
                    this.IPinterfaces.add(((DHCPData) p.getData()).getChosen());
                    this.IP = ((DHCPData) p.getData()).getChosen();
                    ((DHCPData) p.getData()).setACK();
                    this.routingTable.addRule(0, this.IPinterfaces.get(0).getSubnet(((DHCPData) p.getData()).getSubnetInfo().get(1)),
                            ((DHCPData) p.getData()).getSubnetInfo().get(1), ((DHCPData) p.getData()).getSubnetInfo().get(0), 255);
                    p.dst_addr = ((DHCPData)p.getData()).getDHCPaddr();
                    p.src_addr = IPinterfaces.get(0);
                    p.dst_mac = p.src_mac;
                    p.src_mac = this.MACinterfaces.get(0);
                    this.send(p, p.lastPort);
                    awaitingForIP=false;
                    continue;
                }
                else if(this.isDHCPRelay && this.type != PacketTypes.DHCP)
                {
                    if(((DHCPData)p.getData()).getSubnetInfo().get(1) == null)
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
                    else if(IPinterfaces.contains(p.dst_addr))
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

            if(p.getType() == PacketTypes.ARP)
            {
                if(p.isResponse)
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
                else
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
            IP ip;
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
            if(mac == -1)
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
        if(!newStack.isEmpty())
            futureStack.addAll(0, newStack);
    }

    protected void treatData(Packet p) throws BadCallException {

    }

    public void addRoutingRule(int port_number, IP subnet, IP mask, IP gateway, int metric)
    {
        routingTable.addRule(port_number, subnet, mask, gateway, metric);
    }

    public void setDHCPRelay(IP DHCPaddress)
    {
        this.isDHCPRelay = true;
        this.DHCPaddress = DHCPaddress;
    }

    public void DHCPClient() throws BadCallException {
        this.awaitingForIP=true;
        Packet p =new Packet(new IP(255,255,255,255), new IP(0,0,0,0), this.MACinterfaces.get(0), -1, PacketTypes.DHCP, false, true);
        this.DHCPidentifier = ((DHCPData)p.getData()).identifier;
        this.send(p, 0);
    }
}