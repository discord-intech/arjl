package packet.data;


import packet.IP;

import java.util.ArrayList;

public class DHCPData
{
    private ArrayList<IP> range = new ArrayList<>();
    private IP subnetMask = null;
    public int identifier;
    private IP firstRelay;
    private IP chosen = null;
    private boolean ok=false;
    private boolean ack=false;
    private int askerMAC;
    private IP DHCPaddr;

    public DHCPData(int identifier, int mac)
    {
        this.identifier=identifier;
        this.askerMAC = mac;
    }

    public ArrayList<IP> getSubnetInfo()
    {
        return new ArrayList<IP>(){{add(firstRelay);add(subnetMask);}};
    }

    public void setSubnetInfo(IP firstrelay ,IP subnetmask)
    {
        this.firstRelay=firstrelay;
        this.subnetMask=subnetmask;
    }

    public ArrayList<IP> getRange()
    {
        return this.range;
    }

    public void setRange(ArrayList<IP> range)
    {
        this.range = range;
    }

    public boolean setChosen(IP ip)
    {
        if(range.contains(ip))
        {
            chosen = ip;
            return true;
        }
        return false;
    }

    public IP getChosen()
    {
        return chosen;
    }

    public void setOK()
    {
        this.ok = true;
    }

    public boolean isOK()
    {
        return this.ok;
    }

    public void setACK()
    {
        this.ack = true;
    }

    public boolean isACK()
    {
        return this.ack;
    }

    public void setAskerMAC(int mac)
    {
        this.askerMAC = mac;
    }

    public int getAskerMAC()
    {
        return this.askerMAC;
    }
    public IP getDHCPaddr()
    {
        return DHCPaddr;
    }
    public void setDHCPaddr(IP addr)
    {
        this.DHCPaddr = addr;
    }

}