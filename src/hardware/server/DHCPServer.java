package hardware.server;

import enums.Bandwidth;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import packet.*;
import packet.data.DHCPData;
import table.DHCPTable;

import java.util.ArrayList;

/**
 * Classe définisssant un serveur DHCP classique par ethernet (1G)
 */
public class DHCPServer extends AbstractServer
{

    /**
     * Les IPs déjà adressées
     */
    ArrayList<IP> takenIPs;
    /**
     * La liste des transactions en cours, repérées par leur identifiant
     */
    ArrayList<Integer> identifiers = new ArrayList<>();

    /**
     * La table d'adressage
     */
    DHCPTable DHCPtable = new DHCPTable();

    /**
     * Constructeur
     * @param MAC la MAC du serveur
     * @param IP l'IP du serveur
     * @param default_gateway la passerelle par défaut, en général un relai DHCP
     */
    public DHCPServer(int MAC, packet.IP IP, packet.IP default_gateway) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 30, MAC, IP, default_gateway, 0, PacketTypes.DHCP);
        takenIPs = new ArrayList<IP>();
        takenIPs.add(IP);
        setDHCPRelay(IP);
    }

    @Override
    protected void treatData(Packet p) throws BadCallException
    {
    	//Si le paquet est un DHCPDISCOVER
        if((p.getType() == PacketTypes.DHCP) &&
            !(((DHCPData)p.getData()).getChosen() == null) && identifiers.contains(((DHCPData) p.getData()).identifier)
                && !((DHCPData)p.getData()).isACK())
        {
            if(!takenIPs.contains(((DHCPData)p.getData()).getChosen()))
            {
              takenIPs.add(((DHCPData)p.getData()).getChosen());
               ((DHCPData) p.getData()).setOK();
               p.dst_addr = ((DHCPData)p.getData()).getSubnetInfo().get(0);
               p.src_addr = this.IP;
               p.dst_mac = p.src_mac;
               p.src_mac = this.MAC;
               send(p, p.lastPort);
            }
        }
        //Si le paquet est une requête d'IP
        else if ((p.getType() == PacketTypes.DHCP) && !((DHCPData)p.getData()).isACK())
        {
            //System.out.println(this.IP + " : reçu DHCP de " + p.src_addr);
            ArrayList<IP> firstrelay = ((DHCPData)p.getData()).getSubnetInfo();
            IP[] range = DHCPtable.gimmeARange(firstrelay.get(0).getSubnet(firstrelay.get(1)), firstrelay.get(1));
            ArrayList<IP> avail = new ArrayList<packet.IP>();
            ArrayList<IP> subnetIPs = new ArrayList<>();

            for(int i=range[0].o1 ; i<=range[1].o1 ; i++)
                for(int j=range[0].o2 ; j<=range[1].o2 ; j++)
                    for(int k=range[0].o3 ; k<=range[1].o3 ; k++)
                        for(int l=range[0].o4 ; l<=range[1].o4 ; l++)
                            subnetIPs.add((new IP(i,j,k,l)));

            for(IP i : subnetIPs)
            {
                if(!takenIPs.contains(i))
                    avail.add(i);
            }
            ((DHCPData)p.getData()).setRange(avail);
            p.src_addr = this.IP;
            p.dst_addr = firstrelay.get(0);
            p.isResponse = true;
            this.send(p, p.lastPort);
            this.identifiers.add(((DHCPData) p.getData()).identifier);
        }
        //Si c'est l'ACK d'une machine
        else if((p.getType() == PacketTypes.DHCP) && ((DHCPData)p.getData()).isACK() 
        		&& identifiers.contains(((DHCPData)p.getData()).identifier)))
        {
            identifiers.remove(identifiers.indexOf(((DHCPData) p.getData()).identifier));
            System.out.println("DHCP success !! "+p.src_addr);
        }
    }

    /**
     * Ajoute une plage adressable
     * @param subnet le sous-réseau associé
     * @param min IP mini
     * @param max IP maxi
     */
    public void addRange(IP subnet, IP min, IP max)
    {
        IP[] range = new IP[2];
        range[0] = min;
        range[1] = max;
        this.DHCPtable.addRange(subnet, range);
    }
}