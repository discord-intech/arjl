package enums;

/**
 * Enumération des vitesses de traitement des appareils exprimés en millisecondes/100paquet
 * @author J. Desvignes
 */
public enum ClockSpeed
{
    SLOW(6),
    MEDIUM(3),
    HIGH(1),
    INSTANT(0);


    public int speed;
    ClockSpeed(int speed)
    {
        this.speed = speed;
    }
}
