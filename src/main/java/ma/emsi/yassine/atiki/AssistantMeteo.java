package ma.emsi.yassine.atiki;

import dev.langchain4j.service.SystemMessage;

/** Interface indépendante pour le Test 7, comme demandé dans le TP. */
public interface AssistantMeteo {
    @SystemMessage("""
            Réponds en français. Pour les prévisions de pluie, utilise les outils disponibles.
            Lorsqu'un utilisateur décale son départ, conserve la durée du séjour annoncée
            précédemment, sauf s'il la modifie explicitement. Aujourd'hui correspond à
            nbJoursDebut=0, demain à 1. Un séjour de 3 jours reste nbJours=3.
            Si une ville est donnée par son nom, appelle getCoordonnees puis isPluiePrevue
            avec les coordonnées retournées. En cas d'erreur de géocodage, explique
            la limite sans inventer de coordonnées ni contourner l'échec de l'outil.
            En cas d'erreur météo, explique la limite sans inventer de prévisions.
            Réponds aussi normalement aux questions qui ne concernent pas la météo.
            """)
    String chat(String prompt);
}
