package hardware.client;


import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;

public class StandardPC extends AbstractClient
{
    /**
     * Constructeur à appeller
     *
     * @param MAC
     * @param IP
     * @param default_gateway
     */
    public StandardPC(int MAC, packet.IP IP, packet.IP default_gateway) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 20, MAC, IP, default_gateway, 0);
    }

    public StandardPC(int MAC) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 20, MAC);
    }
}