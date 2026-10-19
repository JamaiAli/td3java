/**
 * Exercice 2 - Question 2.4 : le thread daemon
 *
 * Un thread daemon n'empêche pas la JVM de s'arrêter : quand il ne reste plus
 * que des threads daemon, le programme se termine.
 *
 * Étape a : sans setDaemon(true), le programme ne s'arrête jamais (boucle
 *   while(true)) ; il faut l'arrêter avec Ctrl + C.
 * Étape b : avec t.setDaemon(true) AVANT start(), on voit ~4 lignes "Tic",
 *   puis "Fin du main", puis le programme s'arrête tout seul.
 * Étape c : setDaemon(true) APRÈS start() lève IllegalThreadStateException.
 */
public class Horloge implements Runnable {
    public void run() {
        while (true) {
            System.out.println("Tic : " + new java.util.Date());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread t = new Thread(new Horloge());
        t.setDaemon(true);            // Étape b (TODO) : rendre t daemon
        t.start();
        Thread.sleep(3500);
        System.out.println("Fin du main");
    }
}
