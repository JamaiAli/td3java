# TD 3 — Programmation Concurrente en Java

**Module :** Programmation Java Avancée : POO et JVM — STI 4A-FISE 2026/2027
**Enseignante :** Dr. Nadia Badri Fettah

---

## Objectifs du TD

- Créer et démarrer des threads avec `Runnable` et avec `extends Thread`.
- Observer le cycle de vie d'un thread (`getState`, `sleep`, `join`, `daemon`).
- Mettre en évidence une situation de compétition (*race condition*) et la corriger avec `synchronized`.
- Utiliser `volatile`, `AtomicInteger`, `ExecutorService`, `Callable` et `Future`.
- Synchroniser le départ de plusieurs threads avec `wait()` et `notifyAll()`.

> **Note sur les captures d'écran.** Les emplacements marqués `📷 [Capture : ...]`
> sont prévus pour vos propres captures d'écran d'exécution (à placer dans le
> dossier `captures/`). Les sorties déjà recopiées ci-dessous proviennent d'une
> exécution réelle du projet.

---

## Exercice 1 — Téléchargements : séquentiel ou parallèle ?

On simule 3 téléchargements : `photo.jpg` (2 s), `video.mp4` (3 s) et `cours.pdf` (1 s).
Un « téléchargement » est une simple attente avec `sleep()`.

### Question 1.1 — La tâche (`Telechargement.java`)

```java
public class Telechargement implements Runnable {
    private String fichier;
    private int secondes;

    public Telechargement(String fichier, int secondes) {
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
```

### Question 1.2 — Version séquentielle

On appelle `run()` directement : tout est exécuté par le thread `main`, les fichiers
se téléchargent l'un après l'autre. **Durée ≈ 6000 ms (2 + 3 + 1).**

```java
d1.run();
d2.run();
d3.run();
```

### Question 1.3 — Version parallèle (`Navigateur.java`)

On crée un thread par téléchargement, on les démarre avec `start()`, puis on
attend leur fin avec `join()`.

```java
Thread t1 = new Thread(d1, "DL-1");
Thread t2 = new Thread(d2, "DL-2");
Thread t3 = new Thread(d3, "DL-3");

t1.start(); t2.start(); t3.start();
t1.join();  t2.join();  t3.join();
```

**Sortie réelle (version parallèle) :**

```
DL-3 commence cours.pdf
DL-1 commence photo.jpg
DL-2 commence video.mp4
   photo.jpg : 50 %
   cours.pdf : 100 %
DL-3 a terminé cours.pdf
   video.mp4 : 33 %
   photo.jpg : 100 %
DL-1 a terminé photo.jpg
   video.mp4 : 66 %
   video.mp4 : 100 %
DL-2 a terminé video.mp4
Durée totale : 3035 ms
```

Les pourcentages des trois fichiers sont mélangés et la durée ≈ **3000 ms** (le
plus long des trois). ✅

📷 [Capture : exécution parallèle de Navigateur]

### Question 1.4 — Sans `join()`

Si on met les trois `join()` en commentaire, la durée affichée tombe à **≈ 0 ms**.
C'est **faux** : le `main` n'attend plus la fin des téléchargements, il calcule la
durée immédiatement après avoir lancé les threads (qui continuent en arrière-plan).

### Question 1.5 — Tableau comparatif

| Version                     | Durée mesurée (ms) | Thread qui exécute le code     |
|-----------------------------|--------------------|--------------------------------|
| Séquentielle (`run()`)      | ≈ 6000             | `main`                         |
| Parallèle (`start()`+`join()`) | ≈ 3035          | `DL-1`, `DL-2`, `DL-3`         |

**Pourquoi la version parallèle est-elle plus rapide, même sur une machine à un
seul cœur ?** Parce que pendant un `sleep()`, un thread **n'utilise pas le
processeur** : il est mis en attente. Les trois attentes se déroulent donc en même
temps au lieu de s'additionner, d'où une durée égale au plus long (≈ 3 s) et non à
leur somme (≈ 6 s).

---

## Exercice 2 — Cycle de vie : sleep, join, getState, daemon

### Question 2.1 — Afficher les états (`Etats.java`)

```java
System.out.println("1. " + t.getState()); // avant start()  -> NEW
t.start();
System.out.println("2. " + t.getState()); // juste après start() -> RUNNABLE
Thread.sleep(200);
System.out.println("3. " + t.getState()); // t dort -> TIMED_WAITING
t.join();
System.out.println("4. " + t.getState()); // après fin -> TERMINATED
```

**Sortie réelle :**

```
1. NEW
2. RUNNABLE
3. TIMED_WAITING
4. TERMINATED
```
✅ Conforme au résultat attendu.

📷 [Capture : états du thread]

### Question 2.2 — Le rôle de `join()`

Si on commente le `join()` juste avant la ligne 4, l'affichage de la ligne 4 peut
devenir **`TIMED_WAITING`** (le thread dort encore) au lieu de `TERMINATED`.
➡️ **`join()` sert à bloquer le thread appelant jusqu'à ce que le thread visé soit
réellement terminé.**

### Question 2.3 — Redémarrer un thread terminé

Si on ajoute `t.start();` à la fin du `main`, on obtient :

```
Exception in thread "main" java.lang.IllegalThreadStateException
```

➡️ **Un thread ne peut être démarré qu'une seule fois.** Une fois `TERMINATED`, il
ne peut pas redémarrer : il faut créer un nouvel objet `Thread`.

### Question 2.4 — Le thread daemon (`Horloge.java`)

- **Étape a** (sans `setDaemon`) : la boucle `while(true)` ne s'arrête jamais ; le
  programme tourne indéfiniment, il faut l'arrêter avec `Ctrl + C`.
- **Étape b** (`t.setDaemon(true);` **avant** `start()`) : on voit ~4 lignes `Tic`,
  puis `Fin du main`, puis le programme **s'arrête tout seul**. Quand il ne reste
  plus que des threads daemon, la JVM se termine. ✅
- **Étape c** (`setDaemon(true)` **après** `start()`) : lève
  `IllegalThreadStateException` — on ne peut rendre daemon qu'un thread non démarré.

📷 [Capture : horloge daemon qui s'arrête après « Fin du main »]

---

## Exercice 3 — Une ArrayList partagée qui perd des éléments

### Question 3.1 — Le bug

Deux threads ajoutent chacun 10 000 entiers dans la **même** `ArrayList` non
protégée (taille attendue 20000).

**Observation (bug) :** on obtient des tailles **inférieures à 20000** et parfois
une `ArrayIndexOutOfBoundsException` levée par l'un des threads.

| Exécution | Taille obtenue | Exception affichée ? |
|-----------|----------------|----------------------|
| 1         | ex. 17342      | non                  |
| 2         | ex. 20000      | non                  |
| 3         | ex. 13981      | oui (AIOOBE)         |
| 4         | ex. 18765      | non                  |
| 5         | ex. 15012      | oui (AIOOBE)         |

> Les valeurs exactes varient à chaque exécution et selon la machine. Si vous
> obtenez toujours 20000, remplacez `10_000` par `100_000` (attendu 200000).

📷 [Capture : taille < 20000 et/ou ArrayIndexOutOfBoundsException]

### Question 3.2 — Comprendre

`liste.add(x)` fait **deux choses** : 1) ranger `x` dans la case numéro `size` ;
2) faire `size++`. Comme ces deux étapes ne sont pas atomiques, deux threads
peuvent lire la **même** valeur de `size`, écrire dans la **même** case (l'un
écrase l'écriture de l'autre → un élément perdu), puis incrémenter `size` deux
fois. La taille devient incohérente, et une case peut rester « vide » → exception.
C'est le **même mécanisme d'entrelacement** que les autres races.

### Question 3.3 — Correction n°1 : un bloc `synchronized`

On entoure `liste.add(i)` d'un `synchronized (liste) { ... }`.

```java
synchronized (liste) {
    liste.add(i);
}
```

**Sortie réelle (5 exécutions) :** `Taille obtenue : 20000` à chaque fois, sans
exception. ✅

> On verrouille la **ressource partagée elle-même** (`liste`) car le `main` est
> `static` : il n'y a pas de `this`.

### Question 3.4 — Correction n°2 : une liste synchronisée

```java
import java.util.Collections;
List<Integer> liste = Collections.synchronizedList(new ArrayList<>());
```

Chaque méthode (`add`, `size`, `get`…) est déjà `synchronized`. **Résultat : 20000
à chaque fois.** ✅

### Question 3.5 — Réflexion

```java
if (!liste.contains(42)) {
    liste.add(42);
}
```

**Peut-on obtenir deux fois 42 ?** **Oui.** Chaque appel (`contains`, `add`) est
protégé individuellement, mais **pas l'ensemble « vérifier puis ajouter »**. Deux
threads peuvent tous deux évaluer `contains(42)` à `false` avant qu'aucun n'ait
ajouté, puis ajouter chacun un 42.

**Correction :** rendre l'opération composée atomique en verrouillant tout le bloc :

```java
synchronized (liste) {
    if (!liste.contains(42)) {
        liste.add(42);
    }
}
```

---

## Exercice 4 — Bloc `synchronized` et verrous distincts (`Bibliotheque.java`)

### Question 4.1 — Deux ressources, un seul verrou

Avec `public synchronized void ...`, le verrou est **l'objet `b` tout entier**.
`ajouterLivre` et `ajouterLecteur` ne peuvent pas s'exécuter en même temps : `t2`
doit attendre `t1`. **Durée ≈ 2000 ms** (20 appels × 100 ms, en série).

➡️ **`livres` et `lecteurs` ne sont pas liés**, pourtant `t2` attend `t1`
inutilement : un seul verrou pour deux ressources indépendantes.

### Question 4.2 — Un verrou par ressource

```java
public void ajouterLivre(String titre) {
    synchronized (livres) { livres.add(titre); }
    pause();                       // hors du bloc
}
public void ajouterLecteur(String nom) {
    synchronized (lecteurs) { lecteurs.add(nom); }
    pause();
}
```

**Sortie réelle :** `Durée : 1097 ms` ≈ **1000 ms**. Les deux threads avancent
maintenant **en parallèle** (verrous distincts). ✅

📷 [Capture : durée ≈ 1000 ms]

### Question 4.3 — Réflexion

**Pourquoi laisser `pause()` (traitement long) hors de la section critique ?**
Parce que garder un verrou pendant une opération longue bloquerait inutilement les
autres threads ; on ne verrouille que la modification réellement partagée (l'ajout
dans la liste), ce qui maximise le parallélisme.

---

## Exercice 5 — `volatile` et `AtomicInteger`

### Question 5.1 — Arrêter proprement un thread (`Travailleur.java`)

Sans `volatile`, le thread peut **ne jamais voir** que `actif` est passé à `false`
(valeur mise en cache dans un registre). Sur beaucoup de machines on lit alors
`Le thread tourne encore !`. Le problème n'est pas garanti, mais **possible** —
c'est ce qui le rend dangereux.

### Question 5.2 — La correction

On ajoute `volatile` devant `boolean actif`. Toute écriture devient immédiatement
visible par les autres threads.

```java
private volatile boolean actif = true;
```

**Sortie réelle :**

```
Demande d'arrêt envoyée
Arrêté après 3983922949 tours
Le thread est bien arrêté
```
➡️ `Le thread est bien arrêté` à chaque exécution. ✅

📷 [Capture : « Le thread est bien arrêté »]

### Question 5.3 — `volatile` ne suffit pas pour `++` (`CompteurVues.java`)

Avec `private volatile int vues = 0;` et `vues++`, le résultat est **faux**
(< 200000). `volatile` garantit la **visibilité** mais **pas l'atomicité** :
`vues++` = *lire → +1 → écrire* (3 étapes), deux threads s'entrelacent et perdent
des incréments.

### Question 5.4 — `AtomicInteger`

```java
import java.util.concurrent.atomic.AtomicInteger;
private AtomicInteger vues = new AtomicInteger(0);
public void nouvelleVue() { vues.incrementAndGet(); }
public int getVues() { return vues.get(); }
```

`incrementAndGet()` est **atomique** (indivisible). **Sortie réelle :**

```
Vues : 200000 (attendu : 200000)
```
200000 à chaque exécution, **sans aucun `synchronized`**. ✅

### Question 5.5 — Tableau récapitulatif

| Outil           | Garantit la visibilité | Rend `c++` sûr |
|-----------------|:----------------------:|:--------------:|
| Rien            | Non                    | Non            |
| `volatile`      | Oui                    | Non            |
| `synchronized`  | Oui                    | Oui            |
| `AtomicInteger` | Oui                    | Oui            |

---

## Exercice 6 — ExecutorService, Callable et Future

### Question 6.1 — Un pool de 3 threads (`PoolSimple.java`)

Un pool de 3 threads exécute 6 tâches. **Sortie réelle (ordre variable) :**

```
Tâche 2 exécutée par pool-1-thread-2
Tâche 3 exécutée par pool-1-thread-3
Tâche 1 exécutée par pool-1-thread-1
Tâche 5 exécutée par pool-1-thread-3
Tâche 4 exécutée par pool-1-thread-2
Tâche 6 exécutée par pool-1-thread-1
```

- **Combien de threads différents ?** **3** (`pool-1-thread-1`, `-2`, `-3`),
  réutilisés pour plusieurs tâches.
- **Si on oublie `pool.shutdown()` ?** Le programme **ne se termine jamais** : les
  threads du pool (non daemon) restent en attente de nouvelles tâches.

📷 [Capture : 3 threads du pool]

### Question 6.2 — Récupérer un résultat avec Callable et Future (`SommeFuture.java`)

```java
Future<Long> f = pool.submit(somme);
System.out.println("Résultat : " + f.get());
```

**Sortie réelle :** `Résultat : 500000500000` ✅ (`f.get()` est bloquant : il attend
la fin du calcul).

### Question 6.3 — Plusieurs tâches avec invokeAll (`SommeParallele.java`)

Somme de 1 à 4 000 000 découpée en 4 morceaux, exécutés par un pool de 4 threads.

```java
List<Future<Long>> resultats = pool.invokeAll(taches);
long total = 0;
for (Future<Long> f : resultats) {
    total += f.get();
}
```

**Sortie réelle :** `Total : 8000002000000` ✅

### Question 6.4 — ScheduledExecutorService (`Bip.java`)

```java
ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
pool.scheduleAtFixedRate(() -> System.out.println("Bip"), 0, 1, TimeUnit.SECONDS);
Thread.sleep(5000);
pool.shutdown();
```

Affiche `Bip` toutes les secondes pendant 5 secondes (≈ 5-6 lignes), puis s'arrête.

📷 [Capture : les « Bip » toutes les secondes]

---

## Exercice 7 — Le départ de la course

5 coureurs (5 threads) s'échauffent, viennent sur la ligne, attendent le signal
commun, puis partent **en même temps**. L'arbitre (thread `main`) attend que tous
soient prêts, donne le départ, puis attend l'arrivée de tous.

Classes : `LigneDeDepart` (objet partagé), `Classement` (fournie), `Coureur`
(thread), `Course` (main).

### Étape 1 — `LigneDeDepart` (TODO 1 à 4)

```java
public synchronized void seDeclarerPret(String nom) {
    nbPrets++;
    System.out.println(nom + " est prêt (" + nbPrets + "/" + nbCoureurs + ")");
    notifyAll();                               // TODO 1
}
public synchronized void attendreTousPrets() throws InterruptedException {
    while (nbPrets < nbCoureurs) { wait(); }   // TODO 2
    System.out.println("Arbitre : tout le monde est prêt !");
}
public synchronized void donnerDepart() {
    departDonne = true;
    System.out.println(">>> PAN ! Départ ! <<<");
    notifyAll();                               // TODO 3
}
public synchronized void attendreDepart() throws InterruptedException {
    while (!departDonne) { wait(); }           // TODO 4
}
```

### Étapes 3 et 4 — `Coureur` (TODO 5 à 9) et `Course` (TODO 10, 11)

Points clés : `super(nom)` + initialisation des attributs ; `seDeclarerPret` puis
`attendreDepart` ; dans `Course`, boucle `for` pour créer/démarrer les 5 coureurs,
puis `join()` sur chacun.

**Sortie réelle :**

```
Emma s'échauffe...
Bruno s'échauffe...
Chloé s'échauffe...
Driss s'échauffe...
Amina s'échauffe...
Emma est prêt (1/5)
Bruno est prêt (2/5)
Amina est prêt (3/5)
Driss est prêt (4/5)
Chloé est prêt (5/5)
Arbitre : tout le monde est prêt !
Arbitre : à vos marques... prêts...
>>> PAN ! Départ ! <<<
   Bruno s'élance !
   Emma s'élance !
   Chloé s'élance !
   Driss s'élance !
   Amina s'élance !
   Emma franchit l'arrivée : position 1
   Driss franchit l'arrivée : position 2
   Chloé franchit l'arrivée : position 3
   Bruno franchit l'arrivée : position 4
   Amina franchit l'arrivée : position 5
Arbitre : course terminée !
```

**Vérifications :** aucun `s'élance` avant `PAN !` ✅ ; les 5 `s'élance` juste après
`PAN !` ✅ ; positions de 1 à 5 sans doublon ✅ ; `course terminée` en dernier ✅.

📷 [Capture : course complète]

### Étape 2 — Question sur `Classement`

Si on retirait `synchronized` de `arriver()`, deux coureurs arrivant en même temps
pourraient lire la **même** valeur de `prochainePosition` → **deux fois la même
position** (doublon) et une position « sautée ».

### Étape 5 — Expériences

1. **`notifyAll()` → `notify()` dans `donnerDepart()` :** `notify()` ne réveille
   **qu'un seul** coureur. Un seul part, les autres restent bloqués dans `wait()` ;
   le `join()` du main ne se termine jamais → le programme ne se termine pas
   (`Ctrl + C`).
2. **Supprimer `ligne.attendreTousPrets();` dans `Course` :** l'arbitre peut donner
   le départ **avant** que tous soient prêts. Les coureurs encore en échauffement
   au moment du `PAN !` **ne sont pas bloqués pour toujours** : en arrivant, leur
   `attendreDepart()` voit `departDonne == true` (grâce au `while`) et ils partent
   aussitôt, sans attendre. Ils ne « ratent » donc pas le signal.
3. **`while (!departDonne)` → `while (true)` dans `attendreDepart()` :** la
   condition de sortie disparaît ; chaque coureur reste **bloqué pour toujours**
   dans `wait()` même après le départ → la course ne démarre jamais.

### Étape 6 — Comprendre les choix

1. **Pourquoi `departDonne` est-il indispensable ?** C'est la **condition d'état**
   qui mémorise que le départ a eu lieu. Sans elle, un coureur qui fait `wait()`
   après un `notifyAll()` déjà passé attendrait un signal qui ne reviendra jamais
   (réveil manqué). Le booléen permet au `while` de voir « le départ est déjà
   donné » et de ne pas se rendormir.
2. **Pourquoi `while` et non `if` autour de `wait()` ?** À cause des **réveils
   intempestifs** (*spurious wakeups*) et des réveils multiples par `notifyAll()` :
   au réveil, il faut **re-vérifier la condition**. Avec `if`, le thread
   continuerait même si la condition est encore fausse.
3. **Un coureur dans `wait()` consomme-t-il du processeur ?** **Non.** `wait()` met
   le thread en sommeil et libère le verrou ; il ne consomme pas de CPU. Au
   contraire, une attente active `while (!departDonne) {}` **occupe un cœur à
   100 %** inutilement.
4. **Pourquoi `notifyAll()` plutôt que `notify()` dans `seDeclarerPret()` ?** Parce
   que plusieurs threads peuvent attendre sur le **même objet** (l'arbitre dans
   `attendreTousPrets`, et potentiellement d'autres). `notify()` ne réveillerait
   qu'un seul thread, peut-être pas le bon ; `notifyAll()` réveille tout le monde,
   et chacun re-teste sa condition dans son `while`.

### La version moderne — `CountDownLatch` (bonus, package `moderne`)

`LigneDeDepart` réécrite avec deux `CountDownLatch`, sans `synchronized`, `wait()`
ni `notifyAll()` :

```java
private final CountDownLatch prets;            // new CountDownLatch(5)
private final CountDownLatch depart = new CountDownLatch(1);

// coureur prêt            : prets.countDown();
// arbitre attend          : prets.await();
// arbitre donne le départ : depart.countDown();
// coureur attend le départ: depart.await();
```

Les classes `Coureur`, `Classement` et `Course` **ne changent pas**. Même
comportement, code beaucoup plus simple. ✅

---

## FAQ — Erreurs fréquentes rencontrées

| Symptôme | Cause | Solution |
|----------|-------|----------|
| `unreported exception InterruptedException` | `sleep/join/wait/get` lèvent une exception vérifiée | `throws InterruptedException` sur `main` ou `try/catch` |
| `IllegalThreadStateException` | `start()` appelé deux fois | créer un nouvel objet `Thread` |
| `IllegalMonitorStateException` | `wait()/notifyAll()` hors d'un bloc/méthode `synchronized` | appeler dans une méthode `synchronized` |
| variables non `final` dans une lambda | usage de `i` (variable de boucle) | copier avant : `int numero = i;` |
| `ArrayIndexOutOfBoundsException` (ex. 3.1) | c'est le bug à observer | le noter dans le tableau |
| Durée ≈ 0 ms (ex. 1) | `join()` oublié | ajouter les `join()` avant le calcul de durée |
| Le programme ne s'arrête jamais | pool non arrêté / `notifyAll()` oublié | `shutdown()` / `notifyAll()` |

---

## Conclusion

Ce TD a permis de manipuler les deux façons de créer des threads (`Runnable` et
`extends Thread`), d'observer leur cycle de vie, et surtout de comprendre les
**deux garanties distinctes** de la concurrence :

- la **visibilité** (`volatile`) — voir les écritures des autres threads ;
- l'**atomicité** (`synchronized`, `AtomicInteger`) — rendre indivisibles les
  opérations composées comme `c++` ou « vérifier puis ajouter ».

Les outils de haut niveau (`ExecutorService`, `Future`, `CountDownLatch`)
simplifient grandement l'écriture de code concurrent correct par rapport à la
gestion manuelle de `wait()`/`notifyAll()`.
