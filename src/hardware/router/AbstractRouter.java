package hardware.router;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import hardware.AbstractHardware;
import hardware.switchs.SwitchingTable;
import link.Link;
import packet.Packet;

import java.util.ArrayList;

public abstract class AbstractRouter extends AbstractHardware
{

    protected RoutingTable routingTable = new RoutingTable();
    protected SwitchingTable switchingTable = new SwitchingTable();

    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     */
    public AbstractRouter(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth, int overflow)
    {
        super(port_types, port_bandwidth, overflow);
    }

    /**
     * Vérifie s'il connaît que l'appareil est dans sa base de données
     * @param mac la mac reçue
     * @param port le port
     * @return true s'il a ajouté une règle ; false sinon
     */
    private boolean tryToLearn(int mac, int port)
    {
        if(switchingTable.commute(mac) == -1)
        {
            switchingTable.addRule(mac, port);
            return true;
        }
        return false;
    }

    @Override
    public void receive(Packet packet, int port)
    {
        packet.lastPort = port;
        this.tryToLearn(packet.src_mac, port);
        this.futureStack.add(packet);
    }

    @Override
    public void send(Packet packet, int port) throws BadCallException
    {
        Link link = ports.get(port);
        link.getOtherHardware(this).receive(packet, ports.get(port).getOtherHardware(this).whichPort(link));
    }
}