package hardware;


import enums.LinkTypes;
import exceptions.NoFreePortsException;
import link.AbstractLink;
import java.util.ArrayList;

public abstract class AbstractHardware
{
    /** Liste des liens connectés */
    private ArrayList<AbstractLink> ports;

    /** Liste des types de ports disponibles sur l'appareil */
    private ArrayList<LinkTypes> port_types;


    /**
     * Constructeur à appeller avec super()
     * @param port_types liste des types de liens connectables
     */
    public AbstractHardware(ArrayList<LinkTypes> port_types)
    {
        this.port_types = port_types;
        ports = new ArrayList<AbstractLink>(port_types.size());
    }

    /**
     * Renvoie les liens connectés à l'appareil
     */
    public ArrayList<AbstractLink> getConnectedLinks()
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
     * Permet de connecter un lien, à override s'il y a des rétro-compatibilités (type ETH-1G sur du ETH-100M)
     * @param link le lien
     * @throws NoFreePortsException si aucun port libre
     */
    public void connectLink(AbstractLink link) throws NoFreePortsException
    {
        // A chaque port libre, on vérifie s'il est du bon type
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
        throw new NoFreePortsException();
    }
}