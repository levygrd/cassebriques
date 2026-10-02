# README : Projet Casse-briques

## Répartition des Tâches et Fonctionnalités

### **Lucas : Mécaniques de Briques Spéciales et Relance**
* **Briques d'altération de plateforme** : Implémentation d'effets dynamiques modifiant la taille de la plateforme de jeu lors des collisions[cite: 3]. La destruction d'une brique Jaune agit comme un malus en rétrécissant la plateforme, tandis que la brique Verte offre un bonus en l'agrandissant pour faciliter la récupération de la balle[cite: 3].
* **Brique de Dédoublement (Multiball)** : Création d'une mécanique spéciale liée aux briques Roses[cite: 3]. Une fois touchées, ces briques dédoublent la boule en jeu, permettant de gérer jusqu'à 5 boules simultanément sur l'écran pour accélérer la destruction du mur[cite: 3].
* **Menu de relance** : Développement d'un menu de fin interactif qui propose directement au joueur de relancer la partie après une défaite, évitant ainsi de devoir redémarrer l'application[cite: 3].

### **Lévy : Moteur Physique, Interface et Bonus**
* **Système de bonus physiques (Drops)** : Création de la classe `Bonus.java` gérant la chute verticale des objets et la détection de collision avec la raquette pour activer divers pouvoirs[cite: 3].
* **Gestion temporelle des effets (Timer dynamique)** : Ajout d'un compte à rebours de 10 secondes pour les altérations d'état (taille, vitesse)[cite: 3]. Un indicateur textuel "Reset Effets dans : X s" suit les mouvements de la raquette en temps réel pour prévenir l'annulation des effets[cite: 3].
* **Système de Pause interactif** : Implémentation de l'interface `KeyListener` permettant de geler le jeu avec `P` ou `Echap`[cite: 3]. La pause affiche un menu superposé (Overlay) contenant des boutons "Reprendre" et "Quitter"[cite: 3].
* **Menu principal interactif** : Conception d'un écran d'accueil avec des boutons graphiques pour gérer le lancement du thread principal et les boucles de Game Over[cite: 3].
* **Création de la Brique Cyan** : Ajout de la classe `BriqueBonus.java` (ID 6) conçue spécifiquement pour déclencher de manière ciblée la chute exclusive des bonus physiques[cite: 3].
* **HUD (Heads-Up Display)** : Affichage intégré à l'interface de jeu pour le niveau actuel, le score, et les pastilles graphiques rouges représentant les vies restantes[cite: 3].
* **Système de scoring** : Mise en place d'un compteur incrémentiel attribuant 100 points pour chaque brique détruite et 500 points pour chaque loot physique rattrapé[cite: 3].
* **Boîtes de dialogue UI** : Remplacement des alertes système de base par des fenêtres `JOptionPane` formatées en HTML (incluant couleurs, typographie, et variables de score) pour tous les événements majeurs de la partie[cite: 3].

### **Johan : Structure, Progression et Vies**
* **Système de vies** : Intégration d'un compteur accordant 3 vies au joueur[cite: 3]. Ces vies s'affichent visuellement en bas de l'écran pour un suivi clair tout au long de la partie[cite: 3].
* **Gestion des Niveaux** : Conception de l'architecture de progression du jeu, structurée autour d'un enchaînement de 3 niveaux distincts proposant des agencements de murs différents[cite: 3].
* **Outil de Débogage (Mode Test)** : Mise en place d'un mode développeur directement dans le fichier `Mur.java`[cite: 3]. En passant la constante `MODE_TEST` sur `true` ou `false`, il est possible d'accélérer drastiquement les parties pour tester efficacement les transitions de niveaux sans avoir à casser toutes les briques[cite: 3].