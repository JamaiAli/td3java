import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

/**
 * Exercice 3 - Une ArrayList partagée qui perd des éléments
 *
 * Trois versions sont présentes. Choisissez-en UNE en commentant/décommentant.
 *
 *  - Question 3.1 (LE BUG)      : ArrayList non protégée. Taille < 20000 et/ou
 *                                 ArrayIndexOutOfBoundsException (race condition).
 *  - Question 3.3 (CORRECTION 1): bloc synchronized (liste) autour de add().
 *                                 Taille = 20000 à chaque fois.  [ACTIVE par défaut]
 *  - Question 3.4 (CORRECTION 2): Collections.synchronizedList(...).
 *                                 Taille = 20000 à chaque fois.
 *
 * Question 3.2 : liste.add(x) fait 2 choses : 1) ranger x dans la case n° size ;
 *   2) size++. Deux threads peuvent lire la même valeur de size, écrire dans la
 *   même case (l'un écrase l'autre -> un élément perdu) puis faire size++ deux
 *   fois -> taille trop grande ou case vide -> ArrayIndexOutOfBoundsException.
 */
public class ListePartagee {
    public static void main(String[] args) throws InterruptedException {

        // ---- Version 3.1 (bug) et 3.3 (correction 1) : ArrayList simple ----
        List<Integer> liste = new ArrayList<>();

        // ---- Version 3.4 (correction 2) : liste synchronisée ----
        // Décommentez la ligne suivante ET commentez celle du dessus,
        // puis utilisez le add() SANS bloc synchronized dans la lambda.
        // List<Integer> liste = Collections.synchronizedList(new ArrayList<>());

        Runnable tache = () -> {
            for (int i = 0; i < 10_000; i++) {
                // Question 3.3 - CORRECTION 1 : bloc synchronized sur la ressource.
                synchronized (liste) {
                    liste.add(i);
                }
                // Question 3.1 (BUG) : remplacez le bloc ci-dessus par simplement :
                // liste.add(i);
            }
        };

        Thread t1 = new Thread(tache);
        Thread t2 = new Thread(tache);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Taille attendue : 20000");
        System.out.println("Taille obtenue  : " + liste.size());
    }
}
