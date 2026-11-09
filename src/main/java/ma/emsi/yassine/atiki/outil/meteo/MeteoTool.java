package ma.emsi.yassine.atiki.outil.meteo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Scanner;
import java.net.URI;

/** Outil de Richard Grin, avec validation des entrées et traces des appels réels. */
public class MeteoTool {
    private final double seuilPluie;

    public MeteoTool() {
        this(25.0);
    }

    public MeteoTool(double seuilPluie) {
        if (!Double.isFinite(seuilPluie) || seuilPluie < 0 || seuilPluie > 100) {
            throw new IllegalArgumentException("Le seuil doit être compris entre 0 et 100.");
        }
        this.seuilPluie = seuilPluie;
    }

    @Tool("""
    Retourne true si la probabilité d'avoir de la pluie pour la ville dont on donne la latitude et la longitude
    est importante durant une période de nbJours jours. La période commence nbJourDebut jours après aujourd'hui.
    Par exemple, si nbJoursDebut = 1 et nbJours = 2, la période de 2 jours commence demain (1 jour après aujourd'hui). 
    Si nbJoursDebut = 0 et nbJours = 3, la période de 3 jours commence aujourd'hui.
    Retourne false sinon.
    """)
    public boolean isPluiePrevue(
           @P(value="Latitude", name="latitude") double latitude, 
           @P(value="Longitude", name="longitude") double longitude,
           @P(value="Nombre de jours après aujourd'hui pour indiquer le début de la période", name="nbJoursDebut") int nbJoursDebut,
           @P(value="Nombre de jours de la période", name="nbJours") int nbJours) {
        // Seuil au-delà duquel la probabilité d'avoir de la pluie est suffisante pour prendre un parapluie.
        // A modifier éventuellement, en particulier pour faire des tests.
        validerPeriode(nbJoursDebut, nbJours);
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
                || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Coordonnées invalides.");
        }

        try {
            // Construire l'URL de l'API météo en utilisant les coordonnées et la période spécifiée
            String baseUrl = "https://api.open-meteo.com/v1/forecast";
            int finPeriode = Math.min(nbJoursDebut + nbJours, 7); // Limite de 7 jours retenue par le TP
            String apiUri = String.format(Locale.US,
                "%s?latitude=%f&longitude=%f&daily=precipitation_probability_max&forecast_days=%d",
                baseUrl, latitude, longitude, finPeriode);

            String response = lireApi(apiUri);

            // Parser la réponse JSON qui a le format suivant :
            // {
            //   "latitude": 41.875,
            //   "longitude": 12.5,
            //   "generationtime_ms": 0.123,
            //   ...
            //   "daily": {
            //     "time": ["2024-06-01", "2024-06-02", "2024-06-03", ...],
            //     "precipitation_probability_max": [20, 50, 80, ...]
            //   }
            // }
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

//            System.out.println("Réponse de l'API météo : " + response);

            JsonObject daily = jsonResponse.getAsJsonObject("daily");
            if (daily == null || !daily.has("precipitation_probability_max")) {
                throw new IllegalStateException("Prévisions absentes de la réponse météo.");
            }
            JsonArray probabilites = daily.getAsJsonArray("precipitation_probability_max");
            return pluieDansPeriode(probabilites, nbJoursDebut, nbJours, seuilPluie);
        } catch (IOException e) {
            // En cas d'erreur, on considère qu'on ne peut pas déterminer la météo
            throw new RuntimeException("Erreur lors de la récupération de la météo pour ("
                    + latitude + ", " + longitude + ") : " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    static void validerPeriode(int debut, int jours) {
        // Ne pas tronquer silencieusement un séjour au-delà des 7 jours du TP.
        if (debut < 0 || debut > 6 || jours < 1 || jours > 7 || debut + jours > 7) {
            throw new IllegalArgumentException("Période hors des 7 jours du TP (aujourd'hui = 0).");
        }
    }

    static boolean pluieDansPeriode(JsonArray probabilites, int debut, int jours, double seuil) {
        validerPeriode(debut, jours);
        if (probabilites == null || probabilites.size() < debut + jours) {
            throw new IllegalStateException("Période incomplète dans la réponse météo.");
        }
        boolean pluie = false;
        for (int i = debut; i < debut + jours; i++) {
            if (probabilites.get(i).isJsonNull()) {
                throw new IllegalStateException("Probabilité indisponible pour le jour " + i);
            }
            double probabilite = probabilites.get(i).getAsDouble();
            if (!Double.isFinite(probabilite) || probabilite < 0 || probabilite > 100) {
                throw new IllegalStateException("Probabilité invalide pour le jour " + i);
            }
            pluie |= probabilite > seuil;
        }
        return pluie;
    }

    private static String lireApi(String apiUri) throws IOException, URISyntaxException {
        HttpURLConnection connection = (HttpURLConnection) new URI(apiUri).toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);
        try {
            int status = connection.getResponseCode();
            java.io.InputStream flux = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
            String response;
            if (flux == null) {
                response = "";
            } else {
                try (Scanner scanner = new Scanner(flux, java.nio.charset.StandardCharsets.UTF_8)) {
                    response = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                }
            }
            System.out.println("API GET " + apiUri);
            System.out.println("HTTP " + status + " JSON BRUT : " + response);
            if (status != 200) {
                throw new IOException("HTTP " + status + " : " + response);
            }
            return response;
        } finally {
            connection.disconnect();
        }
    }

    @Tool("""
            Retourne la latitude puis la longitude de la première ville trouvée par Open-Meteo.
            Le nom doit être en anglais. On peut préciser le pays, par exemple Rabat,Morocco.
            Utiliser ces coordonnées pour appeler ensuite isPluiePrevue.
            Une ville introuvable provoque une erreur ; ne pas inventer ses coordonnées.
            """)
    public double[] getCoordonnees(@P(value="Nom de ville en anglais, éventuellement suivi du pays", name="ville") String ville) {
        if (ville == null || ville.isBlank()) {
            throw new IllegalArgumentException("Le nom de ville ne peut pas être vide.");
        }
        try {
            String baseUrl = "https://geocoding-api.open-meteo.com/v1/search?name=";
            String apiUri = baseUrl + java.net.URLEncoder.encode(ville, java.nio.charset.StandardCharsets.UTF_8);

            String response = lireApi(apiUri);

//            System.out.println("Réponse de l'API de géocodage : " + response);

            // Parser la réponse JSON qui a le format suivant :
            // (On prend la première correspondance trouvée pour la ville et on retourne ses coordonnées)
            // {
            //  "results": [
            //    {
            //      "id": 3169070,
            //      "name": "Rome",
            //      "latitude": 41.89193,
            //      "longitude": 12.51133,
            Gson gson = new Gson();
            JsonObject jsonResponse = gson.fromJson(response, JsonObject.class);

            // Récupérer le tableau "results"
            if (jsonResponse.has("results")) {
                JsonArray results = jsonResponse.getAsJsonArray("results");

                if (!results.isEmpty()) {
                    // Récupérer le 1er élément du tableau results
                    JsonObject premierResultat = results.get(0).getAsJsonObject();

                    double latitude = premierResultat.get("latitude").getAsDouble();
                    double longitude = premierResultat.get("longitude").getAsDouble();
                    return new double[]{latitude, longitude};
                }
            }

            throw new RuntimeException("Aucune coordonnée trouvée pour la ville : " + ville);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la récupération des coordonnées pour la ville : " + ville, e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
