/**
 * Exercice 1 - Questions 1.2, 1.3, 1.4
 *
 * - Version séquentielle (1.2) : on appelle run() directement -> tout est
 *   exécuté par le thread main, les téléchargements s'enchaînent (~6000 ms).
 * - Version parallèle (1.3)   : on crée 3 threads et on les démarre avec
 *   start(), puis on attend leur fin avec join() (~3000 ms, le plus long).
 *
 * Par défaut, la version PARALLÈLE est active (c'est le résultat attendu final).
 * Pour tester la version séquentielle, commentez le bloc PARALLÈLE et
 * décommentez le bloc SÉQUENTIEL.
 */
public class Navigateur {
    public static void main(String[] args) throws InterruptedException {
        Telechargement d1 = new Telechargement("photo.jpg", 2);
        Telechargement d2 = new Telechargement("video.mp4", 3);
        Telechargement d3 = new Telechargement("cours.pdf", 1);

        long debut = System.currentTimeMillis();

        // ===== Version SÉQUENTIELLE (Question 1.2) ======================
        // d1.run();
        // d2.run();
        // d3.run();
        // ================================================================

        // ===== Version PARALLÈLE (Question 1.3) =========================
        Thread t1 = new Thread(d1, "DL-1");
        Thread t2 = new Thread(d2, "DL-2");   // TODO fait
        Thread t3 = new Thread(d3, "DL-3");   // TODO fait

        t1.start();                            // TODO : démarrer les trois threads
        t2.start();
        t3.start();

        t1.join();                             // TODO : attendre la fin des trois threads avec join()
        t2.join();
        t3.join();
        // Question 1.4 : si on met les join() en commentaire, la durée affichée
        // est ~0 ms car le main n'attend plus la fin des téléchargements.
        // ================================================================

        long duree = System.currentTimeMillis() - debut;
        System.out.println("Durée totale : " + duree + " ms");
    }
}
