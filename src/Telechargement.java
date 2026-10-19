/**
 * Exercice 1 - Question 1.1 : La tâche
 *
 * Un "téléchargement" est simulé par une simple attente avec sleep().
 * La classe implémente Runnable : son code s'exécute dans run().
 */
public class Telechargement implements Runnable {
    private String fichier;
    private int secondes;

    public Telechargement(String fichier, int secondes) {
        // Question 1.1 - TODO : initialiser les deux attributs
        this.fichier = fichier;
        this.secondes = secondes;
    }

    public void run() {
        String qui = Thread.currentThread().getName();
        System.out.println(qui + " commence " + fichier);
        for (int i = 1; i <= secondes; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }
            System.out.println("   " + fichier + " : " + (i * 100 / secondes) + " %");
        }
        System.out.println(qui + " a terminé " + fichier);
    }
}
