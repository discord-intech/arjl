package hardware.router;


import enums.Bandwidth;
import enums.LinkTypes;
import hardware.AbstractHardware;

import java.util.ArrayList;

public abstract class AbstractRouter extends AbstractHardware
{

    /**
     * Constructeur à appeller avec super()
     *
     * @param port_types     liste des types de liens connectables
     * @param port_bandwidth liste des bandes passantes (couplée avec port_types !)
     */
    public AbstractRouter(ArrayList<LinkTypes> port_types, ArrayList<Bandwidth> port_bandwidth) {
        super(port_types, port_bandwidth);
    }


}