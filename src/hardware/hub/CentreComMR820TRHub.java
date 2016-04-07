package hardware.hub;

import enums.Bandwidth;
import enums.LinkTypes;
import exceptions.BadCallException;

import java.util.ArrayList;

/**
 * Created by Aymeric on 24/03/2016.
 * Hub CentreCom MR820TR avec 8 ports ETH (http://www.secondpc.sk/files/ATI_MR820TR_manual.pdf)
 */
public class CentreComMR820TRHub extends AbstractHub {

    /**
     * Constructeur à appeller
     */
    public CentreComMR820TRHub() throws BadCallException {
        super(new ArrayList<LinkTypes>(){{
                  add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH); add(LinkTypes.ETH);
              }},
                new ArrayList<Bandwidth>(){{
                   add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10); add(Bandwidth.ETH_10);
                }});
    }
}
