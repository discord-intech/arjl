package hardware.router;


import enums.Bandwidth;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import hardware.ARPTable;
import hardware.AbstractHardware;
import link.Link;
import packet.IP;
import packet.Packet;

import java.util.ArrayList;

public abstract class AbstractRouter extends AbstractHardware
{

    protected RoutingTable routingTable = new RoutingTable();
    protected ARPTable arp = new ARPTable();

    protected ArrayList<Packet> waitingForARP = new ArrayList<Packet>();

    protected ArrayList<Integer> MACinterfaces;
    protected ArrayList<IP> IPinterfaces;
    protected ArrayList<IP> MASKinterfaces;

    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     */
    public AbstractRouter(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth,
                          int overflow, ArrayList<Integer> MACinterfaces, ArrayList<IP> IPinterfaces, ArrayList<IP> masks) throws BadCallException {
        super(port_types, port_bandwidth, overflow);
        this.MACinterfaces = MACinterfaces;
        this.IPinterfaces = IPinterfaces;
        this.MASKinterfaces = masks;
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
        Link link = ports.get(port);
        link.getOtherHardware(this).receive(packet, ports.get(port).getOtherHardware(this).whichPort(link));
        if(packet.tracked)
            System.out.println(this.toString()+" : sent "+packet.getType()+" to "+packet.dst_addr+" with NHR= "+packet.getNHR() );
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

            if(p.getType() == PacketTypes.ARP)
            {
                if(p.isResponse)
                {
                    for(Packet i : waitingForARP)
                    {
                        if(p.src_addr == i.getNHR())
                        {
                            arp.addRule(p.src_addr, p.src_mac);
                            futureStack.add(i);
                            waitingForARP.remove(i);
                        }
                    }
                }
                else
                {
                    for(IP i : IPinterfaces)
                    {
                        if(i == p.dst_addr)
                        {
                            this.send(new Packet(p.src_addr, p.src_mask,
                                    i, MASKinterfaces.get(IPinterfaces.indexOf(i)),
                                    MACinterfaces.get(IPinterfaces.indexOf(i)), p.src_mac, PacketTypes.ARP, true), p.lastPort);
                            break;
                        }
                    }
                }
                continue;
            }

            if(IPinterfaces.contains(p.dst_addr))
                continue;

            route = routingTable.routeMe(p.dst_addr); //route={port de redirection ; IP du NHR}
            mac = arp.findARP((IP)route.get(1));
            p.setNHR((IP)route.get(1));
            if(mac == -1)
            {
                waitingForARP.add(p);
                this.send(new Packet((IP)route.get(1), p.dst_mask,
                        IPinterfaces.get((int)route.get(0)), MASKinterfaces.get((int)route.get(0)),
                        MACinterfaces.get((int)route.get(0)), -1, PacketTypes.ARP, false), (int)route.get(0));
                continue;
            }

            p.dst_mac = mac;
            p.src_mac = MACinterfaces.get((int)route.get(0));
            this.send(p, (int)route.get(0));

        }
        futureStack.addAll(0, newStack);
    }
}