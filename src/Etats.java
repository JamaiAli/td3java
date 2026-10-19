/**
 * Exercice 2 - Questions 2.1, 2.2, 2.3 : cycle de vie d'un thread
 *
 * On observe les états : NEW -> RUNNABLE -> TIMED_WAITING -> TERMINATED.
 *
 * Question 2.2 : si on commente le join() de la ligne "attendre la fin de t",
 *   la ligne 4 peut afficher TIMED_WAITING (le thread dort encore) au lieu de
 *   TERMINATED. join() sert à attendre que t soit réellement terminé.
 *
 * Question 2.3 : si on ajoute t.start(); à la fin du main, on obtient une
 *   java.lang.IllegalThreadStateException : un thread ne peut être démarré
 *   qu'UNE seule fois (voir la ligne commentée tout en bas).
 */
public class Etats {
    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(1000); // le thread dort 1 seconde
            } catch (InterruptedException e) {
                System.out.println("Interrompu");
            }
        });

        System.out.println("1. " + t.getState()); // avant start()  -> NEW
        t.start();                                  // TODO : démarrer t
        System.out.println("2. " + t.getState()); // juste après start() -> RUNNABLE
        Thread.sleep(200);                          // le main attend un peu
        System.out.println("3. " + t.getState()); // t est en train de dormir -> TIMED_WAITING
        t.join();                                   // TODO : attendre la fin de t avec join()
        System.out.println("4. " + t.getState()); // après la fin de t -> TERMINATED

        // Question 2.3 : décommentez la ligne suivante pour observer
        // l'exception IllegalThreadStateException (thread déjà terminé) :
        // t.start();
    }
}
