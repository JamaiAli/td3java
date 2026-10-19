package moderne;

/** Version moderne : la classe Coureur ne change pas par rapport à l'originale. */
public class Coureur extends Thread {
    private final LigneDeDepart ligne;
    private final Classement classement;

    public Coureur(String nom, LigneDeDepart ligne, Classement classement) {
        super(nom);
        this.ligne = ligne;
        this.classement = classement;
    }

    private int hasard(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }

    public void run() {
        try {
            System.out.println(getName() + " s'échauffe...");
            sleep(hasard(500, 2000));
            ligne.seDeclarerPret(getName());
            ligne.attendreDepart();
            System.out.println("   " + getName() + " s'élance !");
            sleep(hasard(1000, 3000));
            classement.arriver(getName());
        } catch (InterruptedException e) {
            System.out.println(getName() + " a abandonné");
        }
    }
}
