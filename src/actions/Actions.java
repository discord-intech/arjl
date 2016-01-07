package actions;

import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import exceptions.NoFreePortsException;
import hardware.AbstractHardware;
import link.Link;

import java.util.ArrayList;

/**
 * Classe définissant des méthodes statique pour manipuler les éléments réseau (connection/déconnection/...)
 */
public class Actions
{

    /**
     * Permet de connecter deux appareils avec un lien spécifique
     * @param hard1 1er appareil
     * @param hard2 2eme appareil
     * @param linkType type de lien
     * @return le lien si OK ; null sinon
     */
    public static synchronized Link connect(AbstractHardware hard1, AbstractHardware hard2, LinkTypes linkType) throws BadCallException {
        //On vérifie que les 2 appareils ont bien un port de libre
        if(!hard1.getFreePorts().contains(linkType) || !hard2.getFreePorts().contains(linkType))
            return null;

        Link link = new Link(hard1, hard2, Bandwidth.NULL);

        // On check la bande passante à utiliser
        //TODO à optimiser !
        if(hard1.getBestBandwidth(linkType) != hard2.getBestBandwidth(linkType))
        {
            Bandwidth bhard1 = hard1.getBestBandwidth(linkType);
            Bandwidth bhard2 = hard2.getBestBandwidth(linkType);
            if(bhard1.value < bhard2.value)
                link.forceBandwidth(bhard1);
            else
                link.forceBandwidth(bhard2);
        }
        else
        {
            link.forceBandwidth(hard1.getBestBandwidth(linkType));
        }

        try {
            hard1.connectLink(link);
            hard2.connectLink(link);
        }  catch (NoFreePortsException e) {
            throw new BadCallException();
        }
        return link;
    }

    /**
     * Permet de débrancher un câble, libère les ports des appareils
     * @param link le lien à débrancher
     */
    public static synchronized void disconnect(Link link)
    {
        ArrayList<AbstractHardware> hard = link.getHardwareConnected();

        try {
            hard.get(0).disconnect(link);
            hard.get(1).disconnect(link);
            link.forget();
        } catch (BadCallException e) {
            e.printStackTrace();
        }

    }
}