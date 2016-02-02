package hardware.switchs;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import hardware.AbstractHardware;
import packet.Packet;
import table.SwitchingTable;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les switchs à apprentissage
 */
public abstract class AbstractSwitch extends AbstractHardware
{

    /**
     * Table de commutation
     */
    protected SwitchingTable switchingTable = new SwitchingTable();

    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     * @param overflow       capacité de traitement de l'appareil
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
    public void treat() throws BadCallException
    {

        int port;
        for(Packet p : stack)
        {
            if(p.alreadyTreated) //Si c'était un paquet en attente de libération de la bande passante
            {
                p.alreadyTreated=false;
                this.send(p, p.destinationPort);
                continue;
            }
            if((port = switchingTable.commute(p.dst_mac)) != -1) // S'il connait la destination
            {
                this.send(new Packet(p), port);
            }
            else //S'il ne la connaît pas
            {
                for(int i=0 ; i<ports.size() ; i++)
                {
                    if (i != p.lastPort)
                    {
                        if (ports.get(i) != null)
                            this.send(new Packet(p), i);
                    }
                }
            }
        }
    }

}

