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

public class Test5Rag1 {

    public static void main(String[] args) {

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        // 1. Charger le fichier de connaissances
        Document document =
                FileSystemDocumentLoader.loadDocument("infos.txt");

        // 2. Créer la base vectorielle en mémoire
        InMemoryEmbeddingStore<TextSegment> embeddingStore =
                new InMemoryEmbeddingStore<>();

        // 3. Transformer et stocker les documents
        EmbeddingStoreIngestor.ingest(
                List.of(document),
                embeddingStore
        );

        // 4. Créer le système de recherche
        ContentRetriever retriever =
                EmbeddingStoreContentRetriever.from(embeddingStore);

        // 5. Créer l'assistant avec une mémoire et le RAG
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(
                        MessageWindowChatMemory.withMaxMessages(10)
                )
                .contentRetriever(retriever)
                .build();

        // 6. Tester
        String question = "Comment s'appelle le chat de Pierre ?";

        System.out.println("=== Avec RAG ===");
        System.out.println("Question : " + question);
        System.out.println("Réponse : " + assistant.chat(question));
    }
}
