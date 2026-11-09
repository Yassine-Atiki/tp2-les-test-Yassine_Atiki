# Provenance et portée

Travail limité à « Quelques tests avec LangChain4j », Tests 1 à 7.
Aucune application Web ni option streaming ajoutée.

Énoncé : http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/index.html
Les pages officielles ont finalement été accessibles par HTTP. Les copies consultées
sont dans sources/. MeteoTool reprend codeoutil1.html ; le futur géocodage reprend
codeoutil2.html. L'interface autonome et le builder suivent l'énoncé et rag.html ;
la boucle de conversation suit conversation.html.

Adaptations du projet : package nominatif, seuil configurable par constructeur (25 %
par défaut, comparaison strictement >), validation des coordonnées et périodes,
refus d'un séjour incomplet au-delà des 7 jours retenus par le TP, délais HTTP,
fermeture des connexions, détection des probabilités nulles, traces HTTP brutes.
Les prévisions restent en GMT comme le code et l'URL officiels ; les dates réellement
retournées par Open-Meteo figurent dans chaque trace. On utilise l'indice du début
dans les données retournées, pas la date supposée connue du LLM.

AssistantMeteo ajoute une consigne système pour conserver la durée lors du report
du départ. Les premières tentatives, y compris une durée mal interprétée et une
hallucination d'outil mentionné trop tôt, sont conservées, sans les présenter comme
des validations. Une tentative initiale de capture a aussi subi un mauvais encodage
console ; la capture retenue utilise UTF-8 pour stdin/stdout/stderr.

Tests 1 à 6 : code et résultats antérieurs conservés. Seule la question provisoire
du Test 2 a été rétablie à « Quelle est la capitale du Maroc ? » ; le diff initial
est conservé dans historique/. Les réponses anciennes sont des sorties fournies
par l'étudiant dans le chat référencé, et non de nouvelles exécutions.

Versions : LangChain4j 1.22.0 et modules 1.22.0-beta32 conservés. Maven Central
indique désormais 1.23.0 / 1.23.0-beta33 ; aucune migration imposée aux tests validés.
Gson 2.15.0 est déclaré directement pour l'outil. Versions contrôlées sur Maven Central.
La documentation et les sources de la version locale GoogleGenAi ont été consultées :
https://docs.langchain4j.dev/integrations/language-models/google-genai/
https://docs.langchain4j.dev/tutorials/tools/
https://open-meteo.com/en/docs
https://open-meteo.com/en/docs/geocoding-api

La recherche de contrat Orbit a trouvé le géocodage mais une requête météo enregistrée
qui ne demandait pas precipitation_probability_max. Le contrat a donc été confirmé
avec la documentation officielle et de vrais appels. Les données du bonus sont
exclusivement réelles ; aucun mock n'est utilisé comme preuve. La commande
`postman status` n'est pas reconnue par le CLI installé (1.76.0) ; aucun workspace
Postman n'est nécessaire ni publié pour ce TP.
