package link;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;
import hardware.AbstractHardware;

import java.util.ArrayList;

/**
 * Classe définissant les liens entre appareils
 */
public class Link
{

    private AbstractHardware hard1;
    private AbstractHardware hard2;
    private Bandwidth bandwidth;
    private LinkTypes type;

    /**
     * Constructeur de lien à appeller avec super()
     * @param hard1 premier appareil
     * @param hard2 deuxième appareil
     */
    public Link(AbstractHardware hard1, AbstractHardware hard2, Bandwidth bandwidth)
    {
        this.hard1 = hard1;
        this.hard2 = hard2;
        this.bandwidth = bandwidth;
    }

    /**
     * Renvoie les appareils connectés aux extrémités
     */
    public ArrayList<AbstractHardware> getHardwareConnected()
    {
        ArrayList<AbstractHardware> ret = new ArrayList<AbstractHardware>();
        ret.add(hard1);
        ret.add(hard2);
        return ret;
    }

    /**
     * Renvoie le type du lien
     */
    public LinkTypes getType()
    {
        return type;
    }

    /**
     * Renvoie la bande passante du lien
     */
    public Bandwidth getBandwidth()
    {
        return bandwidth;
    }

    /**
     * Permet de forcer une bande passante au lien
     */
    public void forceBandwidth(Bandwidth bandwidth)
    {
        this.bandwidth = bandwidth;
    }

    /**
     * Envoie "l'autre" appareil quand l'un d'eux demande avec qui il est connecté
     * @param hardware l'appareil qui fait la demande
     * @throws BadCallException si l'appareil donné n'est même par relié au lien
     */
    public AbstractHardware getOtherHardware(AbstractHardware hardware) throws BadCallException {
        if(hard1 == hardware)
            return hard2;
        else if(hard2 == hardware)
            return hard1;
        throw new BadCallException();
    }
}