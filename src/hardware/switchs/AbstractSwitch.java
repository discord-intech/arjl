package hardware.switchs;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import hardware.AbstractHardware;
import link.Link;
import packet.Packet;

import java.util.ArrayList;

public abstract class AbstractSwitch extends AbstractHardware
{

    protected SwitchingTable switchingTable = new SwitchingTable();

    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     */
    public AbstractSwitch(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth, int overflow) throws BadCallException {
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
        if(packet.tracked)
            System.out.println(this.toString()+" : sent "+packet.getType()+" to "+packet.dst_addr+" with NHR= "+packet.getNHR() );
    }

    @Override
    public void treat() throws BadCallException
    {
        ArrayList<Packet> newStack = new ArrayList<>(); //Permet de garder les paquets non envoyables
        int[] packetsSent = new int[ports.size()]; //Permet de compter les paquets pour simuler la bande passante
        for(Integer i : packetsSent) //On initialise la liste à 0
            i = 0;

        int port;

        for(Packet p : stack)
        {
            if((port = switchingTable.commute(p.dst_mac)) != -1) // S'il connait la destination
            {
                if(packetsSent[port] < ports.get(port).getBandwidth().value)
                {
                    this.send(new Packet(p), port);
                    packetsSent[port]++;
                }
                else
                    newStack.add(p);
            }
            else //S'il ne la connaît pas
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
        }

        futureStack.addAll(0, newStack);
    }

}

