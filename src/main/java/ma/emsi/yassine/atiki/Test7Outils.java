package ma.emsi.yassine.atiki;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolExecutionErrorHandler;
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

        AssistantMeteo assistant = creerAssistant(model);

        System.out.println("Test 7 — modèle : " + nomModele);
        System.out.println("Tapez fin ou exit pour terminer, nouveau pour recommencer sans historique.");
        // Même principe que conversation.html : un assistant conservé entre les questions.
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String question = scanner.nextLine();
                if ("fin".equalsIgnoreCase(question) || "exit".equalsIgnoreCase(question)) break;
                if (question.isBlank()) continue;
                if ("nouveau".equalsIgnoreCase(question)) {
                    assistant = creerAssistant(model);
                    System.out.println("Nouvelle conversation (mémoire vide).");
                    continue;
                }
                System.out.println("Question : " + question);
                String reponse = assistant.chat(question);
                System.out.println("Réponse : " + reponse);
            }
        }
    }

    private static AssistantMeteo creerAssistant(ChatModel model) {
        return AiServices.builder(AssistantMeteo.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .tools(new MeteoTool())
                // Transmettre les véritables erreurs des outils au LLM pour qu'il les explique.
                .toolExecutionErrorHandler(ToolExecutionErrorHandler.sendExceptionMessageToLlm())
                .build();

    }
}
