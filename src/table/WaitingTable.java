package table;

import enums.PacketTypes;
import packet.IP;

import java.util.ArrayList;

/**
 * Cette table représente les attentes d'un client en terme de réponse d'un serveur
 * Elle représente les transactions en attente et le temps écoulé depuis l'envoi de la demande
 * Elle est surtout utile pour représenter le "TimeOut" d'une requête
 */
public class WaitingTable
{
    /** Représente le type de requète envoyé */
    private final ArrayList<PacketTypes> types = new ArrayList<>();

    /** L'adresse IP du serveur dont on attends la réponse*/
    private final ArrayList<IP> servers = new ArrayList<>();

    /** Le temps écoulé */
    private final ArrayList<Integer> timing = new ArrayList<>();

    /**
     * Nombres de paquets attendus restants
     */
    private final ArrayList<Integer> numberOfPackets = new ArrayList<>();

    /**
     * Supprime les attentes ayant timeout
     * Incrémente les autres attentes de 1
     * @return true s'il y a eu un timeout, false sinon
     */
    public boolean isThereATimeout()
    {
        boolean res = false;

        for(int i=0 ; i<types.size() ; i++)
        {
            if(types.get(i).time < timing.get(i))
            {
                timing.remove(i);
                types.remove(i);
                servers.remove(i);
                numberOfPackets.remove(i);
                i--;
                res = true;
                continue;
            }
            this.timing.set(i, timing.get(i)+1);
        }
        return res;
    }

    /**
     * Ajoute une attente
     * @param type le type de paquet en attente
     * @param dst le serveur dont on attend la réponse
     */
    public void addWaiting(PacketTypes type, IP dst)
    {
        this.types.add(type);
        this.servers.add(dst);
        this.timing.add(0);
        this.numberOfPackets.add(type.size);
    }

    /**
     * Supprime une attente (réponse reçue)
     * @param type le type reçu
     * @param dst le serveur répondant
     */
    public void removeWaiting(PacketTypes type, IP dst)
    {
        for(int i=0 ; i<types.size() ; i++)
        {
            if(types.get(i) == type && servers.get(i).equals(dst))
            {
                types.remove(i);
                servers.remove(i);
                timing.remove(i);
                numberOfPackets.remove(i);
                return;
            }
        }
    }

    /**
     * Es-ce que ce j'attends une réponse de ce serveur?
     * Décrémente le nombre de paquets attendus si oui
     * Si ce dernier atteind0, la requête est supprimée
     * @param type le type reçu
     * @param dst le serveur ayant répondu
     * @return true si oui, false sinon
     */
    public boolean doIWaitForIt(PacketTypes type, IP dst)
    {
        for(int i=0 ; i<types.size() ; i++)
        {
            if(types.get(i) == type && servers.get(i).equals(dst))
            {
                numberOfPackets.set(i, numberOfPackets.get(i)-1);
                if(numberOfPackets.get(i) == 0)
                    this.removeWaiting(type, dst);
                return true;
            }
        }
        return false;
    }

    /**
     * Es-ce que j'attends une réponse ?
     */
    public boolean AmIWaitingForSomething()
    {
        return types.isEmpty();
    }

}