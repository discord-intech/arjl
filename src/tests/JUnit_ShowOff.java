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

package tests;

import actions.Actions;
import enums.LinkTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import hardware.Link;
import hardware.hub.Standard24ETHHub;
import hardware.switchs.Standard24ETHSwitch;
import org.junit.Test;
import packet.Packet;

/**
 * JUnit pour la présentation de l'architecture
 * @author J. Desvignes
 */
public class JUnit_ShowOff
{
    @Test
    public void show() throws BadCallException
    {
        System.out.println((int)0.2);
        Standard24ETHSwitch A = new Standard24ETHSwitch();
        Standard24ETHHub B = new Standard24ETHHub();
        Standard24ETHSwitch C = new Standard24ETHSwitch();

        Link link = Actions.connect(A, B, LinkTypes.ETH);
        Actions.connect(B, C, LinkTypes.ETH);

        System.out.println(link.getHardwareConnected());
        A.start();
        B.start();
        C.start();
        try {
            A.send(new Packet(), 0);
        } catch (OverflowException e) {
            e.printStackTrace();
        }

        while(true);
    }
}

