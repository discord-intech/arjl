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

package enums;

import actions.HardwareFactory;
import exceptions.BadCallException;
import hardware.AbstractHardware;
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

/**
 * Enum des différents hardwares disponibles et de leur dénomination
 * @author J. Desvignes
 * @author A. Gleye
 */
public enum Hardware
{

    PC("pc"),
    DHCPSERVER("dhcpserver"),
    WEBSERVER("webserver"),
    FTPSERVER("ftpserver"),
    STANDARDHUB("standardhub"),
    CENTRECOMMR820TRHUB("centrecomhub8eth"),
    LINKBUILDER3COMHUB("linkbuilder3com12eth"),
    STANDARD24ETHHUB("standardhub24eth"),
    STANDARDSWITCH("standardswitch"),
    AVAYAERS2550T50ETHSWITCH("avayaswitch50eth"),
    NETGEARM4100D12G12ETHSWITCH("netgearswitch12eth"),
    STANDARDROUTER("standardrouter"),
    STANDARD2ETHROUTER("standardrouter2eth"),
    CISCO2ETH1SERIALROUTER("ciscorouter2eth1serial"),
    CISCO12ETHROUTER("ciscorouter12eth"),
    WAN("wan");



    public static HardwareFactory factory = new HardwareFactory();
    private String name;
    Hardware(String name)
    {
        this.name = name;
    }

    public static AbstractHardware getHardware(String request) throws BadCallException
    {
        if(request.equals(PC.name))
            return factory.newStandardPC();
        else if(request.equals(DHCPSERVER.name))
            return factory.newDHCPServer();
        else if(request.equals(WEBSERVER.name))
            return factory.newStandardWEBServer();
        else if(request.equals(STANDARDHUB.name))
            return factory.newStandard24ETHHub();
        else if(request.equals(CENTRECOMMR820TRHUB.name))
            return factory.newCentreComMR820TR8ETHHub();
        if(request.equals(LINKBUILDER3COMHUB.name))
            return factory.newLinkBuilder3Com12ETHHub();
        if(request.equals(STANDARD24ETHHUB.name))
            return factory.newStandard24ETHHub();
        else if(request.equals(STANDARDROUTER.name))
            return factory.newStandard2ETHRouter();
        if(request.equals(STANDARD2ETHROUTER.name))
            return factory.newStandard2ETHRouter();
        if(request.equals(CISCO2ETH1SERIALROUTER.name))
            return factory.newCisco2811Router();
        if(request.equals(CISCO12ETHROUTER.name))
            return factory.newCiscoCRS1Router();
        else if(request.equals(STANDARDSWITCH.name))
            return factory.newStandard24ETHSwitch();
        else if(request.equals(AVAYAERS2550T50ETHSWITCH.name))
            return factory.newAvayaERS2550T50ETHSwitch();
        else if(request.equals(NETGEARM4100D12G12ETHSWITCH.name))
            return factory.newNetgearM4100D12G12ETHSwitch();
        else if(request.equals(FTPSERVER.name))
            return factory.newStandardFTPServer();
        else if(request.equals(WAN.name))
            return factory.newWANPort();

        throw new BadCallException();
    }

    public static String getName(AbstractHardware hard)
    {
        if(hard instanceof StandardPC)
            return "PC Standard";
        else if(hard instanceof StandardWEBServer)
            return "Serveur WEB";
        else if(hard instanceof StandardFTPServer)
            return "Serveur FTP";
        else if(hard instanceof Standard2ETHRouter)
            return "Routeur standard 2 ports ETH";
        else if(hard instanceof CiscoCRS1Router)
            return "Routeur CISCO CRS1 12 ports ETH";
        else if(hard instanceof Cisco2811Router)
            return "Routeur CISCO 2811 2 ports ETH 1 port SERIAL";
        else if(hard instanceof Standard24ETHHub)
            return "HUB standard 24 ports ETH";
        else if(hard instanceof CentreComMR820TRHub)
            return "HUB CentreCom MR820TR 8 ports ETH";
        else if(hard instanceof LinkBuilder3ComHub)
            return "HUB 3Com LinkBuilder 12 ports ETH";
        else if(hard instanceof Standard24ETHSwitch)
            return "Switch standard 24 ports ETH";
        else if (hard instanceof AvayaERS2550TSwitch)
            return "Switch Avaya ERS2550T 50 ports ETH";
        else if (hard instanceof NetgearM4100D12GSwitch)
            return "Switch Netgear M4100-D12G 12 ports ETH";
        else if(hard instanceof DHCPServer)
            return "Serveur DHCP";
        else if(hard instanceof WANPort)
            return "Port WAN";

        return "Appareil inconnu";
    }
}
