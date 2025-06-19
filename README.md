# Test Unitaire TDD #

#### 1- Récupérer les événements du jour ####
Nom du test: `shouldGetTodaysEvents`  

Dans un premier temps, `LocalDateTime` est mockée pour que la date soit fixe dans tout le test et être strictement identique à celle du service.  
Le test vient ensuite simuler deux événements, un pour aujourd'hui et un pour demain.  
On simule la tranche horaire de la journée en donnant la date du jour et l'heure de fin de la journée.  
Ensuite, on simule la réponse du repository pour qu'il retourne uniquement l'événement paramétré avec une date correspondant à aujourd'hui.  
Puis on appelle la méthode du service en donnant en paramètre le résultat simulé du répertoire et on vérifie que le résultat est bien celui attendu, c'est-à-dire l'événement d'aujourd'hui.  
Enfin, on regarde que la taille de la liste retournée est de 1, car l'autre événement est incorrect, si la taille est de 2, le test échoue.   
On vérifie également que la méthode du repository a bien été appelée avec les bons paramètres, c'est-à-dire le début et la fin de la journée.   
***
Nom de la méthode dans le service: `getTodaysEvents`  
  
La methode commence par récupérer le début et la fin de la journée actuelle, puis elle appelle le repository pour récupérer les événements dont la date est comprise entre ces deux bornes.
***
Nom de la méthode dans le repository: `findByEventDateAfterAndDateBefore`:  
  
La méthode du repository est appelée avec les paramètres de début et de fin de journée, et elle retourne une liste d'événements dont la date est comprise entre ces deux bornes.
***
#### 2- Archiver les événements vieux de plus de 30 jours ####

#### Rédaction des tests ####

Nous avons besoin de créer un test qui va vérifier qu'une fois la méthode d'archivage appelé, les événements vieux de plus de 30 jours sont bien archivés.
Pour cela on créer des événements de test à une date antérieure de plus de 30 jours, on mock la date actuelle afin de s'assurer que la date actuelle est bien celle que l'on souhaite, puis on appelle la méthode d'archivage et on vérifie que les événements de test sont bien archivés.
On vient également vérifier que l'on demande au repository uniquement les événements qui sont vieux de plus de 30 jours.

#### Implémentation de la méthode d'archivage ####

Dans un premier temps, on va créer une méthode de récupération des événements antérieurs à une date donnée qui ne sont pas archivés (cela nous permettra de ne pas récupérer les données qui sont déjà archivées.).
Ensuite, on va créer une méthode d'archivage qui va calculer la date limite d'archivage (30 jours avant la date actuelle) et qui va appeler la méthode de récupération des événements antérieurs à cette date limite. Enfin, on modifiera les événements récupérés pour passer leur propriété `active` à `false` et on les enregistrera dans le repository.

### 3- Modifier l'entité pour ajouter un statut d'annulation ###

### Rédaction des tests ###

Nous allons créer un test qui va vérifier que l'on peut annuler un événement en modifiant sa propriété `canceled` à `true` uniquement si l'événement a une date de début supérieure à 24 h de la date actuelle. Pour cela, nous allons créer 3 tests :
1. Un test qui va vérifier que l'on peut annuler un événement si la date de début est supérieure à 24 h de la date actuelle. Pour cela, on va créer un événement de test avec une date de début supérieure à 24 h de la date actuelle, on va appeler la méthode d'annulation et vérifier que la propriété `canceled` est bien passée à `true`. On mock la date actuelle afin de s'assurer que la date actuelle est bien celle que l'on souhaite.
2. Un test qui va vérifier que l'on ne peut pas annuler un événement si la date de début est inférieure à 24h de la date actuelle. Pour cela, on va créer un événement de test avec une date de début inférieure à 24h de la date actuelle, on va appeler la méthode d'annulation et vérifier que la propriété `canceled` n'est pas passée à `true`. On mock la date actuelle afin de s'assurer que la date actuelle est bien celle que l'on souhaite.
3. Un test qui va vérifier que l'on fournie bien un Id d'événement positif et qui existe. Pour cela, on va appeler la méthode d'annulation avec un Id d'événement négatif ou inexistant et vérifier que l'on lève bien une exception. De plus, on va mock un retour null de la part du répository pour un id afin de vérifier que l'on lève bien une exception si l'événement n'existe pas.

### Implémentation de la méthode d'annulation ###

Pour implémenter la méthode d'annulation, nous allons, dans un premier temps, ajouter un champ `canceled` de type booléen à l'entité `Event`, celui-ci permettreras de savoir si l'événement est annulé ou non. Par défaut, ce champ sera à `false`. Ensuite, nous allons créer une méthode `cancelEvent` qui va prendre en paramètre l'Id de l'événement à annuler. Cette méthode va vérifier si l'événement existe et si sa date de début est supérieure à 24 h de la date actuelle. Si c'est le cas, elle va modifier la propriété `canceled` de l'événement à `true` et enregistrer l'événement dans le repository, sinon elle va lever une exception en fonction de la situation.

## Événements e-sport ##

### 4- Indiquer les équipes d'un événement e-sport ###
Pour permettre d’associer deux équipes à un événement e-sport, j’ai tout d’abord ajouté dans le modèle Event deux références vers le modèle Team. Cela permet de stocker les équipes participantes directement dans l’entité événement.

Ensuite, dans la méthode validateEvent, j’ai intégré une vérification afin de m’assurer que ces deux équipes (teamA et teamB) ne soient jamais nulles lors de la création ou de la mise à jour d’un événement. Cette validation garantit que chaque événement a bien deux équipes définies.

Côté tests, j’ai implémenté plusieurs cas pour vérifier le comportement attendu :

Un test vérifie qu’il est impossible de créer un événement si l’une des deux équipes est null.

Un autre test s’assure qu’il est également impossible de mettre à jour un événement en passant une des équipes à null.

### 5- Ajouter un champ teaser pour l'événement e-sport ###

Pour améliorer la présentation des événements, j’ai ajouté un champ teaser de type texte dans le modèle Event afin de stocker une description courte et dynamique de l’événement.

Dans le service, j’ai créé une méthode generateTeaser qui prend un objet Event en paramètre et génère une chaîne de caractères formatée. Cette chaîne résume l’événement en affichant, par exemple :
"Dragons vs Phœnix – 2025-07-01T20:00 at Paris. Players: Alice, Bob vs Xavier, Yasmine".

Lors de la création (createEvent) et de la mise à jour (updateEvent) d’un événement, cette méthode est appelée pour générer et enregistrer automatiquement le teaser correspondant, garantissant ainsi que le teaser soit toujours à jour avec les informations de l’événement.

Côté tests, j’ai ajouté une vérification qui s’assure que la méthode génère correctement le teaser en fonction des données fournies, validant ainsi la bonne construction du texte résumé.
