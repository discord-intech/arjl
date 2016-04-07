package hardware.switchs;

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;

import java.util.ArrayList;

/**
 * Created by Aymeric on 24/03/2016.
 * Switch Neatgear M4100-D12G avec 12 ports Ethernet 1Gb (http://www.downloads.netgear.com/files/GDC/datasheet/fr/M4100.pdf)
 */

public class NetgearM4100D12GSwitch extends AbstractSwitch {
    /**
     * Constructeur à appeller
     */
    public NetgearM4100D12GSwitch() throws BadCallException {
        super(new ArrayList<LinkTypes>(){{
                    add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH);
              }},
                new ArrayList<Bandwidth>(){{
                    add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G);
                }}, 131072, ClockSpeed.HIGH);
    }
}


