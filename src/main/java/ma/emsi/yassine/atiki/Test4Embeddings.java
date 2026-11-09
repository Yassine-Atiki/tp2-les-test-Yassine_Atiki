package ma.emsi.yassine.atiki;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;

public class Test4Embeddings {

    public static void main(String[] args) {

        // Modèle local
        EmbeddingModel modeleBge =
                new BgeSmallEnV15EmbeddingModel();

        // Modèle externe Gemini
        EmbeddingModel modeleGemini =
                GoogleGenAiEmbeddingModel.builder()
                        .apiKey(System.getenv("GEMINI_KEY"))
                        .modelName("gemini-embedding-001")
                        .taskType(
                                GoogleGenAiEmbeddingModel
                                        .TaskTypeEnum.SEMANTIC_SIMILARITY
                        )
                        .timeout(Duration.ofSeconds(30))
                        .build();

        String[][] couples = {
                {
                        "Le chat dort sur le canapé.",
                        "Un félin se repose sur le sofa."
                },
                {
                        "Il fait très chaud aujourd'hui.",
                        "Il fait très froid aujourd'hui."
                },
                {
                        "Je programme une application Java.",
                        "Je développe un logiciel en Java."
                },
                {
                        "La voiture roule sur l'autoroute.",
                        "Je prépare une salade de tomates."
                },
                {
                        "L'étudiant révise pour son examen.",
                        "Un élève se prépare à une épreuve."
                },
                {
                        "Le ciel est couvert et il pleut.",
                        "Une banque approuve un prêt immobilier."
                }
        };

        for (int i = 0; i < couples.length; i++) {

            String phrase1 = couples[i][0];
            String phrase2 = couples[i][1];

            Embedding bge1 = modeleBge.embed(phrase1).content();
            Embedding bge2 = modeleBge.embed(phrase2).content();

            double similariteBge =
                    CosineSimilarity.between(bge1, bge2);

            Embedding gemini1 =
                    modeleGemini.embed(phrase1).content();
            Embedding gemini2 =
                    modeleGemini.embed(phrase2).content();

            double similariteGemini =
                    CosineSimilarity.between(gemini1, gemini2);

            System.out.println("\nCouple " + (i + 1));
            System.out.println("Phrase 1 : " + phrase1);
            System.out.println("Phrase 2 : " + phrase2);

            System.out.printf(
                    "Similarité BGE : %.4f%n",
                    similariteBge
            );

            System.out.printf(
                    "Similarité Gemini : %.4f%n",
                    similariteGemini
            );
        }
    }
}
