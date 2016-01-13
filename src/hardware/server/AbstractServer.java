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

public abstract class AbstractServer extends AbstractRouter
{

    protected PacketTypes type;
    protected IP IP;
    protected int MAC;

    /**
     * Constructeur à appeller
     *
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     * @param overflow       maximum de paquets supportables dans son tampon de traitement
     */
    public AbstractServer(LinkTypes port_type, Bandwidth port_bandwidth,
                          int overflow, int MAC, IP default_gateway, int default_port, PacketTypes type) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}}, new ArrayList<IP>(),
                default_gateway, default_port);
        this.type=type;
        this.MAC = MAC;
        this.DHCPClient();
    }

    /**
     * Constructeur à appeller
     *
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     * @param overflow       maximum de paquets supportables dans son tampon de traitement
     */
    public AbstractServer(LinkTypes port_type, Bandwidth port_bandwidth,
                          int overflow, int MAC,
                          IP IP, IP default_gateway, int default_port, PacketTypes type) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}}, new ArrayList<IP>(){{add(IP);}},
                default_gateway, default_port);
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
            //System.out.println(this.IP + " : reçu WEB de " + p.src_addr);
            for(int i= 0; i<PacketTypes.WEB.size ; i++)
                this.send(new Packet(p.src_addr,
                    IPinterfaces.get(p.lastPort),
                    MACinterfaces.get(p.lastPort), p.src_mac, PacketTypes.WEB, true, false), p.lastPort);
        }
    }

    private void DHCPClient()
    {
        System.err.println(this+" : DHCP PAS IMPLEMENTE !! ADRESSAGE STATIQUE OBLIGATOIRE");
        //TODO ~ DO THE DHCP ~ (WUB WUB WUB)
    }
}