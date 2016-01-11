package hardware.hub;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import hardware.AbstractHardware;
import link.Link;
import packet.Packet;

import java.util.ArrayList;

public abstract class AbstractHub extends AbstractHardware
{
    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     */
    public AbstractHub(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth, int overflow) throws BadCallException {
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
    }

    @Override
    public void treat() throws BadCallException {

        ArrayList<Packet> newStack = new ArrayList<>(); //Permet de garder les paquets non envoyables
        int[] packetsSent = new int[ports.size()]; //Permet de compter les paquets pour simuler la bande passante
        for(Integer i : packetsSent) //On initialise la liste à 0
            i = 0;

        for(Packet p : stack)
        {
            for(int i=0 ; i<ports.size() ; i++)
            {
                if (i != p.lastPort)
                {
                    if (packetsSent[i] < ports.get(i).getBandwidth().value)
                    {
                        this.send(new Packet(p), i);
                        packetsSent[i]++;
                    }
                    else
                        newStack.add(p);
                }
            }
        }

        futureStack.addAll(0, newStack);
    }
}