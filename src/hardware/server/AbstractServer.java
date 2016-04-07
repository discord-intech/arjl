package hardware.server;


import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import hardware.AbstractHardware;
import hardware.router.AbstractRouter;
import packet.IP;
import packet.Packet;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les serveurs
 * @author J. Desvignes
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
                          int overflow, int MAC, PacketTypes type, ClockSpeed speed) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}}, new ArrayList<IP>(){{add(null);}}, speed);
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
                          IP IP, IP default_gateway, PacketTypes type, ClockSpeed speed) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}}, new ArrayList<IP>(){{add(IP);}},
                default_gateway, 0, speed);
        this.IP = IP;
        this.MAC = MAC;
        this.type=type;
    }

    @Override
    public void receive(Packet packet, int port) throws OverflowException {
        if(this.stack.size() >= this.overflowValue)
            throw new OverflowException(this);
        packet.lastPort = port;
        synchronized (this.ports) {
        this.stack.add(packet);
        }
    }

    @Override
    protected void treatData(Packet p) throws BadCallException, OverflowException {
        if ((this.type == PacketTypes.WEB) && (p.getType() == PacketTypes.WEB)) //DEBUG !!!
        {
            System.out.println(this.IP + " : reçu WEB de " + p.src_addr);
            for(int i= 0; i<PacketTypes.WEB.size ; i++)
            {
                try {
                    Thread.sleep((int)(this.treatmentSpeed.speed * AbstractHardware.TIMESPEED));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.send(new Packet(p.src_addr,
                        IPinterfaces.get(p.lastPort),
                        MACinterfaces.get(p.lastPort), p.src_mac, PacketTypes.WEB, true, false), p.lastPort);
            }
        }
        if ((this.type == PacketTypes.FTP) && (p.getType() == PacketTypes.FTP)) //DEBUG !!!
        {
            System.out.println(this.IP + " : reçu FTP de " + p.src_addr);
            for(int i= 0; i<PacketTypes.FTP.size ; i++) {
                try {
                    Thread.sleep((int)(this.treatmentSpeed.speed * AbstractHardware.TIMESPEED));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.send(new Packet(p.src_addr,
                        IPinterfaces.get(p.lastPort),
                        MACinterfaces.get(p.lastPort), p.src_mac, PacketTypes.FTP, true, false), p.lastPort);
            }
        }
    }

    @Override
    protected void printMore(int port)
    {
        if (awaitingForIP[port])
            System.out.println("    Pas de configuration IP en place ; veuillez lancer la requête DHCP ou configurer de manière statique");
        else
            System.out.println("    IP : " + IPinterfaces.get(port).toString() + "   MAC : " + MACinterfaces.get(port).toString());

        System.out.println("    Serveur de type "+this.type.name());
    }
}