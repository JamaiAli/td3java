/**
 * Exercice 5 - Questions 5.1 et 5.2 : arrêter proprement un thread (visibilité)
 *
 * Question 5.1 (bug) : sans "volatile", le thread peut ne jamais "voir" que
 *   actif est passé à false (le champ est gardé dans un cache/registre).
 *   Sur beaucoup de machines on voit "Le thread tourne encore !".
 *
 * Question 5.2 (correction) : avec "volatile", toute écriture de actif est
 *   immédiatement visible par les autres threads -> le thread s'arrête à
 *   chaque exécution ("Le thread est bien arrêté"). [ACTIVE]
 */
public class Travailleur implements Runnable {
    // Question 5.2 : ajouter volatile. (Retirez-le pour reproduire le bug 5.1.)
    private volatile boolean actif = true;

    public void arreter() { actif = false; }

    public void run() {
        long tours = 0;
        while (actif) {
            tours++;
        }
        System.out.println("Arrêté après " + tours + " tours");
    }

    public static void main(String[] args) throws InterruptedException {
        Travailleur w = new Travailleur();
        Thread t = new Thread(w);
        t.start();
        Thread.sleep(1000);
        w.arreter();
        System.out.println("Demande d'arrêt envoyée");
        t.join(3000); // on attend au maximum 3 s
        System.out.println(t.isAlive() ? "Le thread tourne encore !" : "Le thread est bien arrêté");
        System.exit(0);
    }
}
