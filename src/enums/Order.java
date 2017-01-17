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

/**
 * Enum des différents ordres textuels que peut envoyer l'utilisateur
 * @author J. Desvignes
 */
public enum Order
{
    GO("go"),
    PAUSE("pause"),
    READSCRIPT("readscript"),
    ADD("add"),
    REMOVE("remove"),
    LINK("link"),
    EDIT("edit"),
    START("start"),
    STOP("stop"),
    REQUEST("request"),
    DHCP("dhcprequest"),
    CONFIG("config"),
    VERBOSE("verbose"),
    EXIT("exit"),
    DHCPRELAY("dhcprelay"),
    REPETITIVEREQUEST("repetitiverequest"),
    SAVE("save"),
    LOAD("load"),
    PIPO("aymeric"),
    RIP("rip"),
    IP("setip"),
    PRINT("print"),
    GATEWAY("gateway"),
    ADDRANGE("addrange"),
    SPEED("speed"),
    PRINTROUTES("printroutes"),
    STARTALL("startall"),
    ADDROUTE("addroute"),
    CLEARROUTES("clearroutes"),
    DEFAULTROUTE("defaultroute"),
    PRINTSUB("printsub"),
    ADDSUB("addsub"),
    MULTIPROCESSIP("multiprocessip");



    public String order;

    Order(String text)
    {
        this.order = text;
    }
}
