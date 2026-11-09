package ma.emsi.yassine.atiki;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiTokenUsage;
import dev.langchain4j.model.output.TokenUsage;

public class Test2Couts {

    public static void main(String[] args) {

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        String question = "Quelle est la capitale du Maroc ?";

        ChatResponse reponse = model.chat(UserMessage.from(question));

        TokenUsage usage = reponse.tokenUsage();

        int tokensEntree = usage.inputTokenCount();
        int tokensSortie = usage.outputTokenCount();

        int tokensReflexion = 0;

        if (usage instanceof GoogleGenAiTokenUsage usageGemini
                && usageGemini.thoughtsTokenCount() != null) {
            tokensReflexion = usageGemini.thoughtsTokenCount();
        }

        System.out.println("Question : " + question);
        System.out.println("Réponse : " + reponse.aiMessage().text());

        System.out.println("Tokens d'entrée : " + tokensEntree);
        System.out.println("Tokens de sortie : " + tokensSortie);
        System.out.println("Tokens de raisonnement : " + tokensReflexion);
        System.out.println("Total déclaré : " + usage.totalTokenCount());

        double prixEntree = 0.30;
        double prixSortie = 2.50;

        double cout =
                (tokensEntree * prixEntree
                        + (tokensSortie + tokensReflexion) * prixSortie)
                        / 1_000_000.0;

        long nombreRequetes = (long) Math.ceil(1.0 / cout);

        System.out.printf("Coût de la requête : %.8f $%n", cout);
        System.out.println("Requêtes pour dépenser 1 $ : "
                + nombreRequetes);
    }
}
