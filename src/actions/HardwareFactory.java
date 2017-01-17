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

package actions;

import exceptions.BadCallException;
import hardware.client.StandardPC;
import hardware.hub.CentreComMR820TRHub;
import hardware.hub.LinkBuilder3ComHub;
import hardware.hub.Standard24ETHHub;
import hardware.router.Cisco2811Router;
import hardware.router.CiscoCRS1Router;
import hardware.router.Standard2ETHRouter;
import hardware.router.WANPort;
import hardware.server.DHCPServer;
import hardware.server.StandardFTPServer;
import hardware.server.StandardWEBServer;
import hardware.switchs.AvayaERS2550TSwitch;
import hardware.switchs.NetgearM4100D12GSwitch;
import hardware.switchs.Standard24ETHSwitch;
import packet.IP;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Factory comprenant des fonctions pour créer ses appareils facilement
 * @author J. Desvignes
 * @author A. Gleye
 */
public class HardwareFactory implements Serializable
{
    /** Compteur de MAC pour éviter les doublons */
    private int macCount=0;

    /** Objet de mutex */
    private final Integer lock = 0;

    public void setMacCount(int mac)
    {
        this.macCount = mac;
    }

    public int getMacCount()
    {
        return macCount;
    }


    //TODO A FAIRE !
    //===========================================
    // Constructeurs de HUB
    //===========================================

    public Standard24ETHHub newStandard24ETHHub() throws BadCallException
    {
        return new Standard24ETHHub();
    }

    public LinkBuilder3ComHub newLinkBuilder3Com12ETHHub() throws BadCallException
    {
        return new LinkBuilder3ComHub();
    }

    public CentreComMR820TRHub newCentreComMR820TR8ETHHub() throws BadCallException
    {
        return new CentreComMR820TRHub();
    }
    //===========================================
    // Constructeurs de SWITCH
    //===========================================

    public Standard24ETHSwitch newStandard24ETHSwitch() throws BadCallException
    {
        return new Standard24ETHSwitch();
    }

    public AvayaERS2550TSwitch newAvayaERS2550T50ETHSwitch() throws BadCallException
    {
        return new AvayaERS2550TSwitch();
    }

    public NetgearM4100D12GSwitch newNetgearM4100D12G12ETHSwitch() throws BadCallException
    {
        return new NetgearM4100D12GSwitch();
    }





    //===========================================
    // Constructeurs de ROUTEUR
    //===========================================

    public Standard2ETHRouter newStandard2ETHRouter() throws BadCallException
    {
        synchronized(lock)
        {
            return new Standard2ETHRouter(new ArrayList<Integer>() {{
                for (int i = 0; i < 2; i++) add(macCount + i);
                macCount += 2;
            }}, new ArrayList<IP>() {{
                for (int i = 0; i < 2; i++) add(new IP(0, 0, 0, 0));
            }}, new IP(0, 0, 0, 0),new ArrayList<IP>() {{
                for (int i = 0; i < 2; i++) add(new IP(0, 0, 0, 0));
            }}, 0);
        }
    }
    public CiscoCRS1Router newCiscoCRS1Router() throws BadCallException
    {
        synchronized(lock)
        {
            return new CiscoCRS1Router(new ArrayList<Integer>() {{
                for (int i = 0; i < 12; i++) add(macCount + i);
                macCount += 12;
            }}, new ArrayList<IP>() {{
                for (int i = 0; i < 12; i++) add(new IP(0, 0, 0, 0));
            }}, new ArrayList<IP>() {{
                for (int i = 0; i < 12; i++) add(new IP(0, 0, 0, 0));
            }}, new IP(0, 0, 0, 0), 0);
        }
    }

    public Cisco2811Router newCisco2811Router() throws BadCallException
    {
        synchronized(lock)
        {
            return new Cisco2811Router(new ArrayList<Integer>() {{
                for (int i = 0; i < 3; i++) add(macCount + i);
                macCount += 3;
            }}, new ArrayList<IP>() {{
                for (int i = 0; i < 3; i++) add(new IP(0, 0, 0, 0));
            }}, new ArrayList<IP>() {{
                for (int i = 0; i < 3; i++) add(new IP(0, 0, 0, 0));
            }}, new IP(0, 0, 0, 0), 0);
        }
    }

    public WANPort newWANPort() throws BadCallException
    {
        synchronized (lock)
        {
            macCount += 1;
            return new WANPort((macCount-1), new IP(0,0,0,0), new IP(0,0,0,0));
        }
    }

    //===========================================
    // Constructeurs de CLIENT
    //===========================================

    public StandardPC newStandardPC() throws BadCallException
    {
        synchronized(lock)
        {
            macCount+=1;
            return new StandardPC(macCount-1);
        }
    }

    //===========================================
    // Constructeurs de SERVEUR
    //===========================================

    public StandardWEBServer newStandardWEBServer() throws BadCallException
    {
        synchronized(lock)
        {
            macCount += 1;
            return new StandardWEBServer(macCount-1);
        }

    }

    public StandardFTPServer newStandardFTPServer() throws BadCallException
    {
        synchronized(lock)
        {
            macCount += 1;
            return new StandardFTPServer(macCount-1);
        }

    }

    public DHCPServer newDHCPServer() throws BadCallException
    {
        synchronized(lock)
        {
            macCount += 1;
            return new DHCPServer(macCount-1, new IP(0,0,0,0), new IP(0,0,0,0));
        }

    }
}
