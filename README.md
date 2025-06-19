# Test Unitaire TDD #

### 1- Récupérer les événements du jour ####

### 2- Archiver les événements vieux de plus de 30 jours ####

#### Rédaction des tests ####

nous avons besoin de créer un test qui va vérifié qu'une fois la methode d'archivage appelé, les événements vieux de plus de 30 jours sont bien archivés.
pour cela on créer des envénements de test a une date antérieur de plus de 30 jours, on mock la date actuelle afin de s'assurer que la date actuelle est bien celle que l'on souhaite, puis on appelle la méthode d'archivage et on vérifie que les événements de test sont bien archivés.
on vient égelement vérifier que l'on demande au repository uniqument les événements qui sont vieux de plus de 30 jours.

#### Implémentation de la méthode d'archivage ####

Dans un premier temps, on va créer une méthode de récupération des événements antérieurs à une date donnée qui ne sont pas archivés (cela nous permettra de ne pas récupérer les donnée qui sont déjà archivées).
Ensuite, on va créer une méthode d'archivage qui va calculer la date limite d'archivage (30 jours avant la date actuelle) et qui va appeler la méthode de récupération des événements antérieurs à cette date limite. Enfin, on modifiera les événements récupérés pour passer leur propriété `active` à `false` et on les enregistrera dans le repository.

### 3- Modifier l'entité pour ajouter un status d'annulation ####

## Événements e-sport ##

### 4- Indiquer les équipes d'un événement e-sport ####

### 5- Ajouter un champ teaser pour l'événement e-sport ####
