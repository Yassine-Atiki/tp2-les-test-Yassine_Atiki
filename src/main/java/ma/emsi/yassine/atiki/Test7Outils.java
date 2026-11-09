package ma.emsi.yassine.atiki;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import ma.emsi.yassine.atiki.outil.meteo.MeteoTool;

import java.time.Duration;
import java.util.Scanner;

public class Test7Outils {
    public static void main(String[] args) {
        String cle = System.getenv("GEMINI_KEY");
        if (cle == null || cle.isBlank()) {
            throw new IllegalStateException("La variable GEMINI_KEY est absente.");
        }
        String nomModele = System.getenv().getOrDefault("GEMINI_MODEL", "gemini-3.5-flash-lite");
        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(cle)
                .modelName(nomModele)
                .temperature(0.7)
                .timeout(Duration.ofSeconds(60))
                .maxRetries(0)
                .build();

        AssistantMeteo assistant = AiServices.builder(AssistantMeteo.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .tools(new MeteoTool())
                .build();

        System.out.println("Test 7 — modèle : " + nomModele);
        System.out.println("Tapez fin ou exit pour terminer.");
        // Même principe que conversation.html : un assistant conservé entre les questions.
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String question = scanner.nextLine();
                if ("fin".equalsIgnoreCase(question) || "exit".equalsIgnoreCase(question)) break;
                if (question.isBlank()) continue;
                System.out.println("Question : " + question);
                String reponse = assistant.chat(question);
                System.out.println("Réponse : " + reponse);
            }
        }
    }
}
