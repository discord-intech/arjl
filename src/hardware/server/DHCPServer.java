package hardware.server;

import enums.Bandwidth;
import enums.LinkTypes;
import enums.PacketTypes;
import exceptions.BadCallException;
import packet.*;
import packet.data.DHCPData;

import java.util.ArrayList;

public class DHCPServer extends AbstractServer
{

    ArrayList<IP> takenIPs;
    ArrayList<Integer> identifiers = new ArrayList<>();

    public DHCPServer(int MAC, packet.IP IP, packet.IP default_gateway, int default_port) throws BadCallException {
        super(LinkTypes.ETH, Bandwidth.ETH_1G, 30, MAC, IP, default_gateway, default_port, PacketTypes.DHCP);
        takenIPs = new ArrayList<IP>();
        takenIPs.add(IP);
    }

    @Override
    protected void treatData(Packet p) throws BadCallException
    {
        if ((this.type == PacketTypes.DHCP) && (p.getType() == PacketTypes.DHCP) && ((DHCPData)p.getData()).getChosen()==null)
        {
            //System.out.println(this.IP + " : reçu DHCP de " + p.src_addr);
            ArrayList<IP> firstrelay = ((DHCPData)p.getData()).getSubnetInfo();
            ArrayList<IP> subnetIPs = packet.IP.getSubnetIPs(firstrelay.get(0).getSubnet(firstrelay.get(1)));
            ArrayList<IP> avail = new ArrayList<packet.IP>();
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
        else if((this.type == PacketTypes.DHCP) && (p.getType() == PacketTypes.DHCP) &&
                !(((DHCPData)p.getData()).getChosen() == null) && identifiers.contains(((DHCPData) p.getData()).identifier))
        {
            if(!takenIPs.contains(((DHCPData)p.getData()).getChosen()))
            {
                takenIPs.add(((DHCPData)p.getData()).getChosen());
                ((DHCPData) p.getData()).setOK();
                p.dst_addr = ((DHCPData)p.getData()).getChosen();
                p.src_addr = this.IP;
                p.dst_mac = p.src_mac;
                p.src_mac = this.MAC;
                send(p, p.lastPort);
            }
        }
        else if((this.type == PacketTypes.DHCP) && (p.getType() == PacketTypes.DHCP) && ((DHCPData)p.getData()).isACK())
        {
            identifiers.remove(((DHCPData) p.getData()).identifier);
            System.out.println("DHCP success !! "+p.src_addr);
        }
    }
}