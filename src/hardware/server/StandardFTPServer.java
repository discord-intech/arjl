package hardware.server;

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;

/**
 * Classe définissant un serveur FTP classique
 * @author J. Desvignes
 */
public class StandardFTPServer extends AbstractServer
{

    /**
     * Constructeur
     *
     * @param MAC mac de l'appareil
     * @param IP IP de l''appareil (config statique)
     * @param default_gateway passerelle par défaut
     */
    public StandardFTPServer(int MAC, packet.IP IP, packet.IP default_gateway) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 5971, MAC, IP, default_gateway, PacketTypes.FTP, ClockSpeed.MEDIUM);
    }

    public StandardFTPServer(int MAC) throws BadCallException
    {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 5971, MAC, PacketTypes.FTP, ClockSpeed.MEDIUM);
    }
}