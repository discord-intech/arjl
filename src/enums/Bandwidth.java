package enums;

/**
 * Enum des différentes bandes passantes
 * @author J. Desvignes
 * @author A. Gleye
 */
public enum Bandwidth
{
    NULL(0),        // Placeholder pour la création d'un lien
    ETH_10(1310),   //Fast Ethernet 10Mbps
    ETH_100(13107),    // Fast Ethernet 100Mbps
    SERIAL(64),       // Série 64kbps
    ETH_1G(131072),      // Gigabit Ethernet 1Gbps
    ETH_10G(1310720);    // Gigabit Ethernet 10Gbps


    public final int value;
    Bandwidth(int val)
    {
        this.value = val;
    }

}