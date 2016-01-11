package hardware.server;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import hardware.AbstractHardware;
import link.Link;
import packet.Packet;

import java.util.ArrayList;

public abstract class AbstractServer extends AbstractHardware
{

    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     * @param overflow       maximum de paquets supportables dans son tampon de traitement
     */
    public AbstractServer(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth, int overflow) throws BadCallException {
        super(port_types, port_bandwidth, overflow);
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
            System.out.println(this.toString()+" : sent "+packet.getType()+" to "+packet.dst_addr+" with NHR="+packet.getNHR()+" isResponse="+packet.isResponse);
    }

    @Override
    public void treat() throws BadCallException {

    }

    public void addPacket(Packet packet)
    {
        this.futureStack.add(packet);
    }
}