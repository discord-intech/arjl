package enums;


/**
 * Enum des types de liens
 * @author J. Desvignes
 */
public enum LinkTypes
{
    ETH("eth"),
    SERIAL("serial");

    private String name;
    LinkTypes(String name)
    {
        this.name = name;
    }

    public static LinkTypes getType(String text)
    {
        for(LinkTypes i : LinkTypes.values())
        {
            if(i.name.equals(text))
                return i;
        }
        return null;
    }
}