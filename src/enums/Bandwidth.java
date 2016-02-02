package enums;

/**
 * Enum des différentes bandes passantes
 */
public enum Bandwidth
{
    NULL(0),         // Placeholder pour la création d'un lien
    ETH_100(10),    // Fast Ethernet 100Mbps
    SERIAL(1),       // Série 64kbps
    ETH_1G(30);    // Gigabit Ethernet 1Gbps


    public final int value;
    Bandwidth(int val)
    {
        this.value = val;
    }

}