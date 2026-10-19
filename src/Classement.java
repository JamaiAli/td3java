/**
 * Exercice 7 - Étape 2 : la classe Classement (fournie)
 *
 * Attribue une position à chaque coureur qui franchit l'arrivée.
 *
 * Question : si on retirait synchronized, deux coureurs arrivant en même temps
 *   pourraient lire la même valeur de prochainePosition -> deux fois la même
 *   position (doublon), et une position "sautée".
 */
public class Classement {
    private int prochainePosition = 1;

    public synchronized void arriver(String nom) {
        int position = prochainePosition;
        prochainePosition++;
        System.out.println("   " + nom + " franchit l'arrivée : position " + position);
    }
}
