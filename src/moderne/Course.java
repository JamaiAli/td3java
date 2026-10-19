package moderne;

/** Version moderne : la classe Course (le main) ne change pas non plus. */
public class Course {
    public static void main(String[] args) throws InterruptedException {
        LigneDeDepart ligne = new LigneDeDepart(5);
        Classement classement = new Classement();
        String[] noms = {"Amina", "Bruno", "Chloé", "Driss", "Emma"};
        Coureur[] coureurs = new Coureur[noms.length];

        for (int i = 0; i < noms.length; i++) {
            coureurs[i] = new Coureur(noms[i], ligne, classement);
            coureurs[i].start();
        }

        ligne.attendreTousPrets();
        System.out.println("Arbitre : à vos marques... prêts...");
        Thread.sleep(1000);
        ligne.donnerDepart();

        for (Coureur c : coureurs) {
            c.join();
        }
        System.out.println("Arbitre : course terminée !");
    }
}
