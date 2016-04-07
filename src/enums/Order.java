package enums;

/**
 * Enum des différents ordres textuels que peut envoyer l'utilisateur
 * @author J. Desvignes
 * TODO à remplir au fur et à mesure
 */
public enum Order
{
    GO("go"),
    PAUSE("pause"),
    READSCRIPT("readscript"),
    ADD("add"),
    REMOVE("remove"),
    LINK("link"),
    EDIT("edit"),
    START("start"),
    STOP("stop"),
    REQUEST("request"),
    DHCP("dhcprequest"),
    CONFIG("config"),
    VERBOSE("verbose"),
    EXIT("exit"),
    DHCPRELAY("dhcprelay"),
    REPETITIVEREQUEST("repetitiverequest"),
    SAVE("save"),
    LOAD("load"),
    PIPO("aymeric"),
    IP("setip"),
    PRINT("print"),
    GATEWAY("gateway"),
    ADDRANGE("addrange"),
    SPEED("speed"),
    PRINTROUTES("printroutes"),
    STARTALL("startall"),
    ADDROUTE("addroute"),
    CLEARROUTES("clearroutes"),
    DEFAULTROUTE("defaultroute");



    public String order;

    Order(String text)
    {
        this.order = text;
    }
}
