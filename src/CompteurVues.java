import java.util.concurrent.atomic.AtomicInteger;

/**
 * Exercice 5 - Questions 5.3 et 5.4 : volatile ne suffit pas pour ++
 *
 * Question 5.3 (bug) : avec un "private volatile int vues = 0;" et vues++,
 *   le résultat est FAUX (< 200000). volatile garantit la VISIBILITÉ mais pas
 *   l'ATOMICITÉ : vues++ = lire, +1, écrire (3 étapes), deux threads peuvent
 *   s'entrelacer et perdre des incréments.
 *
 * Question 5.4 (correction) : AtomicInteger rend l'incrément atomique.
 *   incrementAndGet() est indivisible -> Vues = 200000 à chaque exécution,
 *   sans aucun synchronized. [ACTIVE]
 */
public class CompteurVues {
    private AtomicInteger vues = new AtomicInteger(0);

    public void nouvelleVue() { vues.incrementAndGet(); }
    public int getVues() { return vues.get(); }

    // ---- Version 5.3 (bug) : décommentez ce bloc et commentez celui du haut ----
    // private volatile int vues = 0;
    // public void nouvelleVue() { vues++; }
    // public int getVues() { return vues; }

    public static void main(String[] args) throws InterruptedException {
        CompteurVues v = new CompteurVues();
        Runnable spectateurs = () -> {
            for (int i = 0; i < 100_000; i++) v.nouvelleVue();
        };
        Thread t1 = new Thread(spectateurs);
        Thread t2 = new Thread(spectateurs);
        t1.start(); t2.start();
        t1.join();  t2.join();
        System.out.println("Vues : " + v.getVues() + " (attendu : 200000)");
    }
}
