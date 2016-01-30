package hardware.server;


import enums.Bandwidth;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import hardware.router.AbstractRouter;
import hardware.Link;
import packet.IP;
import packet.Packet;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les serveurs
 */
public abstract class AbstractServer extends AbstractRouter
{

    /**
     * La MAC du serveur
     */
    protected int MAC;

    /**
     * Constructeur pour adressage par DHCP
     * @param port_type le type de connectique
     * @param port_bandwidth la bande passante
     * @param overflow la capacité de traitement
     * @param MAC la MAC de l'appareil
     * @param type le type de paquets, donc le type de service rendu
     */
    public AbstractServer(LinkTypes port_type, Bandwidth port_bandwidth,
                          int overflow, int MAC, PacketTypes type) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}}, new ArrayList<IP>());
        this.type=type;
        this.MAC = MAC;
    }

    /**
     * Constructeur pour adressage statique
     * @param port_type le type de connectique
     * @param port_bandwidth la bande passante
     * @param overflow la capacité de traitement
     * @param MAC la MAC de l'appareil
     * @param IP l'IP de serveur
     * @param default_gateway la passerelle par défaut
     * @param type le type de paquets, donc le type de service rendu
     */
    public AbstractServer(LinkTypes port_type, Bandwidth port_bandwidth,
                          int overflow, int MAC,
                          IP IP, IP default_gateway, PacketTypes type) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}}, new ArrayList<IP>(){{add(IP);}},
                default_gateway, 0);
        this.IP = IP;
        this.MAC = MAC;
        this.type=type;
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
            System.out.println(this.toString()+" : sent "+packet.getType()+" to "+packet.dst_addr+" with NHR="+packet.getNHR()+" isResponse="+packet.isResponse);
    }

    @Override
    protected void treatData(Packet p) throws BadCallException {
        if ((this.type == PacketTypes.WEB) && (p.getType() == PacketTypes.WEB)) //DEBUG !!!
        {
            System.out.println(this.IP + " : reçu WEB de " + p.src_addr);
            for(int i= 0; i<PacketTypes.WEB.size ; i++)
                this.send(new Packet(p.src_addr,
                    IPinterfaces.get(p.lastPort),
                    MACinterfaces.get(p.lastPort), p.src_mac, PacketTypes.WEB, true, false), p.lastPort);
        }
    }
}