/**
 * Exercice 7 - Étape 1 : la classe LigneDeDepart (l'objet partagé)
 *
 * Deux "conditions" : le nombre de coureurs prêts, et un booléen qui indique
 * si le départ a été donné. Synchronisation avec wait() / notifyAll().
 *
 * Règles (Aide de l'énoncé) :
 *  - Toujours while (condition) { wait(); }, jamais if.
 *  - Pour réveiller, utiliser notifyAll().
 *  - Les méthodes sont synchronized : wait()/notifyAll() s'appellent sans préfixe.
 */
public class LigneDeDepart {
    private final int nbCoureurs;
    private int nbPrets = 0;
    private boolean departDonne = false;

    public LigneDeDepart(int nbCoureurs) {
        this.nbCoureurs = nbCoureurs;
    }

    // Appelée par un coureur quand il arrive sur la ligne
    public synchronized void seDeclarerPret(String nom) {
        nbPrets++;
        System.out.println(nom + " est prêt (" + nbPrets + "/" + nbCoureurs + ")");
        // TODO 1 : réveiller les threads en attente (l'arbitre attend peut-être)
        notifyAll();
    }

    // Appelée par l'arbitre
    public synchronized void attendreTousPrets() throws InterruptedException {
        // TODO 2 : tant que nbPrets < nbCoureurs, appeler wait()
        while (nbPrets < nbCoureurs) {
            wait();
        }
        System.out.println("Arbitre : tout le monde est prêt !");
    }

    // Appelée par l'arbitre
    public synchronized void donnerDepart() {
        departDonne = true;
        System.out.println(">>> PAN ! Départ ! <<<");
        // TODO 3 : réveiller TOUS les coureurs
        notifyAll();
    }

    // Appelée par un coureur, juste après seDeclarerPret
    public synchronized void attendreDepart() throws InterruptedException {
        // TODO 4 : tant que le départ n'a pas été donné, appeler wait()
        while (!departDonne) {
            wait();
        }
    }
}
