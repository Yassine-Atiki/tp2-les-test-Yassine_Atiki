package ma.emsi.yassine.atiki;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.util.List;
import java.util.Scanner;

public class Test6Rag2 {

    public static void main(String[] args) {

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        // 1. Charger le PDF
        Document document =
                FileSystemDocumentLoader.loadDocument("langchain4j.pdf");

        // 2. Créer la base vectorielle en mémoire
        InMemoryEmbeddingStore<TextSegment> embeddingStore =
                new InMemoryEmbeddingStore<>();

        // 3. Découper et indexer le document
        EmbeddingStoreIngestor.ingest(
                List.of(document),
                embeddingStore
        );

        // 4. Créer le système de recherche
        ContentRetriever retriever =
                EmbeddingStoreContentRetriever.from(embeddingStore);

        // 5. Créer l'assistant avec mémoire
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(
                        MessageWindowChatMemory.withMaxMessages(10)
                )
                .contentRetriever(retriever)
                .build();

        // 6. Conversation interactive
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== RAG 2 : Conversation avec le PDF ===");
        System.out.println("Tapez 'exit' pour quitter.");

        while (true) {

            System.out.print("\nVous : ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String question = scanner.nextLine();

            if (question.equalsIgnoreCase("exit")) {
                break;
            }

            if (question.isBlank()) {
                continue;
            }

            String reponse = assistant.chat(question);

            System.out.println("Gemini : " + reponse);
        }

        scanner.close();
    }
}
