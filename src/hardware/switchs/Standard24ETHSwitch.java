package hardware.switchs;

import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;

import java.util.ArrayList;

public class Standard24ETHSwitch extends AbstractSwitch
{

    /**
     * Constructeur à appeller
     */
    public Standard24ETHSwitch() throws BadCallException {
        super(new ArrayList<LinkTypes>(){{
            add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH);
        }},
                new ArrayList<Bandwidth>(){{
                    add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G); add(Bandwidth.ETH_1G);
                }}, 100);
    }
}