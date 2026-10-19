import java.util.ArrayList;
import java.util.List;

/**
 * Exercice 4 - Bloc synchronized et verrous distincts
 *
 * Objectif : ne verrouiller que ce qui est nécessaire, et utiliser un verrou
 * par ressource.
 *
 *  - Question 4.1 : les deux méthodes sont "synchronized" (verrou = l'objet b).
 *                   t1 et t2 se bloquent mutuellement -> ~2000 ms (série).
 *  - Question 4.2 : un verrou par ressource (bloc synchronized(livres) /
 *                   synchronized(lecteurs)), pause() HORS du bloc.
 *                   Les deux threads avancent en parallèle -> ~1000 ms. [ACTIVE]
 *
 * Question 4.3 : on laisse pause() (un traitement long) hors de la section
 *   critique car garder un verrou pendant une opération longue bloquerait
 *   inutilement les autres threads ; on ne verrouille que la modification
 *   réellement partagée (l'ajout dans la liste).
 */
public class Bibliotheque {
    private List<String> livres = new ArrayList<>();
    private List<String> lecteurs = new ArrayList<>();

    // ---- Question 4.2 : un verrou par ressource ----
    public void ajouterLivre(String titre) {
        synchronized (livres) {
            livres.add(titre);
        }
        pause(); // hors du bloc : traitement long non verrouillé
    }

    public void ajouterLecteur(String nom) {
        synchronized (lecteurs) {
            lecteurs.add(nom);
        }
        pause();
    }

    // ---- Question 4.1 (verrou unique) : pour tester, commentez les deux
    //      méthodes ci-dessus et décommentez celles-ci (~2000 ms) ----
    // public synchronized void ajouterLivre(String titre) {
    //     livres.add(titre);
    //     pause();
    // }
    // public synchronized void ajouterLecteur(String nom) {
    //     lecteurs.add(nom);
    //     pause();
    // }

    private void pause() {
        try { Thread.sleep(100); } catch (InterruptedException e) { }
    }

    public static void main(String[] args) throws InterruptedException {
        Bibliotheque b = new Bibliotheque();
        long debut = System.currentTimeMillis();

        Thread t1 = new Thread(() -> { for (int i = 0; i < 10; i++) b.ajouterLivre("Livre " + i); });
        Thread t2 = new Thread(() -> { for (int i = 0; i < 10; i++) b.ajouterLecteur("Lecteur " + i); });
        t1.start(); t2.start();
        t1.join();  t2.join();

        long duree = System.currentTimeMillis() - debut;
        System.out.println("Durée : " + duree + " ms");
    }
}
