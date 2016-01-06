package link;


import enums.LinkTypes;
import hardware.AbstractHardware;

import java.util.ArrayList;

public abstract class AbstractLink
{

    private AbstractHardware hard1;
    private AbstractHardware hard2;
    private int bandwidth;
    private LinkTypes type;

    /**
     * Constructeur de lien à appeller avec super()
     * @param hard1 premier appareil
     * @param hard2 deuxième appareil
     */
    public AbstractLink(AbstractHardware hard1, AbstractHardware hard2, int bandwidth)
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
     * Permet de forcer une bande passante au lien
     */
    public void forceBandwidth(int bandwidth)
    {
        this.bandwidth = bandwidth;
    }
}