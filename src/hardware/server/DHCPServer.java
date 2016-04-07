package hardware.server;

import enums.Bandwidth;
import enums.ClockSpeed;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import exceptions.OverflowException;
import packet.*;
import packet.data.DHCPData;
import table.DHCPTable;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Classe définisssant un serveur DHCP classique par ethernet (1G)
 * @author J. Desvignes
 */
public class DHCPServer extends AbstractServer
{

    /**
     * Les IPs déjà adressées
     */
    private ArrayList<IP> takenIPs;
    /**
     * La liste des transactions en cours, repérées par leur identifiant
     */
    private ArrayList<Integer> identifiers = new ArrayList<>();

    /**
     * La table d'adressage
     */
    private final DHCPTable DHCPtable = new DHCPTable();

    /**
     * Constructeur
     * @param MAC la MAC du serveur
     * @param IP l'IP du serveur
     * @param default_gateway la passerelle par défaut, en général un relai DHCP
     */
    public DHCPServer(int MAC, packet.IP IP, packet.IP default_gateway) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 30, MAC, IP, default_gateway, PacketTypes.DHCP, ClockSpeed.MEDIUM);
        takenIPs = new ArrayList<>();
        takenIPs.add(IP);
        setDHCPRelay(IP);
    }

    @Override
    protected void treatData(Packet p) throws BadCallException, OverflowException {
        //Si le paquet est une requête d'IP
        if((p.getType() == PacketTypes.DHCP) &&
            !(((DHCPData)p.getData()).getChosen() == null) && identifiers.contains(((DHCPData) p.getData()).identifier)
                && !((DHCPData)p.getData()).isACK())
        {
            if(!takenIPs.contains(((DHCPData)p.getData()).getChosen()))
            {
              takenIPs.add(((DHCPData)p.getData()).getChosen());
               ((DHCPData) p.getData()).setOK();
                if(((DHCPData)p.getData()).getSubnetInfo().get(0) != null)
                    p.dst_addr = ((DHCPData)p.getData()).getSubnetInfo().get(0);
                else
                    p.dst_addr = new IP(255,255,255,255);
               p.src_addr = this.IP;
               p.dst_mac = p.src_mac;
               p.src_mac = this.MAC;
               send(p, p.lastPort);
            }
        }
        //Si le paquet est un DHCPDISCOVER
        else if ((p.getType() == PacketTypes.DHCP) && !((DHCPData)p.getData()).isACK())
        {
            //System.out.println(this.IP + " : reçu DHCP de " + p.src_addr);
            ArrayList<IP> firstrelay = ((DHCPData)p.getData()).getSubnetInfo();
            IP[] range;
            if(firstrelay.get(1) != null)
                range = DHCPtable.gimmeARange(firstrelay.get(0).getSubnet(firstrelay.get(1)));
            else
            {
                range = DHCPtable.gimmeARange(new IP(0,0,0,0)); //TODO TEMPORAIRE !!
                ((DHCPData)p.getData()).setDHCPaddr(this.IP);
            }
            if(range[0].equals(new IP(0,0,0,0)) && range[0].equals(new IP(0,0,0,0))) //Pas de plage associée à ce sous-réseau
                return;
            ArrayList<IP> avail = new ArrayList<>();
            ArrayList<IP> subnetIPs = new ArrayList<>();

            for(int i=range[0].o1 ; i<=range[1].o1 ; i++)
                for(int j=range[0].o2 ; j<=range[1].o2 ; j++)
                    for(int k=range[0].o3 ; k<=range[1].o3 ; k++)
                        for(int l=range[0].o4 ; l<=range[1].o4 ; l++)
                            subnetIPs.add((new IP(i,j,k,l)));

            avail.addAll(subnetIPs.stream().filter(i -> !takenIPs.contains(i)).collect(Collectors.toList()));
            ((DHCPData)p.getData()).setRange(avail);
            p.src_addr = this.IP;
            p.src_mac = this.MAC;
            if(firstrelay.get(0) != null)
                p.dst_addr = firstrelay.get(0);
            else
                p.dst_addr = new IP(255,255,255,255);
            p.isResponse = true;
            this.send(p, p.lastPort);
            this.identifiers.add(((DHCPData) p.getData()).identifier);
        }
        //Si c'est l'ACK d'une machine
        else if((p.getType() == PacketTypes.DHCP) && ((DHCPData)p.getData()).isACK() 
        		&& identifiers.contains(((DHCPData)p.getData()).identifier))
        {
            identifiers.remove(identifiers.indexOf(((DHCPData) p.getData()).identifier));
            System.out.println("DHCP success !! "+p.src_addr);
        }
    }

    /**
     * Renvoie littéralement les plages
     * sous-réseau IPmin IPmax
     */
    public void addRange(IP subnet, IP min, IP max)
    {
        IP[] range = new IP[2];
        range[0] = min;
        range[1] = max;
        this.DHCPtable.addRange(subnet, range);
    }

    /**
     * Remplace les plages par le tableau fourni ; non destructif si erreur
     */
    public void setAllRanges(ArrayList<ArrayList<IP>> ranges) throws BadCallException
    {
        this.DHCPtable.setAllRanges(ranges);
    }

    public ArrayList<ArrayList<IP>> getAllRanges()
    {
        return this.DHCPtable.getAllRanges();
    }
}