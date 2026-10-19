import java.util.concurrent.*;

/**
 * Exercice 6 - Question 6.4 : ScheduledExecutorService
 *
 * Avec newScheduledThreadPool(1), on affiche "Bip" toutes les secondes pendant
 * 5 secondes, puis on arrête le pool.
 *
 * scheduleAtFixedRate(tache, 0, 1, TimeUnit.SECONDS) : démarre tout de suite
 * (délai initial 0), puis répète toutes les 1 seconde.
 */
public class Bip {
    public static void main(String[] args) throws InterruptedException {
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);

        pool.scheduleAtFixedRate(() -> System.out.println("Bip"), 0, 1, TimeUnit.SECONDS);

        Thread.sleep(5000); // on laisse tourner 5 secondes
        pool.shutdown();    // arrêt du pool
    }
}
