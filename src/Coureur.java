/**
 * Exercice 7 - Étape 3 : la classe Coureur (le thread)
 *
 * Chaque coureur s'échauffe (durée aléatoire), se déclare prêt, attend le
 * départ, court (durée aléatoire), puis franchit l'arrivée.
 */
public class Coureur extends Thread {
    private final LigneDeDepart ligne;
    private final Classement classement;

    public Coureur(String nom, LigneDeDepart ligne, Classement classement) {
        super(nom);
        // TODO 5 : initialiser les deux attributs
        this.ligne = ligne;
        this.classement = classement;
    }

    private int hasard(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }

    public void run() {
        try {
            System.out.println(getName() + " s'échauffe...");
            // TODO 6 : dormir entre 500 et 2000 ms (échauffement)
            sleep(hasard(500, 2000));
            // TODO 7 : se déclarer prêt sur la ligne, puis attendre le départ
            ligne.seDeclarerPret(getName());
            ligne.attendreDepart();
            System.out.println("   " + getName() + " s'élance !");
            // TODO 8 : dormir entre 1000 et 3000 ms (la course)
            sleep(hasard(1000, 3000));
            // TODO 9 : passer l'arrivée dans le classement
            classement.arriver(getName());
        } catch (InterruptedException e) {
            System.out.println(getName() + " a abandonné");
        }
    }
}
