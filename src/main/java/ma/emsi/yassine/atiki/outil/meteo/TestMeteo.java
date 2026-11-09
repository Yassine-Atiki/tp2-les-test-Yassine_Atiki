package ma.emsi.yassine.atiki.outil.meteo;

import com.google.gson.JsonParser;
import java.util.Arrays;

/** Test autonome de l'outil, sans aucun appel au LLM. */
public class TestMeteo {
    public static void main(String[] args) {
        // Données de contrôle locales, explicitement distinctes des prévisions réelles.
        var valeurs = JsonParser.parseString("[25,26,0,100,10,0,0]").getAsJsonArray();
        verifier(!MeteoTool.pluieDansPeriode(valeurs, 0, 1, 25), "25 % ne dépasse pas 25 %");
        verifier(MeteoTool.pluieDansPeriode(valeurs, 1, 1, 25), "26 % dépasse 25 %");
        verifier(!MeteoTool.pluieDansPeriode(valeurs, 2, 1, 25), "décalage du début respecté");
        verifier(MeteoTool.pluieDansPeriode(valeurs, 0, 4, 25), "au moins un jour pluvieux");
        verifier(!MeteoTool.pluieDansPeriode(valeurs, 0, 7, 100), "seuil 100 : toujours false");
        erreurAttendue(() -> MeteoTool.validerPeriode(-1, 3));
        erreurAttendue(() -> MeteoTool.validerPeriode(0, 0));
        erreurAttendue(() -> MeteoTool.validerPeriode(5, 3));
        erreurAttendue(() -> MeteoTool.pluieDansPeriode(
                JsonParser.parseString("[null]").getAsJsonArray(), 0, 1, 25));
        erreurAttendue(() -> MeteoTool.pluieDansPeriode(
                JsonParser.parseString("[]").getAsJsonArray(), 0, 1, 25));
        System.out.println("Contrôles locaux : OK (données de contrôle, pas de météo simulée).");
        if (Arrays.asList(args).contains("--local")) return;

        MeteoTool outil = new MeteoTool();
        for (String ville : new String[]{"Paris", "Rabat,Morocco", "Zzyzx"}) {
            double[] coordonnees = outil.getCoordonnees(ville);
            System.out.println("Coordonnées de " + ville + " : " + Arrays.toString(coordonnees));
            verifier(coordonnees.length == 2, "latitude et longitude attendues");
        }
        try {
            outil.getCoordonnees("VilleInexistanteQzxv987654");
            throw new AssertionError("La ville de contrôle ne doit pas exister.");
        } catch (RuntimeException e) {
            verifier(e.getMessage().contains("Aucune coordonnée"), "erreur de ville introuvable attendue");
            System.out.println("Ville inexistante : " + e.getMessage());
        }
        tester(outil, "Paris aujourd'hui, 3 jours", 48.85, 2.35, 0, 3);
        tester(outil, "Paris demain, 3 jours", 48.85, 2.35, 1, 3);
        tester(outil, "Paris dans 2 jours, 3 jours", 48.85, 2.35, 2, 3);
        tester(outil, "Rabat aujourd'hui, 3 jours", 34.01325, -6.83255, 0, 3);
        tester(outil, "Sydney aujourd'hui, 3 jours", -33.86785, 151.20732, 0, 3);
        tester(new MeteoTool(0), "Paris seuil 0", 48.85, 2.35, 0, 3);
        tester(new MeteoTool(100), "Paris seuil 100", 48.85, 2.35, 0, 3);
    }

    private static void tester(MeteoTool outil, String cas, double lat, double lon, int debut, int jours) {
        System.out.println("CAS : " + cas);
        System.out.println("isPluiePrevue = " + outil.isPluiePrevue(lat, lon, debut, jours));
    }

    private static void verifier(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }

    private static void erreurAttendue(Runnable action) {
        try {
            action.run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erreur attendue : " + e.getMessage());
            return;
        }
        throw new AssertionError("Une entrée invalide devait être rejetée.");
    }
}
