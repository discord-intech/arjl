package hardware;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import exceptions.NoFreePortsException;
import link.Link;

import java.util.ArrayList;

/**
 * Classe abstraite définissant les appareils connectés
 */
public abstract class AbstractHardware
{
    /** Liste des liens connectés */
    private ArrayList<Link> ports;

    /** Liste des types de ports disponibles sur l'appareil */
    private final ArrayList<LinkTypes> port_types;

    /** Liste des bandes passantes par port (ETH-100M / ETH-1G / ...) */
    private final ArrayList<Bandwidth> port_bandwidth;

    /**
     * Constructeur à appeller avec super()
     * @param port_types liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     */
    public AbstractHardware(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth)
    {
        this.port_types = port_types;
        this.port_bandwidth = port_bandwidth;
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
}