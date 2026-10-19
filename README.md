# TD3 — Programmation Concurrente en Java

Dossier prêt à l'emploi pour IntelliJ IDEA.

**Dépôt GitHub :** https://github.com/JamaiAli/td3java

## Structure

```
td3/
├── src/                     <- toutes les classes (marquer ce dossier "Sources Root")
│   ├── Telechargement.java   Exercice 1
│   ├── Navigateur.java       Exercice 1  (main)
│   ├── Etats.java            Exercice 2  (main)
│   ├── Horloge.java          Exercice 2  (main, daemon)
│   ├── ListePartagee.java    Exercice 3  (main)
│   ├── Bibliotheque.java     Exercice 4  (main)
│   ├── Travailleur.java      Exercice 5  (main, volatile)
│   ├── CompteurVues.java     Exercice 5  (main, AtomicInteger)
│   ├── PoolSimple.java       Exercice 6  (main)
│   ├── SommeFuture.java      Exercice 6  (main)
│   ├── SommeParallele.java   Exercice 6  (main)
│   ├── Bip.java              Exercice 6  (main, ScheduledExecutor)
│   ├── LigneDeDepart.java    Exercice 7
│   ├── Classement.java       Exercice 7
│   ├── Coureur.java          Exercice 7
│   ├── Course.java           Exercice 7  (main)
│   └── moderne/              Exercice 7 — version CountDownLatch (bonus)
│       ├── LigneDeDepart.java
│       ├── Classement.java
│       ├── Coureur.java
│       └── Course.java       (main, lancer "moderne.Course")
├── captures/                <- placez-y vos captures d'écran
├── RAPPORT.md               <- le rapport complet du TD
└── README.md
```

## Ouvrir dans IntelliJ

1. **File → Open…** et sélectionnez le dossier `td3`.
2. Si IntelliJ ne détecte pas les sources : clic droit sur le dossier `src`
   → **Mark Directory as → Sources Root**.
3. Vérifiez le JDK : **File → Project Structure → Project → SDK** (JDK 8+).
4. Ouvrez n'importe quelle classe contenant un `main` et cliquez sur la flèche
   verte ▶ à côté de `public static void main(...)` pour la lancer.

> Chaque fichier = une classe publique du même nom. Les classes d'un même
> exercice (ex. 1 et ex. 7) se référencent entre elles : lancez la classe qui
> contient le `main` (voir la liste ci-dessus).

## Classes à lancer (celles avec un `main`)

`Navigateur`, `Etats`, `Horloge`, `ListePartagee`, `Bibliotheque`, `Travailleur`,
`CompteurVues`, `PoolSimple`, `SommeFuture`, `SommeParallele`, `Bip`, `Course`,
et `moderne.Course`.

Certains programmes ne s'arrêtent pas tout seuls (c'est normal) :
- `Horloge` en **étape a** (sans `setDaemon`) : arrêter avec le carré rouge ■.

## Variantes (bug / correction)

Plusieurs fichiers contiennent, en commentaire, la version « buguée » et les
corrections pour reproduire ce que demande l'énoncé :
- `ListePartagee.java` : bug 3.1 / correction 3.3 (active) / correction 3.4.
- `Bibliotheque.java` : verrou unique 4.1 / un verrou par ressource 4.2 (active).
- `Travailleur.java` : retirer `volatile` pour reproduire le bug 5.1.
- `CompteurVues.java` : bloc commenté pour le bug 5.3 / `AtomicInteger` 5.4 (active).
- `Navigateur.java` : version séquentielle 1.2 / parallèle 1.3 (active).

## Compiler / lancer en ligne de commande (optionnel)

```bash
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out Navigateur
java -cp out moderne.Course
```
