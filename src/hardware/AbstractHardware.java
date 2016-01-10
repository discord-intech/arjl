package hardware;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import exceptions.NoFreePortsException;
import link.Link;
import packet.Packet;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les appareils connectés
 */
public abstract class AbstractHardware
{
    /** Liste des liens connectés */
    protected ArrayList<Link> ports;

    /** Liste des types de ports disponibles sur l'appareil */
    protected final ArrayList<LinkTypes> port_types;

    /** Liste des bandes passantes par port (ETH-100M / ETH-1G / ...) */
    protected final ArrayList<Bandwidth> port_bandwidth;

    /** Liste de paquets représentant le tampon de l'appareil */
    protected ArrayList<Packet> stack = new ArrayList<Packet>();

    /** Liste temporaire de paquets pour éviter qu'ils soient traités trop vite */
    protected ArrayList<Packet> futureStack = new ArrayList<Packet>();

    /** Représente le maximum supportable par l'appareil en terme de paquets dans son tampon (stack) */
    protected final int overflowValue;

    /**
     * Constructeur à appeller avec super()
     * @param port_types liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     * @param overflow maximum de paquets supportables dans son tampon de traitement
     */
    public AbstractHardware(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth, int overflow)
    {
        this.port_types = port_types;
        this.port_bandwidth = port_bandwidth;
        this.overflowValue = overflow;
        ports = new ArrayList<Link>(port_types.size());
    }

    /**
     * Renvoie les liens connectés à l'appareil
     */
    public ArrayList<Link> getConnectedLinks()
    {
        return ports;
    }

    /**
     * Renvoie une liste des types de ports disponibles
     */
    public ArrayList<LinkTypes> getFreePorts()
    {
        ArrayList<LinkTypes> free = new  ArrayList<LinkTypes>();
        // On vérifie les ports libres, s'ils le sont, on envoie leur type
        for(int i=0 ; i < ports.size() ; i++)
        {
            if(ports.get(i) == null)
                free.add(port_types.get(i));
        }
        return free;
    }

    /**
     * Permet de connecter un lien
     * @param link le lien
     * @throws NoFreePortsException si aucun port libre
     */
    public void connectLink(Link link) throws NoFreePortsException
    {
        // A chaque port libre, on vérifie s'il est du bon type et de la bonne bande passante
        for(int i=0 ; i < ports.size() ; i++)
        {
            if(ports.get(i) == null)
            {
                if(port_types.get(i) == link.getType() && port_bandwidth.get(i) == link.getBandwidth())
                {
                    ports.set(i, link);
                    return;
                }
            }
        }

        // Echec : on rééssaie sans prendre en compte la bande passante (ex : ETH-100M sur un ETH-1G)
        for(int i=0 ; i < ports.size() ; i++)
        {
            if(ports.get(i) == null)
            {
                if(port_types.get(i) == link.getType())
                {
                    ports.set(i, link);
                    return;
                }
            }
        }

        //Echec critique : impossible de connecter
        throw new NoFreePortsException();
    }

    /**
     * Renvoie la bande passante la plus élevée disponible
     * @param linkType le type de lien
     */
    public Bandwidth getBestBandwidth(LinkTypes linkType) throws BadCallException {
        Bandwidth fastest = Bandwidth.NULL;
        for(int i=0 ; i<port_types.size() ; i++)
        {
            if(port_types.get(i) == linkType)
            {
                if(port_bandwidth.get(i).value > fastest.value)
                    fastest = port_bandwidth.get(i);
            }
        }

        if(fastest == Bandwidth.NULL)
            throw new BadCallException();

        return fastest;

    }

    /**
     * Déconnecte un lien
     * @param link le lien
     */
    public void disconnect(Link link) throws BadCallException {
        for(int i=0 ; i<ports.size() ; i++)
        {
            if(ports.get(i) == link) {
                ports.set(i, null);
                return;
            }
        }
        throw new BadCallException();
    }

    /**
     * Revoie le port sur lequel le lien donné est connecté
     * @param link le lien
     * @return le port ou -1 si le lien n'est pas connecté
     */
    public int whichPort(Link link)
    {
        for(int i=0 ; i<ports.size() ; i++)
        {
            if(ports.get(i) == link)
                return i;
        }
        return -1;
    }

    /**
     * Permet de valider le nouveau stack une fois tous les stacks de tous les appareils traités
     */
    public void validateStack()
    {
        this.stack = this.futureStack;
        this.futureStack = new ArrayList<>();
    }

    /**
     * Ces méthodes envoient et reçoivent des paquets, c'est la couche physique, pas de modification du paquet !
     */
    public abstract void receive(Packet packet, int port);
    public abstract void send(Packet packet, int port) throws BadCallException;

    /**
     * Cette méthode lance le traitement du tampon (tout le tampon pour l'instant)
     */
    public abstract void treat() throws BadCallException;
}