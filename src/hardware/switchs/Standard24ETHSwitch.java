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

package hardware.switchs;

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import exceptions.BadCallException;

import java.util.ArrayList;

/**
 * Switch classique à 24 ports 1Gbit
 * @author J. Desvignes
 */
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
            }}, 1000, ClockSpeed.HIGH);
}
}