package exceptions;

/**
 * Exception lancée si l'on appelle une méthode sur un mauvais objet
 * ex : un appareil demande à un lien quel est l'appareil de l'autre côté, alors que lui même n'y est pas connecté
 *
 * Utilisée partout dans le moteur de simulation, afin de simplifier les catch de l'interface
 * @author J. Desvignes
 */

public class BadCallException extends Exception
{

}