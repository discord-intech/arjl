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
 * Enum des types de liens
 * @author J. Desvignes
 */
public enum LinkTypes
{
    ETH("eth"),
    SERIAL("serial");

    private String name;
    LinkTypes(String name)
    {
        this.name = name;
    }

    public static LinkTypes getType(String text)
    {
        for(LinkTypes i : LinkTypes.values())
        {
            if(i.name.equals(text))
                return i;
        }
        return null;
    }
}