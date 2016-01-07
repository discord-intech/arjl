package enums;

/**
 * Enum des différentes bandes passantes
 */
public enum Bandwidth
{
    NULL(0),
    ETH_100(100),
    SERIAL(3),
    ETH_1G(1000);


    public int value;
    private Bandwidth(int val)
    {
        this.value = val;
    }

}