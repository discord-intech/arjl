package hardware;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe définissant les liens entre appareils
 * @author J. Desvignes
 */
public class Link implements Serializable
{

    /** Le premier appareil */
    private final AbstractHardware hard1;
    /** Le second appareil */
    private final AbstractHardware hard2;
    /** La bande passante du lien */
    private Bandwidth bandwidth;
    /** Le type de lien (ETH, Serial,...) */
    private final LinkTypes type;

    /** Permet de compter les liens instanciés */
    private static int linksCount=0;

    /**
     * Constructeur de lien
     * @param hard1 premier appareil
     * @param hard2 deuxième appareil
     * @param linktype type de lien
     * @param bandwidth bande passante
     */
    public Link(AbstractHardware hard1, AbstractHardware hard2, LinkTypes linktype, Bandwidth bandwidth)
    {
        this.hard1 = hard1;
        this.hard2 = hard2;
        this.bandwidth = bandwidth;
        this.type = linktype;
        linksCount++;
    }

    /**
     * Renvoie les appareils connectés aux extrémités
     */
    public ArrayList<AbstractHardware> getHardwareConnected()
    {
        ArrayList<AbstractHardware> ret = new ArrayList<>();
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
    public AbstractHardware getOtherHardware(AbstractHardware hardware){
        if(hard1 == hardware)
            return hard2;
        else if(hard2 == hardware)
            return hard1;
        return null;
    }

    /**
     * Oublie le lien (décrémente le compte de liens)
     */
    public void forget()
    {
        linksCount--;
    }

    /**
     * Renvoie le nombre de liens instanciés
     */
    public static int getLinksCount()
    {
        return linksCount;
    }
}