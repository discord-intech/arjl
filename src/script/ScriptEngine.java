package script;

import actions.Actions;
import enums.Hardware;
import enums.LinkTypes;
import enums.Order;
import enums.PacketTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import hardware.AbstractHardware;
import hardware.Link;
import hardware.client.AbstractClient;
import hardware.router.AbstractRouter;
import hardware.server.AbstractServer;
import hardware.server.DHCPServer;
import packet.IP;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Classe de gestion de l'interface textuelle
 * Principalement constituée de boucles absorbantes
 * @author J. Desvignes
 **/
@SuppressWarnings("deprecation")
public class ScriptEngine extends Thread
{

    /** Ces trois attributs forment le graphe */
    private ArrayList<AbstractHardware> hardwares = new ArrayList<>();
    private ArrayList<String> hardwareNames = new ArrayList<>();
    private ArrayList<Link> links = new ArrayList<>();

    private Scanner input = new Scanner(System.in);
    private String userInput;

    private ArrayList<String> orders = new ArrayList<>();

    private ArrayList<AbstractHardware> paused = new ArrayList<>();

    private boolean treatingScript = false;

    private BufferedReader scriptReader;

    @Override
    public void run()
    {
        System.out.println("Inteface NO-GUI de Network Simulator 2016 lancée");
        while(true)
        {

            if(treatingScript)
            {
                try
                {
                    userInput = scriptReader.readLine();
                }
                catch (IOException e)
                {
                    this.treatingScript = false;
                    System.out.println();
                    System.out.println("Fin du script");
                    try {
                        scriptReader.close();
                        scriptReader = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                if(userInput == null)
                {
                    this.treatingScript = false;
                    System.out.println();
                    System.out.println("Fin du script");
                    try {
                        scriptReader.close();
                        scriptReader = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
            }
            else
            {
                System.out.println();
                System.out.print("-> ");
                userInput = input.nextLine();
            }

            if(userInput.replaceAll(" ", "").equals("")) //Si il n'a rien envoyé
                continue;

            orders.clear();
            for(String i : userInput.split(" "))
                orders.add(i.toLowerCase().replaceAll(" ", ""));

            if(orders.get(0).equals(Order.EXIT.order))
            {
                System.out.println("Arrêt du simulateur");
                for(AbstractHardware hard : hardwares)
                    hard.stop();
                break;
            }
            else if(orders.get(0).equals(Order.SPEED.order))
            {
                if(orders.size() != 2)
                    System.out.println("BAD ARGS : "+Order.SPEED.order+" <vitesse [0.1 ; 100]>");
                else
                {
                    try
                    {
                        AbstractHardware.setSpeed(Float.parseFloat(orders.get(1)));
                    }
                    catch(BadCallException|NumberFormatException e)
                    {
                        System.out.println("Mauvaise vitesse");
                    }
                }
            }
            else if(orders.get(0).equals(Order.STARTALL.order))
            {
                for(int i=0 ; i<hardwares.size() ; i++)
                {
                    if (paused.isEmpty()) {
                        System.out.println(hardwareNames.get(i) + " lancé");
                        if(!hardwares.get(i).isAlive())
                            hardwares.get(i).start();
                        hardwares.get(i).changeState(true);
                    } else {
                        System.out.println(hardwareNames.get(i) + " lancé mais simulation en pause");
                        if(!hardwares.get(i).isAlive())
                            hardwares.get(i).start();
                        hardwares.get(i).changeState(false);
                        paused.add(hardwares.get(i));
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(orders.get(0).equals(Order.READSCRIPT.order))
            {
                if(orders.size() != 2)
                {
                    System.out.println("BAD ARGS : "+Order.READSCRIPT.order+" <cheminFichier>");
                    continue;
                }
                try
                {
                    scriptReader = new BufferedReader(new FileReader(orders.get(1)));
                }
                catch (FileNotFoundException e)
                {
                    System.out.println("Fichier introuvable");
                    continue;
                }
                System.out.println("Script trouvé, lecture et exécution lancée");
                System.out.println();
                this.treatingScript = true;
            }
            else if(orders.get(0).equals(Order.PIPO.order))
            {
                System.out.println("El Pipo del Fuego a la playa !");
                continue;
            }
            else if(orders.get(0).equals(Order.SAVE.order))
            {
                if(orders.size() != 2)
                {
                    System.out.println("BAD ARGS : "+Order.SAVE.order+" <nomDeSauvegarde>");
                    continue;
                }
                try
                {
                    Actions.saveNOGUIGraph(orders.get(1), hardwares, hardwareNames, links);
                    System.out.println("Sauvegarde réussie !");
                }
                catch (BadCallException e)
                {
                    System.out.println("Erreur de sauvegarde");
                    e.printStackTrace();
                }
            }
            else if(orders.get(0).equals(Order.LOAD.order))
            {
                ArrayList<ArrayList<Object>> save;
                if (orders.size() != 2) {
                    System.out.println("BAD ARGS : " + Order.LOAD.order + " <nomDeSauvegarde>");
                    continue;
                }
                try
                {
                    save = Actions.loadNOGUIGraph(orders.get(1));
                }
                catch (BadCallException e)
                {
                    System.out.println("Erreur de lecture");
                    e.printStackTrace();
                    continue;
                }

                for(Object i : save.get(0))
                    links.add((Link)i);
                for(Object i : save.get(1))
                    hardwares.add((AbstractHardware) i);
                for(Object i : save.get(2))
                    hardwareNames.add((String)i);
                System.out.println("Chargement réussi !");
            }
            else if(orders.get(0).equals(Order.REMOVE.order))
            {
                if(orders.size() != 2)
                    System.out.println("BAD ARGS : "+Order.REMOVE.order+" <surnomAppareil>");
                else
                    this.remove();
            }
            else if(orders.get(0).equals(Order.ADD.order))
            {
                if(orders.size() != 3)
                    System.out.println("BAD ARGS : "+Order.ADD.order+" <nomAppareil> <surnom>");
                else
                    this.add();
            }
            else if(orders.get(0).equals(Order.GO.order))
            {
                this.go();
            }
            else if(orders.get(0).equals(Order.PAUSE.order))
            {
                this.pause();
            }
            else if(orders.get(0).equals(Order.LINK.order))
            {
                if(orders.size() != 4)
                    System.out.println("BAD ARGS : "+Order.LINK.order+" <surnomAppareil1> <surnomAppareil2> <typeLien>");
                else
                    this.link();
            }
            else if(orders.get(0).equals(Order.EDIT.order))
            {
                if(orders.size() != 2)
                    System.out.println("BAD ARGS : "+Order.EDIT.order+" <surnomAppareil>");
                else
                    this.edit();
            }
            else
            {
                System.out.println("Commande inconnue");
            }
        }

    }

    private void go()
    {
        if(paused.isEmpty())
        {
            System.out.println("La simulation n'est pas en pause");
            return;
        }
        for(AbstractHardware i : paused)
        {
            i.changeState(true);
        }
        paused.clear();
        System.out.println("Simulation relancée");
    }

    private void pause()
    {
        if(hardwares.isEmpty())
        {
            System.out.println("Aucun appareil instancié");
            return;
        }
        for(AbstractHardware i : hardwares)
        {
            if(i.isRunning())
            {
                i.changeState(false);
                paused.add(i);
            }
        }
        if(paused.isEmpty())
        {
            System.out.println("Aucun appareil d'allumé, la simulation n'est pas lancée");
        }
        else
        {
            System.out.println("Simulation mise en pause, "+paused.size()+" appareil(s) arrêté(s)");
        }
    }

    private void add()
    {
        for(String i : hardwareNames)
            if(i.equals(orders.get(2)))
            {
                System.out.println("Nom d'appareil déjà utilisé");
                return;
            }

        try
        {
            hardwares.add(Hardware.getHardware(orders.get(1)));
            hardwareNames.add(orders.get(2));
        }
        catch (BadCallException e)
        {
            System.out.println(orders.get(1)+" n'est pas un type d'appareil existant");
            return;
        }
        System.out.println(Hardware.getName(hardwares.get(hardwares.size()-1))+" ajouté sous le surnom "+orders.get(2));
        hardwares.get(hardwares.size()-1).start();
    }

    private void remove()
    {
        for(String i : hardwareNames)
        {
            if(i.equals(orders.get(1)))
            {
                int index = hardwareNames.indexOf(i);
                hardwares.get(index).stop();
                ArrayList<Link> connected = hardwares.get(index).getConnectedLinks();
                for(Link j : connected)
                {
                    if(j != null && links.contains(j))
                    {
                        links.remove(j);
                        Actions.disconnect(j);
                    }
                }
                hardwares.remove(index);
                hardwareNames.remove(index);
                System.out.println(orders.get(1)+" supprimé et lien(s) déconnecté(s)");
                return;
            }
        }
        System.out.println("Pas d'appareil avec ce nom");
    }

    private void link()
    {
        AbstractHardware hard1 = null;
        AbstractHardware hard2 = null;
        LinkTypes type;

        for(String i : hardwareNames)
        {
            if (i.equals(orders.get(1)))
            {
                hard1 = hardwares.get(hardwareNames.indexOf(i));
            }
            else if (i.equals(orders.get(2)))
            {
                hard2 = hardwares.get(hardwareNames.indexOf(i));
            }
        }

        if(hard1 == null || hard2 == null)
        {
            System.out.println("Pas d'appareil avec ce nom");
            return;
        }

        if(hard1 == hard2)
        {
            System.out.println("Impossible de connecter un appareil avec lui-même");
            return;
        }

        if((type = LinkTypes.getType(orders.get(3))) == null)
        {
            System.out.println("Mauvais type de lien");
            return;
        }

        try
        {
            links.add(Actions.connect(hard1, hard2, type));
        }
        catch(BadCallException e)
        {
            System.out.println("Pas de port libre de ce type sur l'un des appareils");
        }
        System.out.println(orders.get(1)+" et "+orders.get(2)+" connectés avec un lien "+type.name());
    }

    private void edit()
    {
        String nomAppareil = orders.get(1);
        AbstractHardware hard = null;
        for(String i : hardwareNames)
        {
            if (i.equals(nomAppareil))
            {
                hard = hardwares.get(hardwareNames.indexOf(i));
            }
        }

        if(hard == null)
        {
            System.out.println("Aucun appareil avec ce nom");
            return;
        }

        System.out.println("Entrée en mode éditeur pour l'appareil "+nomAppareil);

        while(true)
        {
            if(treatingScript)
            {
                try
                {
                    userInput = scriptReader.readLine();
                }
                catch (IOException e)
                {
                    this.treatingScript = false;
                    System.out.println();
                    System.out.println("Fin du script");
                    try {
                        scriptReader.close();
                        scriptReader = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                if(userInput == null)
                {
                    this.treatingScript = false;
                    System.out.println();
                    System.out.println("Fin du script");
                    try {
                        scriptReader.close();
                        scriptReader = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
            }
            else
            {
                System.out.println();
                System.out.print(nomAppareil+" -> ");
                userInput = input.nextLine();
            }

            if(userInput.replaceAll(" ", "").equals("")) //Si il n'a rien envoyé
                continue;

            orders.clear();
            for(String i : userInput.split(" "))
                orders.add(i.toLowerCase().replaceAll(" ", ""));

            if(orders.get(0).equals(Order.EXIT.order))
            {
                return;
            }
            else if(orders.get(0).equals(Order.START.order))
            {
                if(paused.isEmpty())
                {
                    System.out.println(nomAppareil + " lancé");
                    hard.changeState(true);
                }
                else
                {
                    System.out.println(nomAppareil + " lancé mais simulation en pause");
                    hard.changeState(false);
                    paused.add(hard);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(orders.get(0).equals(Order.STOP.order))
            {
                System.out.println(nomAppareil+" arrêté");
                hard.changeState(false);
            }
            else if(orders.get(0).equals(Order.DHCP.order))
            {
                int port;
                if(orders.size() != 2)
                {
                    System.out.println("BAD ARGS : "+Order.DHCP.order+" <port>");
                    continue;
                }
                if(!(hard instanceof AbstractClient || hard instanceof AbstractServer) || hard instanceof DHCPServer)
                {
                    System.out.println("Cet appareil ne supporte pas la requête DHCP");
                }
                try
                {
                    port = Integer.parseInt(orders.get(1));
                    if(port < 0 || port > hard.getConnectedLinks().size())
                    {
                        System.out.println("Mauvais port");
                        continue;
                    }
                }
                catch (NumberFormatException e)
                {
                    System.out.println("Mauvais port");
                    continue;
                }
                System.out.println(nomAppareil+" lance une requête DHCP");
                try
                {
                    ((AbstractRouter)hard).DHCPClient(port);
                } catch (BadCallException|OverflowException e)
                {
                    //TODO traitement de l'overflow
                    e.printStackTrace();
                }
            }
            else if(orders.get(0).equals(Order.REPETITIVEREQUEST.order))
            {
                PacketTypes type;
                IP serv;
                int wait;
                if(orders.size() != 4)
                {
                    System.out.println("BAD ARGS : "+Order.REPETITIVEREQUEST.order+" <type> <IPserveur> <tempsD'attente>");
                    continue;
                }
                if(!(hard instanceof AbstractClient))
                {
                    System.out.println(nomAppareil+" n'est pas un client");
                    continue;
                }
                if(orders.get(1).equals("null") || (type = PacketTypes.getType(orders.get(1))) == null)
                {
                    System.out.println("Mauvais type de packet");
                    return;
                }
                try
                {
                    serv = IP.stringToIP(orders.get(2));
                }
                catch (BadCallException e)
                {
                    System.out.println("Mauvaise IP entrée");
                    return;
                }
                try
                {
                    wait = Integer.parseInt(orders.get(3));
                    if(wait<0)
                    {
                        System.out.println("Mauvaise valeur d'attente");
                        return;
                    }
                }
                catch (NumberFormatException e)
                {
                    System.out.println("Mauvaise valeur d'attente");
                    return;
                }
                System.out.println(nomAppareil+" lance une requête répétitive "+orders.get(1)+" vers "+serv.toString());
                ((AbstractClient)hard).setRepetitiveRequest(type, serv, wait);
            }
            else if(orders.get(0).equals(Order.ADDRANGE.order))
            {
                IP sub;
                IP min;
                IP max;
                if(orders.size() != 4)
                {
                    System.out.println("BAD ARGS : "+Order.ADDRANGE.order+" <IPsubnet> <IPmin> <IPmax>");
                    continue;
                }
                if(!(hard instanceof DHCPServer))
                {
                    System.out.println("Réservé aux serveurs DHCP");
                }
                try
                {
                    sub = IP.stringToIP(orders.get(1));
                    min = IP.stringToIP(orders.get(2));
                    max = IP.stringToIP(orders.get(3));
                }
                catch (BadCallException e)
                {
                    System.out.println("Mauvaise(s) IP(s)");
                    continue;
                }
                ((DHCPServer)hard).addRange(sub, min, max);
                System.out.println("Plage IP ajoutée pour "+sub.toString());
            }
            else if(orders.get(0).equals(Order.DHCPRELAY.order))
            {
                if(!(orders.size() == 3 || orders.size() == 2) && !(orders.get(1).equals("on") || orders.get(1).equals("off")))
                    System.out.println("BAD ARGS : "+Order.DHCPRELAY.order+" <on/off> <IPserveurDHCP>");
                else
                {
                    if(!(hard instanceof AbstractRouter))
                    {
                        System.out.println(nomAppareil+" n'est pas un routeur");
                        continue;
                    }

                    if(orders.get(1).equals("off"))
                    {
                        ((AbstractRouter)hard).stopDHCPRelay();
                        System.out.println("Service relai DHCP arrêté");
                    }
                    else
                    {
                        IP dhcpserv;
                        try {
                            dhcpserv = IP.stringToIP(orders.get(2));
                        } catch (BadCallException e) {
                            System.out.println("Le second paramètre n'est pas une IP correcte");
                            continue;
                        }
                        System.out.println("Service relai DHCP lancé pour "+dhcpserv.toString());
                        ((AbstractRouter)hard).setDHCPRelay(dhcpserv);
                    }
                }
            }
            else if(orders.get(0).equals(Order.REQUEST.order))
            {
                PacketTypes type;
                IP serv;
                if(orders.size() != 3)
                {
                    System.out.println("BAD ARGS : "+Order.REQUEST.order+" <type> <IPserveur>");
                    continue;
                }
                if(!(hard instanceof AbstractClient))
                {
                    System.out.println(nomAppareil+" n'est pas un client");
                    continue;
                }
                if(orders.get(1).equals("null") || (type = PacketTypes.getType(orders.get(1))) == null)
                {
                    System.out.println("Mauvais type de packet");
                    return;
                }
                try
                {
                    serv = IP.stringToIP(orders.get(2));
                }
                catch (BadCallException e)
                {
                    System.out.println("Mauvaise IP entrée");
                    return;
                }
                System.out.println(nomAppareil+" lance une requête "+orders.get(1)+" vers "+serv.toString());
                ((AbstractClient)hard).launchRequest(type, serv);
            }
            else if(orders.get(0).equals(Order.VERBOSE.order))
            {
                if(orders.size() != 2 && !(orders.get(1).equals("on") || orders.get(1).equals("off")))
                {
                    System.out.println("BAD ARGS : "+Order.VERBOSE.order+" <on/off>");
                    continue;
                }
                if(orders.get(1).equals("off"))
                {
                    hard.stopVerbose();
                    System.out.println("Verbose désactivé");
                }
                else
                {
                    hard.setVerbose(nomAppareil);
                    System.out.println("Verbose activé");
                }

            }
            else if(orders.get(0).equals(Order.PRINT.order))
            {
                try {
                    hard.print();
                } catch (BadCallException e) {
                    e.printStackTrace();
                }
            }
            else if(orders.get(0).equals(Order.PRINTROUTES.order))
            {
                if((hard instanceof AbstractClient || hard instanceof AbstractServer) || !(hard instanceof AbstractRouter))
                {
                    System.out.println("Réservé aux routeurs");
                    continue;
                }
                ((AbstractRouter)hard).printRoutes();
            }
            else if(orders.get(0).equals(Order.ADDROUTE.order))
            {
                IP mask, sub, gate;
                int port;
                if(orders.size() != 5)
                {
                    System.out.println("BAD ARGS : "+Order.ADDROUTE.order+" <IPsousréseau> <masque> <IPpasserelle> <noInterface>");
                    continue;
                }
                if(!(hard instanceof AbstractRouter))
                {
                    System.out.println("Réservé aux routeurs");
                    continue;
                }
                try
                {
                    sub = IP.stringToIP(orders.get(1));
                    mask = IP.stringToIP(orders.get(2));
                    gate = IP.stringToIP(orders.get(3));
                }
                catch (BadCallException e)
                {
                    System.out.println("Mauvaise(s) IP(s)/masque");
                    continue;
                }
                try
                {
                    port = Integer.parseInt(orders.get(4));
                    if(port < 0 || port > hard.getConnectedLinks().size())
                    {
                        System.out.println("Mauvais port");
                        continue;
                    }
                }
                catch (NumberFormatException e)
                {
                    System.out.println("Mauvais port");
                    continue;
                }
                try {
                    ((AbstractRouter)hard).addRoutingRule(port, sub, mask, gate);
                } catch (BadCallException e) {
                    e.printStackTrace();
                }
                System.out.println("Règle ajoutée.");
            }
            else if(orders.get(0).equals(Order.CLEARROUTES.order))
            {
                if(!(hard instanceof AbstractRouter))
                {
                    System.out.println("Réservé aux routeurs");
                    continue;
                }
                ((AbstractRouter)hard).clearRoutes();
            }
            else if(orders.get(0).equals(Order.CONFIG.order))
            {
                int port;
                if(orders.size() != 2)
                {
                    System.out.println("BAD ARGS : "+Order.CONFIG.order+" <numPort>");
                    continue;
                }
                try
                {
                    orders.get(1);
                    port = Integer.parseInt(orders.get(1));
                }
                catch (NumberFormatException e)
                {
                    System.out.println("Port inexistant");
                    continue;
                }
                if(port < 0 || port+1 > hard.getConnectedLinks().size())
                {
                    System.out.println("Port inexistant");
                    continue;
                }
                if(!(hard instanceof AbstractRouter))
                {
                    System.out.println("Appareil non configurable");
                    continue;
                }
                this.config((AbstractRouter) hard, nomAppareil ,port);

            }
            else if(orders.get(0).equals(Order.GATEWAY.order)) //Seulement client/serv
            {
                if(orders.size() != 2)
                {
                    System.out.println("BAD ARGS : "+Order.GATEWAY.order+" <ipPasserelle>");
                    continue;
                }
                if(!(hard instanceof AbstractClient || hard instanceof AbstractServer))
                {
                    System.out.println("Seul un client ou un serveur ont juste une passerelle par défaut");
                    System.out.println("Pour un routeur, entrez dans la config du port et faîtes "+Order.DEFAULTROUTE.order);
                    continue;
                }
                try
                {
                    ((AbstractRouter)hard).configureGateway(IP.stringToIP(orders.get(1)));
                }
                catch (BadCallException e)
                {
                    System.out.println("Mauvaise passerelle");
                    continue;
                }
            }
            else
            {
                System.out.println("Commande inconnue");
            }
        }
    }

    private void config(AbstractRouter hard, String nomAppareil, int port)
    {
        while(true)
        {
            if(treatingScript)
            {
                try
                {
                    userInput = scriptReader.readLine();
                }
                catch (IOException e)
                {
                    this.treatingScript = false;
                    System.out.println();
                    System.out.println("Fin du script");
                    try {
                        scriptReader.close();
                        scriptReader = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
                if(userInput == null)
                {
                    this.treatingScript = false;
                    System.out.println();
                    System.out.println("Fin du script");
                    try {
                        scriptReader.close();
                        scriptReader = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                }
            }
            else
            {
                System.out.println();
                System.out.print(nomAppareil + ":" + port + " -> ");
                userInput = input.nextLine();
            }

            if (userInput.replaceAll(" ", "").equals("")) //Si il n'a rien envoyé
                continue;

            orders.clear();
            for (String i : userInput.split(" "))
                orders.add(i.toLowerCase().replaceAll(" ", ""));

            if (orders.get(0).equals(Order.EXIT.order))
            {
                return;
            }
            else if(orders.get(0).equals(Order.IP.order))
            {
                if(orders.size() != 2)
                {
                    System.out.println("BAD ARGS : "+Order.IP.order+" <ipInterface>");
                    continue;
                }
                try
                {
                    hard.configureIP(port, IP.stringToIP(orders.get(1)));
                }
                catch (BadCallException e)
                {
                    System.out.println("Mauvaise IP");
                    continue;
                }
            }
            else if(orders.get(0).equals(Order.DEFAULTROUTE.order)) //Seulement routeur
            {
                if(orders.size() != 2)
                {
                    System.out.println("BAD ARGS : "+Order.DEFAULTROUTE.order+" <ipPasserelle>");
                    continue;
                }
                if(hard instanceof AbstractClient || hard instanceof AbstractServer)
                {
                    System.out.println("Ne peut être configuré que sur un routeur");
                    continue;
                }
                try
                {
                    hard.configureDefaultRoute(port, IP.stringToIP(orders.get(1)));
                }
                catch (BadCallException e)
                {
                    System.out.println("Mauvaise passerelle");
                    continue;
                }
            }
        }
    }
}
