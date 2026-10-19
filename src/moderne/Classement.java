package moderne;

/** Version moderne : identique à l'originale (inchangée). */
public class Classement {
    private int prochainePosition = 1;

    public synchronized void arriver(String nom) {
        int position = prochainePosition;
        prochainePosition++;
        System.out.println("   " + nom + " franchit l'arrivée : position " + position);
    }
}
