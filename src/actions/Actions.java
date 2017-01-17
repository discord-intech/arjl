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

import enums.Bandwidth;
import enums.Hardware;
import enums.LinkTypes;
import exceptions.BadCallException;
import exceptions.NoFreePortsException;
import graphics.DeviceManager;
import graphics.ImageToSave;
import graphics.LinkToSave;
import hardware.AbstractHardware;
import hardware.client.AbstractClient;
import hardware.router.AbstractRouter;
import hardware.Link;

import java.io.*;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Classe définissant des méthodes statique pour manipuler les éléments réseau (connection/déconnection/...)
 * @author J. Desvignes
 */
public class Actions
{

    /**
     * Permet de connecter deux appareils avec un lien spécifique
     * @param hard1 1er appareil
     * @param hard2 2eme appareil
     * @param linkType type de lien
     * @return le lien si OK ; null sinon
     */
    public static synchronized Link connect(AbstractHardware hard1, AbstractHardware hard2, LinkTypes linkType) throws BadCallException {
        //On vérifie que les 2 appareils ont bien un port de libre
        if(!hard1.getFreePorts().contains(linkType) || !hard2.getFreePorts().contains(linkType))
            throw new BadCallException();

        if(hard1 instanceof AbstractRouter || hard2 instanceof AbstractRouter)
        {
            //TODO vérification spéciale pour connecter le bon port au bon sous-réseau
        }

        Link link = new Link(hard1, hard2, linkType, Bandwidth.NULL);

        // On check la bande passante à utiliser
        //TODO à optimiser !
        if(hard1.getBestBandwidth(linkType) != hard2.getBestBandwidth(linkType))
        {
            Bandwidth bhard1 = hard1.getBestBandwidth(linkType);
            Bandwidth bhard2 = hard2.getBestBandwidth(linkType);
            if(bhard1.value < bhard2.value)
                link.forceBandwidth(bhard1);
            else
                link.forceBandwidth(bhard2);
        }
        else
        {
            link.forceBandwidth(hard1.getBestBandwidth(linkType));
        }

        try {
            hard1.connectLink(link);
            hard2.connectLink(link);
        }  catch (NoFreePortsException e) {
            throw new BadCallException();
        }
        return link;
    }

    /**
     * Permet de débrancher un câble, libère les ports des appareils
     * @param link le lien à débrancher
     */
    public static synchronized void disconnect(Link link)
    {
        ArrayList<AbstractHardware> hard = link.getHardwareConnected();

        try {
            hard.get(0).disconnect(link);
            hard.get(1).disconnect(link);
            link.forget();
        } catch (BadCallException e) {
            System.out.println("Impossible to disconnect");
            e.printStackTrace();
        }

    }

    /**
     * Donne les valeurs d'occupation d'une liste de liens en %
     * @param links la liste de liens
     * @return int[] avec index correspondant à la liste de liens
     * @throws BadCallException si l'un de ces liens est orphelin
     */
    public static int[] linkStats(ArrayList<Link> links) throws BadCallException
    {
        int[] res = new int[links.size()];

        for(int i=0 ; i<links.size() ; i++)
        {
            res[i] = links.get(i).getHardwareConnected().get(0).getLinkStats(links.get(i));
        }
        return res;
    }

    /**
     * Donne les valeurs d'occupation d'une liste d'appareils en %
     * @param hards la liste d'appareils
     * @return int[] avec index correspondant à la liste d'appareils
     */
    public static int[] hardwareStats(ArrayList<AbstractHardware> hards)
    {
        int[] res = new int[hards.size()];

        for(int i=0 ; i<hards.size() ; i++)
        {
            res[i] = hards.get(i).getStats();
        }

        return res;
    }


    /**
     * Sauvegarde le graphe donné (pour interface NOGUI)
     * @param filename le nom de fichier
     * @param hards les appareils
     * @param names les surnoms
     * @param links les liens
     * @throws BadCallException
     */
    public static void saveNOGUIGraph(String filename, ArrayList<AbstractHardware> hards, ArrayList<String> names, ArrayList<Link> links) throws BadCallException
    {
        FileOutputStream writer;
        ZipOutputStream zipper;
        ObjectOutputStream serializer;
        File fl = new File(filename+".arjlng");
        if(fl.isDirectory())
            throw new BadCallException();

        if(fl.exists())
            fl.delete();

        try
        {
            writer = new FileOutputStream(fl);
            zipper = new ZipOutputStream(writer);
            ZipEntry ze= new ZipEntry("links");
            zipper.putNextEntry(ze);
            serializer = new ObjectOutputStream(zipper);
            serializer.writeObject(links);
            serializer.flush();
            zipper.closeEntry();

            ze= new ZipEntry("hard");
            zipper.putNextEntry(ze);
            serializer.writeObject(hards);
            serializer.flush();
            zipper.closeEntry();

            ze= new ZipEntry("name");
            zipper.putNextEntry(ze);
            serializer.writeObject(names);
            serializer.flush();
            zipper.closeEntry();

            ze = new ZipEntry("count");
            zipper.putNextEntry(ze);
            zipper.write(Hardware.factory.getMacCount());
            zipper.flush();
            zipper.closeEntry();

            zipper.close();
            serializer.close();

        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new BadCallException();
        }
    }

    /**
     * Charge un graphe pour l'interface NOGUI
     * @param filename le nom de fichier
     * @return {liens, appareils, surnoms}
     */
    public static ArrayList<ArrayList<Object>> loadNOGUIGraph(String filename) throws BadCallException
    {
        FileInputStream reader;
        ZipInputStream zipper;
        ObjectInputStream deserializer;

        ArrayList<ArrayList<Object>> res = new ArrayList<>();
        res.add(null);
        res.add(null);
        res.add(null);

        File fl = new File(filename+".arjlng");

        if(!fl.exists())
            throw new BadCallException();

        try
        {

            reader = new FileInputStream(fl);
            zipper = new ZipInputStream(reader);
            ZipEntry ze = zipper.getNextEntry();
            deserializer = new ObjectInputStream(zipper);
            while(ze!=null)
            {
                if(ze.getName().equals("links"))
                {
                    res.set(0, (ArrayList<Object>) deserializer.readObject());
                }
                else if(ze.getName().equals("hard"))
                {
                    res.set(1, (ArrayList<Object>) deserializer.readObject());
                }
                else if(ze.getName().equals("name"))
                {
                    res.set(2, (ArrayList<Object>) deserializer.readObject());
                }
                else if(ze.getName().equals("count"))
                {
                    Hardware.factory.setMacCount(zipper.read());
                }
                ze = zipper.getNextEntry();
            }

        }
        catch(IOException|ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new BadCallException();
        }

        return res;
    }

    /**
     * Sauvegarde le graphe donné (pour interface GUI)
     * @param filename le nom de fichier
     * @param hards les appareils
     * @param links les liens
     * @param images les images
     * @param imgLinks les liens de l'interface
     * @throws BadCallException
     */
    public static void saveGUIGraph(String filename, ArrayList<AbstractHardware> hards, ArrayList<Link> links,
                                    ArrayList<ImageToSave> images, ArrayList<LinkToSave> imgLinks) throws BadCallException
    {
        FileOutputStream writer;
        ZipOutputStream zipper;
        ObjectOutputStream serializer;
        File fl = new File(filename);
        if(fl.isDirectory())
            throw new BadCallException();

        if(fl.exists())
            fl.delete();

        try
        {
            writer = new FileOutputStream(fl);
            zipper = new ZipOutputStream(writer);
            ZipEntry ze= new ZipEntry("links");
            zipper.putNextEntry(ze);
            serializer = new ObjectOutputStream(zipper);
            serializer.writeObject(links);
            serializer.flush();
            zipper.closeEntry();

            ze= new ZipEntry("hard");
            zipper.putNextEntry(ze);
            serializer.writeObject(hards);
            serializer.flush();
            zipper.closeEntry();

            ze = new ZipEntry("count");
            zipper.putNextEntry(ze);
            zipper.write(DeviceManager.factory.getMacCount());
            zipper.flush();
            zipper.closeEntry();

            ze= new ZipEntry("images");
            zipper.putNextEntry(ze);
            serializer.writeObject(images);
            serializer.flush();
            zipper.closeEntry();

            ze= new ZipEntry("guilinks");
            zipper.putNextEntry(ze);
            serializer.writeObject(imgLinks);
            serializer.flush();
            zipper.closeEntry();

            zipper.close();
            serializer.close();

        }
        catch(IOException e)
        {
            e.printStackTrace();
            throw new BadCallException();
        }
    }

    /**
     * Charge un graphe pour l'interface GUI
     * @param filename le nom de fichier
     * @return {liens, appareils, ImageToSave, LinkToSave}
     */
    public static ArrayList<ArrayList<Object>> loadGUIGraph(String filename) throws BadCallException
    {
        FileInputStream reader;
        ZipInputStream zipper;
        ObjectInputStream deserializer;

        ArrayList<ArrayList<Object>> res = new ArrayList<>();
        res.add(null);
        res.add(null);
        res.add(null);
        res.add(null);

        File fl = new File(filename);

        if(!fl.exists() || fl.isDirectory())
            throw new BadCallException();

        try
        {

            reader = new FileInputStream(fl);
            zipper = new ZipInputStream(reader);
            ZipEntry ze = zipper.getNextEntry();
            deserializer = new ObjectInputStream(zipper);
            while(ze!=null)
            {
                if(ze.getName().equals("links"))
                {
                    res.set(0, (ArrayList<Object>) deserializer.readObject());
                }
                else if(ze.getName().equals("hard"))
                {
                    res.set(1, (ArrayList<Object>) deserializer.readObject());
                }
                else if(ze.getName().equals("images"))
                {
                    res.set(2, (ArrayList<Object>) deserializer.readObject());
                }
                else if(ze.getName().equals("guilinks"))
                {
                    res.set(3, (ArrayList<Object>) deserializer.readObject());
                }
                else if(ze.getName().equals("count"))
                {
                    DeviceManager.factory.setMacCount(zipper.read());
                }
                ze = zipper.getNextEntry();
            }

        }
        catch(IOException|ClassNotFoundException e)
        {
            e.printStackTrace();
            throw new BadCallException();
        }

        // Au cas où l'on a chargé des clients en pleine repetitiverequest, on les arrête
        for(Object i : res.get(1))
        {
            if(i instanceof AbstractClient)
                ((AbstractClient) i).stopRepetitiveRequest();
        }

        return res;
    }
}