# Go4Lunch

Cette application a été réalisée dans le cadre du parcours "Développeur d'applications Android" proposé par OpenClassrooms. Le but était de réaliser une application à 
destination des employés d'une entreprise pour qu'ils puissent facilement se retrouver pour le déjeuner.

## Fonctionnalités

### Connexion via Facebook ou Google

![Ecran d'accueil](/assets/images/Screenshot_go4lunch_home.png =270x)

### Liste des restaurants à proximité

En faisant appel à différentes API telles que Places et Distances Matrix, l'application affiche la liste des restaurants à proximité. L'application propose également 
aux utilisateurs de noter les restaurants et affiche la note moyenne sur cet écran.

![Liste des restaurants](/assets/images/Screenshot_go4lunch_list.png =270x)

### Ecran de détail d'un restaurant

Sur cet écran, l'utilisateur peut consulter les informations détaillées d'un restaurant, lancer un appel téléphonique vers celui-ci ou être dirigé vers son site web. Il 
peut également donner une note au restaurant ou indiquer qu'il va y déjeuner. La liste des utilisateurs ayant sélectionné ce restaurant depuis la veille à midi s'affiche 
en bas (affichage réactif, se mettant à jour instantanément pour tous les utilisateurs connectés)

![Liste des restaurants](/assets/images/Screenshot_go4lunch_details.png =270x)


### Carte des restaurants

Cette carte Google maps affiche les restaurants à proximité. Elle se met à jour selon les déplacements de l'utilisateur.

![Carte des restaurants](/assets/images/Screenshot_go4lunch_list.png =270x)


## Aspects techniques

- Architecture MVVM
- Base de données Firebase Firestore
- Authentification via Firebase
- Utilisation de Google Maps, Distances matrix API, Places API, Places autocomplete service 
- Requête http via Retrofit
- Notification push


## Dernière modification 
31/12/2021

## Connexion
Il est possible de se connecter avec un compte Facebook de test ou un (vrai) compte Google
Comptes Facebook de test :
- will_iwpdejp_yang@tfbnw.net / gogo4512
- elizabeth_kaafmrm_brownman@tfbnw.net / lunlun4512

