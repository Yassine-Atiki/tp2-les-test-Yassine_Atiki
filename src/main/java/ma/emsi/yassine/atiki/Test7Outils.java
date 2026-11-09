package ma.emsi.yassine.atiki;

import com.google.genai.Client;
import com.google.genai.types.ClientOptions;
import com.google.genai.types.HttpOptions;
import okhttp3.OkHttpClient;

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
        // Le logger du modèle affiche des objets Java. Cet intercepteur ajoute les JSON
        // réellement transmis au service Gemini, nécessaires pour l'analyse du bonus.
        Client client = Client.builder()
                .apiKey(cle)
                .httpOptions(HttpOptions.builder().timeout(60000).build())
                .clientOptions(ClientOptions.builder().customHttpClient(
                        new OkHttpClient.Builder().addInterceptor(new TraceJsonGemini()).build()).build())
                .build();
        ChatModel model = GoogleGenAiChatModel.builder()
                .client(client)
                .apiKey(cle)
                .modelName(nomModele)
                .temperature(0.7)
                .timeout(Duration.ofSeconds(60))
                .maxRetries(0)
                .logRequestsAndResponses(true)
                .build();

        AssistantMeteo assistant = creerAssistant(model);

        System.out.println("Test 7 — modèle : " + nomModele);
        System.out.println("Tapez fin ou exit pour terminer, nouveau pour recommencer sans historique.");
        // Même principe que conversation.html : un assistant conservé entre les questions.
        try (client; Scanner scanner = new Scanner(System.in)) {
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
