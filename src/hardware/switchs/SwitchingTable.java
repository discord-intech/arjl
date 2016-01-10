package hardware.switchs;

import java.util.ArrayList;

/**
 * Classe définissant les tables de commutation
 */
public class SwitchingTable
{
    private ArrayList<Integer> macs = new ArrayList<>();
    private ArrayList<Integer> ports = new ArrayList<>();

    public void addRule(int mac, int port)
    {
        macs.add(mac);
        ports.add(port);
    }

    /**
     * Cherche la mac dans sa table et renvoie le port correspondant
     * @param mac la mac
     * @return le port correspondant ; -1 si absente de la table
     */
    public int commute(int mac)
    {
        for(int i=0 ; i<macs.size() ; i++)
            if(macs.get(i) == mac)
                return ports.get(i);
        return -1;
    }

}