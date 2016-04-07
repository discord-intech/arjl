package exceptions;

import hardware.AbstractHardware;

/**
 * Exception lancée par un appareil quand il overflow
 * @author J. Desvignes
 */
public class OverflowException extends Exception
{
    private AbstractHardware who;

    public OverflowException(AbstractHardware who)
    {
        this.who = who;
    }

    public AbstractHardware whoDidThis()
    {
        return who;
    }
}
