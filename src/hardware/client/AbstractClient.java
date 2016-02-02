package hardware.client;

import enums.Bandwidth;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import hardware.router.AbstractRouter;
import hardware.Link;
import packet.IP;
import packet.Packet;
import table.WaitingTable;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les clients (end-user)
 */
public abstract class AbstractClient extends AbstractRouter
{
    /**
     * La MAC de l'appareil
     */
    protected int MAC;

    /**
     * Table d'attente de réponses
     */
    protected WaitingTable waitingRequests = new WaitingTable();


    /**
     * Constructeur à appeller
     *
     * @param port_type le type de connectique
     * @param port_bandwidth la bande passante de la connectique
     * @param overflow       maximum de paquets supportables dans son tampon de traitement
     * @param MAC la mac de l'appareil
     * @param IP l'IP statique
     * @param default_gateway la passerelle par défaut
     */
    public AbstractClient(LinkTypes port_type, Bandwidth port_bandwidth, int overflow, int MAC,
                          IP IP, IP default_gateway) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}}, new ArrayList<IP>(){{add(IP);}},
                default_gateway, 0);
        this.IP = IP;
        this.MAC = MAC;
    }

    /**
     * Constructeur à appeller
     *
     * @param port_type le type de connectique
     * @param port_bandwidth la bande passante de la connectique
     * @param overflow       maximum de paquets supportables dans son tampon de traitement
     * @param MAC  la mac de l'appareil
     */
    public AbstractClient(LinkTypes port_type, Bandwidth port_bandwidth, int overflow, int MAC) throws BadCallException {
        super(new ArrayList<LinkTypes>(){{add(port_type);}}, new ArrayList<Bandwidth>(){{add(port_bandwidth);}}, overflow, new ArrayList<Integer>(){{add(MAC);}},
                new ArrayList<IP>());
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
        if(RNG.nextInt(1001) < collisionRate) //Si on perd le paquet dans une collision
            return;
        if(packet.TTLdown())
            return;
        Link link = ports.get(port);
        link.getOtherHardware(this).receive(packet, ports.get(port).getOtherHardware(this).whichPort(link));
        if(packet.tracked)
            System.out.println(this.toString()+" : sent "+packet.getType()+" to "+packet.dst_addr+" with NHR="+packet.getNHR()+" isResponse="+packet.isResponse);
    }

    @Override
    protected void treatData(Packet p) throws BadCallException {
        if (p.isResponse)
        {
            waitingRequests.doIWaitForIt(p.getType(), p.src_addr);
        }
    }

    /**
     * Lance une requête spécifiée
     * @param type le type de paquet, donc le type de requête
     * @param destination l'IP du destinataire
     */
    public synchronized void launchRequest(PacketTypes type, IP destination)
    {
        if(this.awaitingForIP)
        {
            return;
        }
        if(type == PacketTypes.WEB)
        {
            futureStack.add(new Packet(destination, this.IP, this.MAC, -1, PacketTypes.WEB, false, false));
            waitingRequests.addWaiting(PacketTypes.WEB, destination);
            //System.out.println(this.IP+" : Envoi requête WEB vers "+destination);
        }
    }

    /**
     * Renvoie true si l'appareil attends la réponse d'un appareil
     */
    public boolean waitsForSomething()
    {
        return waitingRequests.AmIWaitingForSomething();
    }

    /**
     * Vérifie les timeout et les décrémente, supprime une requête si elle a timeout
     * @return true si on a eu un timeout, false sinon
     */
    public boolean timeoutCheck()
    {
        return this.waitingRequests.isThereATimeout();
    }

}