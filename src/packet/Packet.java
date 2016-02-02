package packet;

import enums.PacketTypes;
import packet.data.DHCPData;

import java.util.Random;

public class Packet
{
    /** IP et MAC du destinataire */
    public IP dst_addr;
    public int dst_mac;

    /** IP et MAC de l'envoyeur */
    public IP src_addr;
    public int src_mac;

    /** Variable TTL du paquet */
    private int TTL=128;

    /** Utilisé pour création d'identifiants */
    public static final Random RNG = new Random(System.currentTimeMillis());

    /** Prochain routeur */
    private IP NHR;

    /** S'il faut afficher dans la console son chemin pris (un tracert en somme)*/
    public boolean tracked = false;

    /** Contenu du paquet */
    private Object data; // Permet de stocker une objet dans le paquet

    /**
     * Permet au système de traitement des appareils de savoir par quel port est entré ce paquet (fictif, juste utile
     * pour la redirection et le broadcast)
     */
    public int lastPort;

    /** Type de paquet */
    private final PacketTypes type;

    /** Si c'est une réponse (pas utilisé par tous les protocoles) */
    public boolean isResponse;

    /** Utilisé par les appareils pour reconaître un paquet déjà traité mais en attente de libération de la bande passante*/
    public boolean alreadyTreated=false;

    /** Utilisé pour la gestion de la bande passante*/
    public int destinationPort;

    /**
     *  Constructeur du paquet
     * @param dst_addr adresse de destination
     * @param src_addr adresse de l'envoyeur
     * @param src_mac MAC de l'envoyeur
     * @param dst_mac MAC du destinataire (-1 si inconnue)
     * @param type type de paquet
     * @param isResponse si c'est une réponse
     */
    public Packet(IP dst_addr, IP src_addr, int src_mac, int dst_mac, PacketTypes type, boolean isResponse)
    {
        this.dst_addr=dst_addr;
        this.src_addr=src_addr;
        this.dst_mac=dst_mac;
        this.src_mac=src_mac;
        this.type=type;
        this.isResponse = isResponse;
        this.NHR = src_addr;

        if(type == PacketTypes.DHCP)
            this.data = new DHCPData(Packet.RNG.nextInt(65533), src_mac);
    }

    /**
     * Constructeur de paquet en copiant un autre
     * @param p paquet à copier
     */
    public Packet(Packet p)
    {
        this.dst_addr=p.dst_addr;
        this.src_addr=p.src_addr;
        this.dst_mac=p.dst_mac;
        this.src_mac=p.src_mac;
        this.type=p.type;
        this.isResponse = p.isResponse;
        this.NHR = p.getNHR();
        this.tracked=p.tracked;
        this.data=p.getData();
        this.TTL = p.getTTL();

    }

    /**
     *  Constructeur du paquet
     * @param dst_addr adresse de destination
     * @param src_addr adresse de l'envoyeur
     * @param src_mac MAC de l'envoyeur
     * @param dst_mac MAC du destinataire (-1 si inconnue)
     * @param type type de paquet
     * @param isResponse si c'est une réponse
     * @param tracked si le paquet est suivi
     */
    public Packet(IP dst_addr, IP src_addr, int src_mac, int dst_mac, PacketTypes type, boolean isResponse, boolean tracked)
    {
        this.dst_addr=dst_addr;
        this.src_addr=src_addr;
        this.dst_mac=dst_mac;
        this.src_mac=src_mac;
        this.type=type;
        this.isResponse = isResponse;
        this.NHR = src_addr;
        this.tracked=tracked;

        if(type == PacketTypes.DHCP)
            this.data = new DHCPData(Packet.RNG.nextInt(655335478), src_mac);
    }

    /**
     * Constructeur de paquet en copiant un autre
     * @param p paquet à copier
     * @param tracked si le paquet est suivi
     */
    public Packet(Packet p, boolean tracked)
    {
        this.dst_addr=p.dst_addr;
        this.src_addr=p.src_addr;
        this.dst_mac=p.dst_mac;
        this.src_mac=p.src_mac;
        this.type=p.type;
        this.isResponse = p.isResponse;
        this.NHR = p.getNHR();
        this.tracked=tracked;
        this.data=p.getData();
        this.TTL = p.getTTL();
    }

    /**
     * Réduit le TTL de 1
     * @return true s'il a atteint 0, false sinon
     */
    public boolean TTLdown()
    {
        TTL--;
        return TTL == 0;
    }

    /**
     * ========================
     *   GETTERS ET SETTERS
     * ========================
     */

    public PacketTypes getType() {
        return type;
    }

    public IP getNHR() {
        return NHR;
    }

    public void setNHR(IP nhr)
    {
        this.NHR = nhr;
    }

    public int getTTL()
    {
        return TTL;
    }

    public Object getData()
    {
        return this.data;
    }

    public void setData(Object data)
    {
        this.data = data;
    }

}