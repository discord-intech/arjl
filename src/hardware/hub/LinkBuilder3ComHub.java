package hardware.hub;

import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;

import java.util.ArrayList;

/**
 * Classe définissant un HUB 3Com LinkBuilder FMSII à 12 ports Ethernet (http://www.manualslib.com/manual/443470/3com-3c16670-Linkbuilder-Fms-Ii-Hub.html?page=4#manual)
 * @author A. Gleye
 */
public class LinkBuilder3ComHub extends AbstractHub
{

    /**
     * Constructeur à appeller
     */
    public LinkBuilder3ComHub() throws BadCallException {
        super(new ArrayList<LinkTypes>(){{
                  add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH);
              }},
                new ArrayList<Bandwidth>(){{
                    add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10);
                }});
    }
}