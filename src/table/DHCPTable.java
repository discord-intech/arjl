package table;


import packet.IP;

import java.util.ArrayList;

/**
 * Classe définissant la base de donnée DHCP, incluant les plages adressables par sous-résea
 */
public class DHCPTable
{
	/** les sous-réseaux */
    ArrayList<IP> subnets = new ArrayList<IP>();
    
    /** les plages associées aux sous-réseaux */
    ArrayList<IP[]> ranges = new ArrayList<IP[]>();

    /**
     * Donne une plage pour le sous-réseau donné
     * Renvoie une plage vide si le sous-réseau n'est pas répertorié
     * @param subnet le sous-réseau
     * @param mask son masque associé
     */
    public IP[] gimmeARange(IP subnet, IP mask)
    {
        if(!subnets.contains(subnet)) //Si la demande est d'un sous-réseau non couvert par le serveur
        {
            IP[] range = new IP[2];
            range[0] = new IP(0,0,0,0);
            range[1] = new IP(0,0,0,0);
            return range;
        }

        return ranges.get(subnets.indexOf(subnet));
    }

    /**
     * Ajoute une plage
     * @param subnet le sous-réseau en bénéficiant
     * @param range la plage associée
     */
    public void addRange(IP subnet, IP[] range)
    {
        this.subnets.add(subnet);
        this.ranges.add(range);
    }

}