package hardware.switchs;


import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import hardware.AbstractHardware;
import packet.Packet;
import table.SwitchingTable;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les switchs à apprentissage
 * @author J. Desvignes
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
    public AbstractSwitch(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth, int overflow, ClockSpeed speed) throws BadCallException {
        super(port_types, port_bandwidth, overflow, speed);
    }

    /**
     * Vérifie s'il connaît que l'appareil est dans sa base de données
     * @param mac la mac reçue
     * @param port le port
     * @return true s'il a ajouté une règle ; false sinon
     */
    private boolean tryToLearn(int mac, int port)
    {
        if(mac != -1 && switchingTable.commute(mac) == -1)
        {
            switchingTable.addRule(mac, port);
            return true;
        }
        return false;
    }

    @Override
    public void receive(Packet packet, int port) throws OverflowException {
        if(this.stack.size() >= this.overflowValue)
            throw new OverflowException(this);
        packet.lastPort = port;
        this.tryToLearn(packet.src_mac, port);
        synchronized (this.ports) {
            this.stack.add(packet);
        }
    }

    @Override
    public synchronized void treat() throws BadCallException, OverflowException {

        int port;
        if(stack.isEmpty())
            return;
        Packet p;
        synchronized (this.ports) {
            p = stack.get(0);
            stack.remove(0);
        }
        if(p.alreadyTreated) //Si c'était un paquet en attente de libération de la bande passante
        {
            p.alreadyTreated=false;
            this.send(p, p.destinationPort);
            return;
        }
        if(p.dst_mac != -1 && (port = switchingTable.commute(p.dst_mac)) != -1) // S'il connait la destination
        {
            if(this.ports.get(port) != null)
                this.send(new Packet(p), port);
            else
            {
                this.switchingTable.removeRules(port);
                this.stack.add(0, p);
            }
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

