package exceptions;

/**
 * Exception lancée si l'on appelle une méthode sur un mauvais objet
 * ex : un appareil demande à un lien quel est l'appareil de l'autre côté, alors que lui même n'y est pas connecté
 */

public class BadCallException extends Exception
{

}