package hardware.switchs;

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;

import java.util.ArrayList;

/**
 * Created by Aymeric on 24/03/2016.
 * Switch Avaya ERS 2550T-PWR à 50 ports Ethernet(http://vivecommunications.com/wp-content/uploads/2012/12/Avaya-2500-series-Routing-Switches.pdf)
 */
public class AvayaERS2550TSwitch extends AbstractSwitch
{
    /**
     * Constructeur à appeller
     */
    public AvayaERS2550TSwitch() throws BadCallException {
        super(new ArrayList<LinkTypes>(){{
                  add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH);
              }},
                new ArrayList<Bandwidth>(){{
                    add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_100); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G);
                }}, 131072, ClockSpeed.HIGH);
    }
}
