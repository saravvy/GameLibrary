# Game Library

## Vision

Visionen med mit projekt er at skabe et spil bibliotek, hvor brugere kan tilføje spil til deres egen bibliotek, eller ønskeliste, på deres profil. 
Så formålet er at skabe et sted, hvor man kan holde styr på hvilke spil man ejer, og hvilke spil man er på udkig efter.

## Links

Deployed application (optional):  
https://gamelibrary.saraelena.dk/api/routes

Source code repository:  
https://github.com/saravvy/GameLibrary

Portfolio website:  
https://saravvy.github.io/portfolio/

---

# Architecture

## System Overview

Strukturen af mit projekt er opdelt på følgende måde: 

- Controller layer: Bruges til HTTP requests, og sender respons til brugeren.

- DAO layer: Bruges til database operationer.

- Entity layer: Indeholder alle entiteter.

- Security layer: Bruges til alt der har med sikkerhed at gøre, som oprettelse at brugere, verificering af brugerens rolle, login.

- Database: Bruges til at gemme data.

Technologier i mit projekt:

- Java 17
- Javalin
- JPA / Hibernate
- PostgreSQL
- Maven
- JWT authentication
- Jackson
- RestAssured
- JUnit 5 
- BCrypt


---

# Data Model

## ERD
![img.png](img.png)


## Important Entities

### User
Entititen user repræsenterer et bruger i systemet.

Felter:

- id

- name

- password

- role

- library

- wishlist


### Game 
Entititen Game repræsenterer et spil i systemet.

Felter:

- name
- developer
- id
- dateReleased
- description



### GameInLibrary
Etititen GameInLibrary repræsenterer et spil, som er i en brugeres bibliotek.

Felter:

- id
- dateAdded
- library 
- game

### GameInWishlist 
Etititen GameInWishlist repræsenterer et spil, som er i en brugeres ønskeliste.

Felter:

- id
- dateAdded
- wishlist
- game

###

# API Documentation

Her vil jeg dokumentere nogle af de vigtigste endpoints:

### Registrer et bruger:

POST /api/auth/register

Request body:
{

"name": "testuser",

"password": "123456",

"role" : "USER"

}


Response:
201 Created

{

"token": "dHVzZXIiLCJle"
"name": "testuser"

}

---

### Hent alle spil 

GET /api/game

Response:

{

"id": 92,

"name": "Metro: Last Light Redux",

"developer": "Deep Silver",

"dateReleased": [
2014,
8,
25
],

"description": "It is the sequel to the game Metro 2033. Both games are based on the world of Dmitry Glukhovsky's novel Metro 2033 - in post-apocalyptic Moscow, in the tunnels of subway lines groups of very different people survive. And not only people. There are mutated people there, and mutated animals. They are distinguished by unusual abilities, but even more - behaviour strategies. In the struggle for the remnants of resources, all these beings spend their gloomy days.\nAll characters use unusual and unexpected means for life and war. Something is created from the remnants of a past, normal life, something has already appeared in a new, changed world.\nLocations are struck by an unusual synthesis of the ruins of a known world and traces of a catastrophe.\nFrom the previous game Metro: Last Light differs, according to general opinion, by careful work on the remarks and shortcomings of the previous game. The set of weapons is significantly expanded, the technologies of Metro 2033 have become more developed.\nThe game has two fundamentally different endings and five additions."

},

{

"id": 53,

"name": "Outlast",

"developer": "Red Barrels",

"dateReleased": [
2013,
9,
3
],

"description": "Cinematic gameplay is used to make a stylistic point, and this psychological horror game performs just that. Outlast follows the story of investigative journalist Miles Upshur, that got the lead on the inhuman experiments, performed on the asylum patients. Outlast forces players to run away from danger, simply because there is no combat system. All Miles can do is to document what is happening inside the Mount Massive Asylum. Players do not have a flashlight, only a night vision camera, which constantly requires batteries in order to function. Miles will be pursued through the halls, rooms, yards and enclosed laboratories by distressed and aggressive inhabitants. Over the course of the game, players will have to solve puzzles by locating and collecting items and travel through the Asylum with realistic movement animation and horror sting sound effect, in order to make players sympathize with the protagonists."

}

### Login
POST /api/auth/login

Request body:

{

"name": "testuser3",

"password": "1234562"

}

Response:

{

"token": "ejxm4_0-mbXn8gwkYdfwUKDB1Fu4",

"username": "testuser3"

}
### Opret et review 
POST /api/reviews/{gameId}

Request body:

{

 "stars": 5,
 
"description": "this game is so good",

"added": "2026-10-29",

"user": 
{

"id": 8

}

}

Response:
201 Created

### Delete et review

Request body:
DELETE /api/reviews/{gameId}

Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.ey

Response:
200 OK

# User Stories


- Som en bruger, skal jeg kunne tilføje et spil til mit bibliotek, så jeg kan holde styr over de spil jeg ejer.
- Som en bruger, skal jeg kunne tilføje et spil til min ønskeliste, så jeg kan holde styr over de spil jeg ønsker mig.
- Som en person, skal jeg kunne oprette mig som bruger, så jeg har min egen profil.
- Som en bruger, skal jeg kunne logge ind, så jeg kan se mit profil.
- Som en bruger, skal jeg kunne tilføje et review til et spil, så andre kan se hvad jeg synes om spillet.
- Som en admin, skal jeg kunne se alle brugere, så jeg kan holde styr på dem.
- Som en admin, skal jeg kunne tilføje et spil, så systemet bliver opdateret med nye spil. 
- Som en admin, skal jeg kunne slette et spil, så spil som ikke længere findes, kan fjernes. 
- Som en admin, skal jeg kunne slette en bruger, så brugere kan slettes, hvis de ikke skal have lov til at være i systemet mere. 
- Som en admin, skal jeg kunne slette en review, så upassende reviews kan fjernes.

---
