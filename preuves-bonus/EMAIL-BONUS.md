**Objet : [CASA-IA : groupe à compléter] [N° étudiant à compléter] TP 2 - tests terminé - Yassine Atiki**

Bonjour Monsieur Grin,

Je vous transmets mon travail pour la partie « Quelques tests avec LangChain4j » du TP 2 (Tests 1 à 7). Le projet est un projet Maven Java 25. Les résultats et les explications demandés sont reproduits ci-dessous dans le corps de cet email.

Dépôt GitHub : https://github.com/Yassine-Atiki/tp2-les-test-Yassine_Atiki

Les résultats des Tests 1 à 6 et du premier essai sans outils sont les sorties authentiques sauvegardées pendant mon travail précédent. Les nouveaux essais du Test 7 utilisent réellement Gemini et Open-Meteo. Les dates des prévisions sont celles des JSON enregistrés, en GMT comme dans le code du TP ; elles ne constituent pas une prévision permanente.

## Test 1 — Question sur l’heure

```text
Question : Quelle heure est-il maintenant ?
Réponse : Je ne peux pas vous donner l'heure exacte car je n'ai pas accès à votre localisation ni à votre fuseau horaire. 

Vous pouvez consulter l'horloge de votre appareil (téléphone, ordinateur, montre) pour connaître l'heure actuelle !
```

Cet appel simple ne reçoit ni l’heure de mon ordinateur ni un outil donnant l’heure. Le modèle ne fournit donc pas une heure actuelle vérifiée. Sa réponse évoque ma localisation et mon fuseau horaire ; cela reste une explication générée par le modèle, et non la preuve d’une consultation de l’horloge.

## Test 1 — Présentation et mémoire

```text
Question 1 : Bonjour, je m'appelle Yassine Atiki.
Réponse 1 : Bonjour Yassine ! Ravi de faire votre connaissance. Comment puis-je vous aider aujourd'hui ?
------------------------------
Question 2 : Comment je m'appelle ?
Réponse 2 : Je ne le sais pas encore ! Comme je suis une intelligence artificielle, je ne connais pas votre nom à moins que vous ne me le disiez. 

Comment vous appelez-vous ?
```

Les deux appels model.chat(String) du Test 1 sont indépendants : le deuxième appel ne retransmet pas la présentation. L’API étant sans état et aucune ChatMemory n’étant associée à ces appels, le modèle ne dispose plus de mon nom.

## Test 4 — Comparaison des embeddings

```text
Couple 1
Phrase 1 : Le chat dort sur le canapé.
Phrase 2 : Un félin se repose sur le sofa.
Similarité BGE : 0,6475
Similarité Gemini : 0,9323

Couple 2
Phrase 1 : Il fait très chaud aujourd'hui.
Phrase 2 : Il fait très froid aujourd'hui.
Similarité BGE : 0,9084
Similarité Gemini : 0,8533

Couple 3
Phrase 1 : Je programme une application Java.
Phrase 2 : Je développe un logiciel en Java.
Similarité BGE : 0,8267
Similarité Gemini : 0,9617

Couple 4
Phrase 1 : La voiture roule sur l'autoroute.
Phrase 2 : Je prépare une salade de tomates.
Similarité BGE : 0,6093
Similarité Gemini : 0,7195

Couple 5
Phrase 1 : L'étudiant révise pour son examen.
Phrase 2 : Un élève se prépare à une épreuve.
Similarité BGE : 0,7174
Similarité Gemini : 0,9306

Couple 6
Phrase 1 : Le ciel est couvert et il pleut.
Phrase 2 : Une banque approuve un prêt immobilier.
Similarité BGE : 0,7048
Similarité Gemini : 0,7269
```

BGE Small EN V1.5 quantifié fonctionne localement ; Gemini utilise gemini-embedding-001 avec SEMANTIC_SIMILARITY. Dans ces mesures, Gemini classe mieux les trois couples de sens proche (1, 3 et 5) que les couples sans rapport (4 et 6). BGE attribue notamment 0,9084 au couple chaud/froid : les phrases partagent leur structure et leur sujet. Un score de cosinus élevé ne signifie donc pas une équivalence logique ; des opposés peuvent rester proches dans l’espace d’embeddings. La pertinence du classement compte davantage qu’un seuil universel.

## Test 5 — RAG et contenu de infos.txt

```text
=== Sans RAG ===
Question : Comment s'appelle le chat de Pierre ?
Réponse : Il y a plusieurs personnages ou personnes nommés Pierre qui ont des chats célèbres. Pourriez-vous préciser de quel Pierre il s'agit ? 

Par exemple :
* S'agit-il d'un personnage de fiction, de littérature (comme dans un conte) ?
* D'une personnalité publique ou d'un influenceur ?
* D'une énigme ?

Donnez-moi un peu plus de contexte et je pourrai vous répondre !
```

```text
=== Avec RAG ===
Question : Comment s'appelle le chat de Pierre ?
Réponse : Le chat de Pierre s'appelle Felix.
```

```text
=== Avec RAG ===
Question : Pierre appelle son chat. Qu'est-ce qu'il pourrait dire ?
Réponse : Il pourrait dire « Felix ».
```

Pour l’expérience demandée, j’ai temporairement remplacé la phrase sur Paris par « La capitale de la France est Marseille. ».

```text
=== Avec RAG ===
Question : Quelle est la capitale de la France ?
Réponse : D'après les informations fournies, la capitale de la France est Marseille.
```

Le modèle reprend ici le contexte récupéré par le RAG, y compris une affirmation volontairement fausse. Le RAG apporte des informations privées, mais ne garantit pas leur exactitude. Le fichier infos.txt a ensuite été restauré et contient bien « La capitale de la France est Paris. ».

```text
=== Avec RAG ===
Question : Quelle est la capitale de la France ?
Réponse : La capitale de la France est Paris.
```

## Test 6 — PDF et conversation

```text
Vous : Quel est l'objectif du cours « Agents conversationnels en Java avec LangChain4j » de Richard Grin ?
Gemini : D'après les informations fournies, le texte ne mentionne pas explicitement l'objectif global du cours de Richard Grin, mais il détaille plusieurs sujets abordés dans celui-ci en lien avec LangChain4j :

* **L'utilisation d'agents conversationnels et de services IA** en Java (via `AiServices`) pour interagir avec des modèles de langage (LM) comme Google Gemini.
* **L'utilisation d'outils (tools)** par les agents IA pour étendre leurs capacités ou agir sur l'extérieur (par exemple, des outils de météo ou de calcul), en simplifiant un processus complexe grâce à l'annotation `@Tool`.
* **La gestion des résultats** (avec `Result<T>`), incluant le suivi de l'utilisation des tokens (`TokenUsage`) et des exécutions d'outils.
* **La récupération de types structurés** en réponse (des objets Java plutôt qu'une simple chaîne de caractères) pour plus de sécurité et de facilité de traitement.
* **L'utilisation de modèles de chat** (`ChatModel`, `ChatRequest`, `ChatResponse`) pour effectuer des requêtes de type question-réponse.

Vous : Peux-tu m'expliquer plus en détail le rôle de AiServices que tu viens de mentionner ?
Gemini : Sur la base des informations fournies, voici le rôle de `AiServices` :

* **Création et instanciation d'assistants virtuels :** Il permet de construire des services ou des assistants (via `AiServices.builder(...)`) à partir d'interfaces Java (comme `Aide` ou `Assistant`).
* **Configuration des modèles et des capacités :** Il sert à associer un modèle de langage (`chatModel` ou `streamingChatModel`), d'ajouter de la mémoire de conversation (`chatMemory`), ou d'intégrer des outils (`tools`) que le modèle peut exécuter.
* **Gestion de la modération et des guardrails :** Il permet d'intégrer des mécanismes pour vérifier et contrôler ce qui est envoyé au modèle de langage (LM) et ce qui est retourné par celui-ci, assurant ainsi des échanges appropriés et sûrs (notamment grâce à l'association d'un `ModerationModel`).
* **Gestion avancée des requêtes :** Il propose des méthodes pour gérer l'exécution des outils (en mode séquentiel ou parallèle, gestion des erreurs), ajouter des écouteurs d'événements, ou configurer le RAG (gestion de la mémoire des messages augmentés).
```

Le document interrogé est langchain4j.pdf. La réponse ne prétend pas trouver un objectif explicite absent des extraits : elle décrit les thèmes récupérés. La relance sur AiServices illustre la continuité de la conversation avec MessageWindowChatMemory.withMaxMessages(10). Le chargement du PDF, l’ingestion et le retriever déjà validés sont conservés.

## Test 7 — Première expérience sans outils

```text
Question : Je dois partir dans 2 jours à Paris ; j'y resterai 3 jours. Est-ce que je dois mettre un parapluie dans mes valises ?
Réponse : C'est une excellente question, surtout à Paris où le temps peut être capricieux ! 

Pour vous répondre avec précision, il me faudrait la date exacte ou consulter la météo en temps réel pour les 3 prochains jours. Cependant, règle générale pour Paris : **oui, glissez un petit parapluie pliant dans votre valise.** 

Voici pourquoi :
1. **Le climat parisien** est très changeant. Même au printemps ou en été, une averse surprise est vite arrivée.
2. **Vous allez probablement marcher beaucoup** (visites, musées, balades le long de la Seine). Être mouillé gâche vite une journée de tourisme.
3. Un petit parapluie ne prend presque pas de place et ne pèse rien.

**Mon conseil :** Regardez l'application météo de votre smartphone juste avant de fermer votre valise pour confirmer, mais par sécurité, prévoyez-le ! Bon voyage à Paris !
Tokens d'entrée : 36
Tokens de sortie : 204
Tokens de raisonnement : 0
Total déclaré : 240
Coût de la requête : 0,00052080 $
Requêtes pour dépenser 1 $ : 1921
```

Sans outil, Gemini conseille un parapluie d’après des généralités et reconnaît qu’il ne consulte pas de prévisions en temps réel. Cette réponse, reproduite intégralement, ne démontre donc aucune météo effective pour les dates du séjour. Le coût ci-dessus est l’estimation de l’expérience initiale avec les tarifs alors utilisés dans Test2Couts ; ce n’est pas une facture ni une nouvelle vérification tarifaire.

## Test 7 — API Open-Meteo et TestMeteo

Requête directe demandée : https://api.open-meteo.com/v1/forecast?latitude=48.85&longitude=2.35&daily=precipitation_probability_max&forecast_days=3

```json
{"latitude":48.853382,"longitude":2.355208,"generationtime_ms":0.030517578125,"utc_offset_seconds":0,"timezone":"GMT","timezone_abbreviation":"GMT","elevation":46.0,"daily_units":{"time":"iso8601","precipitation_probability_max":"%"},"daily":{"time":["2026-11-09","2026-11-10","2026-11-11"],"precipitation_probability_max":[90,83,53]}}
```

forecast_days=3 comprend le jour courant. Pour un départ demain et un séjour de 3 jours, l’outil demande 4 jours et ne teste que les indices 1, 2, 3 ; pour un départ dans 2 jours, il demande 5 jours et teste les indices 2, 3, 4. Le seuil du professeur est strictement supérieur à 25 %. Les probabilités Paris enregistrées sont [90,83,53], puis [90,83,53,15] et [90,83,53,15,15] : les trois séjours dépassent donc ce seuil. TestMeteo a aussi réellement testé Rabat et Sydney, ainsi que les seuils 0 % et 100 %. À 100 %, le résultat est false, conformément à la comparaison stricte. Des contrôles locaux explicitement distincts des prévisions vérifient 25/26 %, les indices, les entrées invalides et les valeurs nulles. Une période dépassant les 7 jours retenus par le TP est refusée plutôt que tronquée silencieusement.

## Test 7 — Conversation avec un seul outil

```text
Question : J'ai prévu d'aller aujourd'hui à la ville dont la latitude est 48.85 et la longitude est 2.35 pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?
Réponse : Oui, je vous conseille de mettre un parapluie dans votre valise, car de la pluie est prévue durant votre séjour de 3 jours à partir d'aujourd'hui.
```

```text
Question : Finalement, je ne vais partir que demain. Est-ce que tu me conseilles de prendre un parapluie ?
Réponse : Oui, je vous conseille toujours de prendre un parapluie. De la pluie est prévue pour votre séjour de 3 jours, qui commence cette fois-ci demain.
```

```text
Question : Explique en une phrase ce qu'est une interface Java.
Réponse : Une interface Java est un contrat de programmation abstrait qui définit un ensemble de méthodes que les classes qui l'implémentent s'engagent à fournir.
```

```text
Question : J'ai prévu d'aller aujourd'hui à Paris pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?
Réponse : Pour Paris (latitude 48.8566, longitude 2.3522), voici les prévisions pour votre séjour de 3 jours commençant aujourd'hui :
```

```text
Question : J'ai prévu d'aller aujourd'hui à la ville de Zzyzx pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?
Réponse : Je ne connais pas les coordonnées géographiques (latitude et longitude) de la ville de Zzyzx, ce qui m'empêche de vérifier les prévisions météo pour cet endroit. Si vous me les fournissez, je pourrai vous dire s'il est conseillé de prendre un parapluie.
```

Les deux premières requêtes météo utilisent bien forecast_days=3 puis 4 pour un séjour de trois jours, avec nbJoursDebut=0 puis 1. La réponse sur Paris donnée ensuite est incomplète dans cette exécution : je la conserve telle quelle et ne la présente pas comme une prévision supplémentaire validée. Pour Zzyzx, le modèle demande les coordonnées. Une autre tentative, sauvegardée séparément, avait proposé des coordonnées de mémoire : cela montre la variabilité du modèle, sans prouver l’exactitude de ses connaissances géographiques. La consigne système de conservation de la durée a été ajoutée après un essai où le modèle avait réduit le séjour à un seul jour.

## Test 7 — Conversation avec les deux outils

```text
Question : J'ai prévu d'aller aujourd'hui à Paris pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?
Réponse : Oui, je vous conseille de mettre un parapluie dans votre valise. Des prévisions de pluie sont annoncées à Paris pour les 3 prochains jours.
```

```text
Question : Finalement, je ne vais partir que demain. Est-ce que tu me conseilles de prendre un parapluie ?
Réponse : Oui, je vous conseille toujours de prendre un parapluie. Des prévisions de pluie sont annoncées pour votre séjour de 3 jours à partir de demain.
```

```text
Question : J'ai prévu d'aller aujourd'hui à Rabat,Morocco pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?
Réponse : Oui, je vous conseille de mettre un parapluie dans votre valise. Des prévisions de pluie sont annoncées à Rabat pour votre séjour de 3 jours qui commence aujourd'hui.
```

```text
Question : J'ai prévu d'aller aujourd'hui à la ville de Zzyzx pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?
Réponse : Non, je ne vous conseille pas de mettre un parapluie dans votre valise. Aucune pluie n'est prévue à Zzyzx pour votre séjour de 3 jours qui commence aujourd'hui.
```

```text
Question : Dois-je prendre un parapluie aujourd'hui à VilleInexistanteQzxv987654 pour un séjour de 3 jours ?
Réponse : Je n'ai pas pu trouver les coordonnées de la ville « VilleInexistanteQzxv987654 » (cette ville est introuvable). Je ne peux donc pas vérifier les prévisions météo pour cet endroit.
```

```text
Question : Explique en une phrase ce qu'est une interface Java.
Réponse : En Java, une interface est un contrat abstrait qui définit un ensemble de méthodes que les classes qui l'implémentent s'engagent à fournir.
```

Les traces HTTP montrent getCoordonnees puis isPluiePrevue pour les villes nommées. Le géocodage retourne notamment Zzyzx à [35.14276,-116.10474] et la météo [0,0,0]. Le nom volontairement inexistant ne donne aucun résultat ; l’exception réelle est transmise au modèle, qui explique sa limite. La question Java est traitée sans appel météo. Les expériences indépendantes ont été séparées par la commande « nouveau », après observation d’un HTTP 400 Gemini lorsqu’une longue conversation cumulait les villes et saturait la fenêtre imposée de 20 messages. La question de départ et sa relance « demain » restent dans la même conversation.

## Test 7 — JSON réels, nouvelle session Zzyzx après activation du logging

Question : J'ai prévu d'aller aujourd'hui à la ville de Zzyzx pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?

Les fragments ci-dessous sont prélevés littéralement dans les corps JSON capturés par le vrai client HTTP Google GenAI. Les explications sont en gras et placées immédiatement avant chaque fragment pour garder les JSON lisibles. Seules les parties nouvelles sont reproduites ; les répétitions de l’historique ne sont pas recopiées. Les journaux complets et les corps bruts sont conservés dans le dépôt.

**1. APPLICATION → GEMINI — REQUÊTE initiale. Le champ tools décrit les deux fonctions Java disponibles et leurs paramètres ; le modèle peut demander leur exécution, sans accéder lui-même au code Java.**

```json
"tools":[{"functionDeclarations":[{"description":"Retourne la latitude puis la longitude de la première ville trouvée par Open-Meteo.\nLe nom doit être en anglais. On peut préciser le pays, par exemple Rabat,Morocco.\nUtiliser ces coordonnées pour appeler ensuite isPluiePrevue.\nUne ville introuvable provoque une erreur ; ne pas inventer ses coordonnées.\n","name":"getCoordonnees","parameters":{"description":"","properties":{"ville":{"description":"Nom de ville en anglais, éventuellement suivi du pays","type":"STRING"}},"required":["ville"],"type":"OBJECT"}},{"description":"Retourne true si la probabilité d'avoir de la pluie pour la ville dont on donne la latitude et la longitude\nest importante durant une période de nbJours jours. La période commence nbJourDebut jours après aujourd'hui.\nPar exemple, si nbJoursDebut = 1 et nbJours = 2, la période de 2 jours commence demain (1 jour après aujourd'hui).\nSi nbJoursDebut = 0 et nbJours = 3, la période de 3 jours commence aujourd'hui.\nRetourne false sinon.\n","name":"isPluiePrevue","parameters":{"description":"","properties":{"latitude":{"description":"Latitude","type":"NUMBER"},"longitude":{"description":"Longitude","type":"NUMBER"},"nbJoursDebut":{"description":"Nombre de jours après aujourd'hui pour indiquer le début de la période","type":"INTEGER"},"nbJours":{"description":"Nombre de jours de la période","type":"INTEGER"}},"propertyOrdering":["latitude","longitude","nbJoursDebut","nbJours"],"required":["latitude","longitude","nbJoursDebut","nbJours"],"type":"OBJECT"}}]}]
```

**2. GEMINI → APPLICATION — RÉPONSE à la première requête et demande d’exécution de getCoordonnees. Le modèle fournit Zzyzx comme argument. L’identifiant relie l’appel à son résultat. LangChain4j exécute ensuite la méthode Java.**

```json
"functionCall": {
              "name": "getCoordonnees",
              "args": {
                "ville": "Zzyzx"
              },
              "id": "call_2787286228850517705"
            }
```

**3. APPLICATION → OPEN-METEO — REQUÊTE HTTP de géocodage, réalisée par la méthode Java. Ce n’est pas un appel direct du LLM au Web.**

```text
GET https://geocoding-api.open-meteo.com/v1/search?name=Zzyzx
```

**4. OPEN-METEO → APPLICATION — RÉPONSE HTTP 200 du géocodage. Le premier résultat fournit les coordonnées utilisées par l’outil.**

```json
{"results":[{"id":5411245,"name":"Zzyzx","latitude":35.14276,"longitude":-116.10474,"elevation":290.0,"feature_code":"PPL","country_code":"US","admin1_id":5332921,"admin2_id":5391726,"timezone":"America/Los_Angeles","population":1,"country_id":6252001,"country":"United States","admin1":"California","admin2":"San Bernardino"}],"generationtime_ms":0.17344952}
```

**5. APPLICATION → GEMINI — NOUVELLE REQUÊTE contenant la réponse de l’outil getCoordonnees. Bien que le champ soit nommé functionResponse, il est envoyé dans une nouvelle requête HTTP de l’application à Gemini. Le résultat est une chaîne représentant le tableau Java sérialisé.**

```json
"functionResponse":{"id":"call_2787286228850517705","name":"getCoordonnees","response":{"result":"[35.14276,-116.10474]"}}
```

**6. GEMINI → APPLICATION — RÉPONSE à cette deuxième requête et demande d’exécution de isPluiePrevue. Le modèle reprend les coordonnées retournées par le géocodage ; nbJoursDebut=0 et nbJours=3 décrivent le séjour.**

```json
"functionCall": {
              "name": "isPluiePrevue",
              "args": {
                "longitude": -116.10474,
                "latitude": 35.14276,
                "nbJoursDebut": 0,
                "nbJours": 3
              },
              "id": "call_3373297188592331085"
            }
```

**7. APPLICATION → OPEN-METEO — REQUÊTE HTTP météo avec ces coordonnées et forecast_days=3. La méthode Java applique ensuite le seuil strict de 25 %.**

```text
GET https://api.open-meteo.com/v1/forecast?latitude=35.142760&longitude=-116.104740&daily=precipitation_probability_max&forecast_days=3
```

**8. OPEN-METEO → APPLICATION — RÉPONSE HTTP 200 météo. Les probabilités sont nulles en pourcentage (0 %), et non des valeurs JSON null indisponibles.**

```json
{"latitude":35.12898,"longitude":-116.10696,"generationtime_ms":0.04410743713378906,"utc_offset_seconds":0,"timezone":"GMT","timezone_abbreviation":"GMT","elevation":286.0,"daily_units":{"time":"iso8601","precipitation_probability_max":"%"},"daily":{"time":["2026-11-09","2026-11-10","2026-11-11"],"precipitation_probability_max":[0,0,0]}}
```

**9. APPLICATION → GEMINI — TROISIÈME REQUÊTE, ajoutant le résultat false de isPluiePrevue. Le reste de l’historique est retransmis par LangChain4j mais n’est pas répété ici.**

```json
"functionResponse":{"id":"call_3373297188592331085","name":"isPluiePrevue","response":{"result":"false"}}
```

**10. GEMINI → APPLICATION — RÉPONSE finale en langage naturel, désormais fondée sur le résultat réel de l’outil.**

```json
"text": "D'après les prévisions pour votre séjour de 3 jours à Zzyzx (qui commence aujourd'hui), aucune pluie n'est prévue. Il n'est donc pas nécessaire de prendre un parapluie dans votre valise !"
```

Les thoughtSignature visibles dans les corps complets sont des métadonnées opaques du fournisseur liées aux réponses ; elles ne constituent pas du texte de raisonnement à interpréter. Elles ne sont pas recopiées dans ces extraits centrés sur les outils.

## Test 7 — JSON du cas d’erreur, session indépendante

**11. GEMINI → APPLICATION — RÉPONSE demandant le géocodage du nom volontairement inexistant.**

```json
"functionCall": {
              "name": "getCoordonnees",
              "args": {
                "ville": "VilleInexistanteQzxv987654"
              },
              "id": "call_8002231450628647994"
            }
```

**12. OPEN-METEO → APPLICATION — RÉPONSE réelle HTTP 200 sans tableau results. Le statut HTTP est un succès de transport, mais ne signifie pas qu’une ville a été trouvée.**

```json
{"generationtime_ms":0.06067753}
```

**13. APPLICATION → GEMINI — NOUVELLE REQUÊTE avec le message de l’exception réelle de getCoordonnees. Le gestionnaire d’erreur de LangChain4j transmet ce résultat au modèle. Aucun appel météo n’a lieu pour cette ville.**

```json
"functionResponse":{"id":"call_8002231450628647994","name":"getCoordonnees","response":{"result":"Aucune coordonnée trouvée pour la ville : VilleInexistanteQzxv987654"}}
```

**14. GEMINI → APPLICATION — RÉPONSE finale expliquant qu’aucune prévision ne peut être vérifiée.**

```json
"text": "Je suis désolé, mais je n'ai pas trouvé les coordonnées de la ville \"VilleInexistanteQzxv987654\". Il m'est donc impossible de vérifier les prévisions météo pour cet endroit et de vous dire si vous aurez besoin d'un parapluie."
```

## Test 7 — Comptage complet des tokens pour une seule question

```text
Échange Gemini | Entrée | Sortie | Total déclaré
1 | 600 | 20 | 620
2 | 655 | 48 | 703
3 | 719 | 49 | 768
SOMME | 1974 | 117 | 2091
```

Cette somme provient des trois usageMetadata des réponses Gemini de la même session neuve Zzyzx, et non du seul dernier échange. Les champs de tokens de réflexion et de cache ne sont pas renseignés dans ces réponses ; je ne leur attribue pas une mesure inventée. Les appels HTTP Open-Meteo ne consomment pas de tokens Gemini directement, mais les résultats d’outils réinjectés et l’historique retransmis participent aux entrées des requêtes suivantes.

Pour économiser des tokens, je proposerais un nouvel outil previsionsPluieParVille(ville, nbJoursDebut, nbJours). Il enchaînerait le géocodage et la météo en Java puis retournerait un résultat concis. Gemini ferait normalement un seul appel d’outil au lieu de deux : une requête intermédiaire, une réponse et une retransmission de l’historique seraient évitées. Les deux requêtes HTTP Open-Meteo resteraient nécessaires, mais les coordonnées n’auraient plus à transiter par un échange LLM supplémentaire. Le gain exact devrait être mesuré ; je ne donne pas de faux chiffre. Ce nouvel outil n’est pas codé, conformément à la consigne.

## Code, vérifications et commits

MeteoTool reprend codeoutil1.html et codeoutil2.html du professeur, avec annotations, contrôles d’entrée, délais HTTP, encodage des noms de villes et traces des réponses réelles. L’interface AssistantMeteo est dans son propre fichier. Le builder utilise AiServices, MessageWindowChatMemory.withMaxMessages(20) et tools(new MeteoTool()). La classe autonome TestMeteo est incluse. logback.xml reprend le fichier fourni, et logRequestsAndResponses(true) est activé.

LangChain4j 1.22.0, google-genai 1.22.0-beta32 et Java 25 sont conservés ; Gson 2.15.0 et Logback 1.6.6 sont déclarés. Les clés restent dans GEMINI_KEY. Les Tests 1 à 6, infos.txt restauré et le PDF ont été préservés. La partie Web et les options ne font pas partie de ce dépôt de tests.

Vérifications : compilation Maven de toutes les classes ; exécution autonome de TestMeteo ; conversations réelles à un puis deux outils ; sessions neuves Zzyzx et ville inexistante avec capture JSON. Les incidents et les premières tentatives sont conservés séparément des traces retenues, sans correction des sorties a posteriori.

```text
60a2800 test7: geocodage et enchainement des deux outils
0f624eb test7: outil pluie Open-Meteo et conversation avec memoire
3081851 test6: RAG avec PDF et conversation interactive
694b5d6 test5: implementation du RAG avec LangChain4j
601f82c test4: comparaison des embeddings BGE et Gemini
7319f73 test3: traduction avec PromptTemplate
ccd1abb test2: calcul du cout en tokens Gemini
9956ef8 test1: premiers appels Gemini avec LangChain4j
```

Les étapes « un outil » (0f624eb) et « deux outils » (60a2800) ont chacune été suivies d’un push et d’une vérification du commit distant. La dernière étape regroupe le logging, les preuves brutes et cette préparation du bonus. Les fichiers de preuves complètent l’email, dont le corps contient déjà les résultats demandés.

Cordialement,
Yassine Atiki
