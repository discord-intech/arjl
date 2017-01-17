/**
 * Copyright (C) 2016 Desvignes Julian, Louis-Baptiste Trailin, Aymeric Gleye, Rémi Dulong
 */

/**
 This file is part of ARJL.

 ARJL is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 ARJL is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with ARJL.  If not, see <http://www.gnu.org/licenses/>

 */

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
