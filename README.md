# TP 2 — Quelques tests avec LangChain4j

Yassine Atiki — projet Maven Java 25, Tests 1 à 7 du TP de Richard Grin.

Énoncé : http://richard.grin.free.fr/emsi/casablanca-ia/tp/tp2-langchain4j/index.html

## Exécuter

Prérequis : JDK 25 et Maven sur le PATH. La variable d'environnement `GEMINI_KEY`
doit être présente pour les programmes Gemini ; aucune clé n'est enregistrée dans
le projet. Le Test 7 utilise `gemini-3.5-flash-lite`, réellement disponible pendant
les expériences ; `GEMINI_MODEL` permet de choisir un autre modèle disponible.

Depuis la racine du projet, dans PowerShell :

```powershell
mvn clean compile
.\scripts\executer.ps1 -Test TestMeteo -Local
.\scripts\executer.ps1 -Test TestMeteo
.\scripts\executer.ps1 -Test Test7Outils
```

`TestMeteo -Local` vérifie les limites sans aucun accès réseau. Sans `-Local`, il
interroge réellement le géocodage et les prévisions (Paris, Rabat, Zzyzx, ville
inexistante, Sydney, plusieurs dates et seuils). `Test7Outils` lance une conversation
avec Gemini : `fin` ou `exit` termine ; `nouveau` recrée une mémoire vide.
Le script conserve la racine du projet comme dossier courant, indispensable aux
Tests 5 et 6 pour retrouver `infos.txt` et `langchain4j.pdf`.

Exemples du TP :

```text
J'ai prévu d'aller aujourd'hui à la ville dont la latitude est 48.85 et la longitude est 2.35 pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?
Finalement, je ne vais partir que demain. Est-ce que tu me conseilles de prendre un parapluie ?
nouveau
J'ai prévu d'aller aujourd'hui à la ville de Zzyzx pour un séjour de 3 jours. Est-ce que tu me conseilles de mettre un parapluie dans ma valise ?
```

## Code et résultats

- Tests 1 à 6 : sources antérieures conservées. La question provisoire météo du
  Test 2 est sauvegardée dans un patch, puis sa question initiale a été rétablie.
  `infos.txt` contient bien Paris. Les anciens tests n'ont pas été réexécutés
  inutilement ; leurs résultats fournis dans le chat précédent sont archivés.
- Test 7 : `AssistantMeteo`, `Test7Outils`, `outil/meteo/MeteoTool` et
  `outil/meteo/TestMeteo`. Les deux méthodes de MeteoTool reprennent les codes
  officiels du professeur, avec les adaptations décrites dans
  [PROVENANCE.md](preuves-bonus/PROVENANCE.md).
- Le seuil est `probabilite > 25`, pas `>= 25`. L'outil respecte les décalages
  aujourd'hui=0/demain=1 et refuse les séjours dépassant les 7 jours du TP.
  Les dates sont celles du JSON Open-Meteo en GMT, comme le code officiel.
- Logging : `src/main/resources/logback.xml` reprend la configuration fournie.
  `.logRequestsAndResponses(true)` est activé. `TraceJsonGemini` ajoute les vrais
  corps JSON HTTP via un intercepteur du SDK ; les en-têtes et la clé sont exclus.

## Bonus prêt à relire

Ouvrir [EMAIL-BONUS.html](preuves-bonus/EMAIL-BONUS.html) et copier son contenu dans
le **corps de l'email**, en conservant le gras des explications. Le même texte est
disponible en [Markdown](preuves-bonus/EMAIL-BONUS.md). L'objet contient exactement
`TP 2 - tests terminé`. Le groupe et le numéro étudiant restent à compléter avant
envoi. Aucun email n'a été envoyé.

L'email comprend les réponses intégrales des Tests 1, 5 et 6, les six couples et
scores BGE/Gemini, la réponse initiale complète sans outils, les conversations
Test 7, les extraits nouveaux des deux outils et du cas d'erreur, les explications
en gras, ainsi que le comptage de **2 091 tokens** (1 974 entrée, 117 sortie) pour
une seule question Zzyzx avec trois échanges Gemini. Le nouvel outil proposé pour
réduire les échanges n'est pas implémenté, comme demandé.

Preuves authentiques dans `preuves-bonus/` :

- `historique/` : sorties fournies par l'étudiant dans l'ancien chat et patch Test 2.
- `test7-un-outil.*`, `test7-deux-outils.*` : conversations avec prompts exacts,
  réponses intégrales, requêtes Open-Meteo et métadonnées d'exécution.
- `test7-api-deux-outils.*` : exécution de TestMeteo, seuils et dates.
- `test7-logging-zzyzx.*`, `test7-logging-erreur.*` : nouvelles sessions avec logs.
- `json-bruts/` : corps extraits littéralement des traces, index, lignes d'origine
  et empreintes SHA-256. `tokens-zzyzx.json` additionne les vrais usageMetadata.
- `sources/` : pages officielles consultées et requêtes directes réelles.
- `tentative-*` et `avant-consigne-*` : incidents conservés, pas des validations.

Les prévisions dépendent de la date et peuvent évoluer. Une première relance avait
mal conservé la durée : une consigne système explicite a été ajoutée et validée.
Une longue conversation cumulant toutes les villes a aussi rempli la fenêtre
imposée de 20 messages et provoqué un HTTP 400 Gemini lié à l'ordre des rôles.
Utiliser `nouveau` entre expériences indépendantes ; la relance sur le départ
demain garde, elle, la mémoire de la durée. Une réponse incomplète de l'essai à un
outil est conservée telle quelle dans les preuves et signalée dans l'email.

Versions : LangChain4j 1.22.0, modules 1.22.0-beta32 conservés ; Gson 2.15.0 et
Logback 1.6.6 ajoutés après consultation de Maven Central. Aucun changement
d'identité Git. Aucune partie Web ou option streaming n'a été développée.
