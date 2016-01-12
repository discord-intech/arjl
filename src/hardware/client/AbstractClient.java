package hardware.client;

import enums.Bandwidth;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import hardware.router.AbstractRouter;
import link.Link;
import packet.IP;
import packet.Packet;

import java.util.ArrayList;

public abstract class AbstractClient extends AbstractRouter
{
    protected IP IP;
    protected int MAC;

    protected ArrayList<IP> waitingFrom = new ArrayList<>();

    /**
     * Constructeur à appeller avec super()
     *
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     * @param overflow       maximum de paquets supportables dans son tampon de traitement
     */
    public AbstractClient(LinkTypes port_type, Bandwidth port_bandwidth,
                          int overflow, int MAC,
                          IP IP, IP default_gateway, int default_port) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}}, new ArrayList<IP>(){{add(IP);}},
                default_gateway, default_port);
        this.IP = IP;
        this.MAC = MAC;

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
        if (p.isResponse && p.getType() == PacketTypes.WEB && waitingFrom.contains(p.src_addr)) //DEBUG !!!
        {
            System.out.println(this.toString() + " reçue WEB de " + p.src_addr);
            waitingFrom.remove(p.src_addr);
        }
    }

    public void launchRequest(PacketTypes type, IP destination)
    {
        if(type == PacketTypes.WEB)
        {
            futureStack.add(new Packet(destination, this.IP, this.MAC, -1, PacketTypes.WEB, false, true));
            waitingFrom.add(destination);
        }
    }

}