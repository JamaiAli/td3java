package moderne;

import java.util.concurrent.CountDownLatch;

/**
 * Exercice 7 - La version moderne : LigneDeDepart réécrite avec deux
 * CountDownLatch (java.util.concurrent). Plus besoin de synchronized, wait()
 * ni notifyAll().
 *
 *  - prets  = new CountDownLatch(5) : chaque coureur appelle prets.countDown(),
 *             l'arbitre appelle prets.await().
 *  - depart = new CountDownLatch(1) : chaque coureur appelle depart.await(),
 *             l'arbitre appelle depart.countDown().
 */
public class LigneDeDepart {
    private final int nbCoureurs;
    private final CountDownLatch prets;
    private final CountDownLatch depart = new CountDownLatch(1);

    public LigneDeDepart(int nbCoureurs) {
        this.nbCoureurs = nbCoureurs;
        this.prets = new CountDownLatch(nbCoureurs);
    }

    public void seDeclarerPret(String nom) {
        prets.countDown();
        System.out.println(nom + " est prêt (" + (nbCoureurs - prets.getCount()) + "/" + nbCoureurs + ")");
    }

    public void attendreTousPrets() throws InterruptedException {
        prets.await();
        System.out.println("Arbitre : tout le monde est prêt !");
    }

    public void donnerDepart() {
        System.out.println(">>> PAN ! Départ ! <<<");
        depart.countDown();
    }

    public void attendreDepart() throws InterruptedException {
        depart.await();
    }
}
