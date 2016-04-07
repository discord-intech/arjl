package hardware.client;


import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;

/**
 * Classe définissant un PC classique
 * @author J. Desvignes
 */
public class StandardPC extends AbstractClient
{
    /**
     * Constructeur à appeller
     *
     * @param MAC la MAC de l'appareil
     * @param IP l'IP statique
     * @param default_gateway la passerelle par défaut
     */
    public StandardPC(int MAC, packet.IP IP, packet.IP default_gateway) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 5971, MAC, IP, default_gateway, ClockSpeed.MEDIUM);
    }

    /**
     * Construteur pour configuration IP par DHCP
     * @param MAC la MAC de l'appareil
     */
    public StandardPC(int MAC) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 5971, MAC, ClockSpeed.MEDIUM);
    }
}