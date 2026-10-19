/**
 * Exercice 7 - Étape 4 : le programme principal (l'arbitre)
 *
 * L'arbitre attend que les 5 coureurs soient prêts, donne le départ, puis
 * attend que tous aient franchi l'arrivée.
 *
 * Vérifications attendues :
 *  - aucun "s'élance" avant "PAN !" ;
 *  - les 5 "s'élance" apparaissent juste après "PAN !" ;
 *  - les positions vont de 1 à 5 sans doublon ;
 *  - "course terminée" s'affiche en dernier.
 */
public class Course {
    public static void main(String[] args) throws InterruptedException {
        LigneDeDepart ligne = new LigneDeDepart(5);
        Classement classement = new Classement();
        String[] noms = {"Amina", "Bruno", "Chloé", "Driss", "Emma"};
        Coureur[] coureurs = new Coureur[noms.length];

        // TODO 10 : créer et démarrer les 5 coureurs (une boucle for)
        for (int i = 0; i < noms.length; i++) {
            coureurs[i] = new Coureur(noms[i], ligne, classement);
            coureurs[i].start();
        }

        ligne.attendreTousPrets();
        System.out.println("Arbitre : à vos marques... prêts...");
        Thread.sleep(1000);
        ligne.donnerDepart();

        // TODO 11 : attendre la fin des 5 coureurs avec join()
        for (Coureur c : coureurs) {
            c.join();
        }
        System.out.println("Arbitre : course terminée !");
    }
}
