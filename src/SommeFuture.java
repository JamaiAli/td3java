import java.util.concurrent.*;

/**
 * Exercice 6 - Question 6.2 : récupérer un résultat avec Callable et Future
 *
 * Un Callable renvoie une valeur ; submit() renvoie un Future qui permet de
 * récupérer le résultat (bloquant) avec get().
 *
 * Résultat attendu : Résultat : 500000500000
 */
public class SommeFuture {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        Callable<Long> somme = () -> {
            long s = 0;
            for (int i = 1; i <= 1_000_000; i++) s += i;
            return s;
        };

        // TODO : soumettre la tâche avec submit() et récupérer un Future<Long>
        Future<Long> f = pool.submit(somme);
        // TODO : afficher "Résultat : " + le résultat obtenu avec get()
        System.out.println("Résultat : " + f.get());

        pool.shutdown();
    }
}
