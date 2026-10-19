import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Exercice 6 - Question 6.1 : un pool de 3 threads
 *
 * Un pool de 3 threads exécute 6 tâches : on voit au maximum 3 threads
 * différents (pool-1-thread-1, -2, -3), chacun réutilisé pour plusieurs tâches.
 *
 * Question : si on oublie pool.shutdown(), le programme ne se termine jamais
 *   (les threads du pool, non daemon, restent en attente de nouvelles tâches).
 */
public class PoolSimple {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 6; i++) {
            int numero = i; // copie nécessaire pour l'utiliser dans la lambda
            pool.execute(() ->
                System.out.println("Tâche " + numero + " exécutée par "
                        + Thread.currentThread().getName()));
        }

        pool.shutdown(); // TODO : arrêter proprement le pool
    }
}
