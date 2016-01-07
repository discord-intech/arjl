package enums;

/**
 * Enum des différentes bandes passantes
 */
public enum Bandwidth
{
    NULL(0),         // Placeholder pour la création d'un lien
    ETH_100(100),    // Fast Ethernet 100Mbps
    SERIAL(3),       // Série 64kbps
    ETH_1G(1000);    // Gigabit Ethernet 1Gbps


    public int value;
    private Bandwidth(int val)
    {
        this.value = val;
    }

}