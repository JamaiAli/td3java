import java.util.*;
import java.util.concurrent.*;

/**
 * Exercice 6 - Question 6.3 : plusieurs tâches avec invokeAll
 *
 * On découpe la somme de 1 à 4 000 000 en 4 morceaux de 1 000 000 chacun.
 * invokeAll() exécute toutes les tâches et renvoie la liste des Future.
 *
 * Résultat attendu : Total : 8000002000000
 */
public class SommeParallele {
    public static void main(String[] args) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(4);
        List<Callable<Long>> taches = new ArrayList<>();

        for (int k = 0; k < 4; k++) {
            long debut = k * 1_000_000L + 1;
            long fin   = (k + 1) * 1_000_000L;
            taches.add(() -> {
                long s = 0;
                for (long i = debut; i <= fin; i++) s += i;
                return s;
            });
        }

        List<Future<Long>> resultats = pool.invokeAll(taches);
        long total = 0;
        // TODO : parcourir resultats et ajouter chaque f.get() à total
        for (Future<Long> f : resultats) {
            total += f.get();
        }
        System.out.println("Total : " + total);
        pool.shutdown();
    }
}
