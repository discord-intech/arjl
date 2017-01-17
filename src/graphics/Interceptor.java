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

package graphics;


import java.io.OutputStream;
import java.io.PrintStream;

public class Interceptor extends PrintStream {
    public Interceptor(OutputStream out) {
        super(out, true);
    }

    @Override
    public synchronized void print(String s) {
        try {
            DeviceManager.addLineVerbose(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void println(String s) {
        try {
            DeviceManager.addLineVerbose(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void print(int i) {
        String s = "" + i;
        try {
            DeviceManager.addLineVerbose(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void println(int i) {
        String s = "" + i;
        try {
            DeviceManager.addLineVerbose(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void print(long i) {
        String s = "" + i;
        try {
            DeviceManager.addLineVerbose(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public synchronized void println(long i) {
        String s = "" + i;
        try {
            DeviceManager.addLineVerbose(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


