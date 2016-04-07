package hardware.server;

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;

/**
 * Classe définissant un serveur WEB classique
 * @author J. Desvignes
 */
public class StandardWEBServer extends AbstractServer
{

    /**
     * Constructeur
     *
     * @param MAC mac de l'appareil
     * @param IP IP de l''appareil (config statique)
     * @param default_gateway passerelle par défaut
     */
    public StandardWEBServer(int MAC, packet.IP IP, packet.IP default_gateway) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 600, MAC, IP, default_gateway, PacketTypes.WEB, ClockSpeed.MEDIUM);
    }

    public StandardWEBServer(int MAC) throws BadCallException
    {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 600, MAC, PacketTypes.WEB, ClockSpeed.MEDIUM);
    }
}