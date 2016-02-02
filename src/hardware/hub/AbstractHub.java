package hardware.hub;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import hardware.AbstractHardware;
import packet.Packet;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les hubs
 */
public abstract class AbstractHub extends AbstractHardware
{
    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     * @param overflow limite en capacité de traitement (simule la congestion sur un HUB)
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
    public void treat() throws BadCallException {

        for(Packet p : stack)
        {
            if(p.alreadyTreated) //Si c'était un paquet en attente de libération de la bande passante
            {
                p.alreadyTreated=false;
                this.send(p, p.destinationPort);
                continue;
            }
            for(int i=0 ; i<ports.size() ; i++)
            {
                if (i != p.lastPort)
                {
                    if (ports.get(i) != null)
                    {
                        this.send(new Packet(p), i);
                    }
                }
            }
        }
    }
}