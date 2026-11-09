package ma.emsi.yassine.atiki;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

public class Test3Traducteur {

    public static void main(String[] args) {

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(System.getenv("GEMINI_KEY"))
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        // Test 3.1 : un seul paramètre (texte)
        PromptTemplate template1 = PromptTemplate.from(
                "Traduis le texte suivant en anglais : {{it}}"
        );

        Prompt prompt1 = template1.apply(
                "Bonjour, comment allez-vous aujourd'hui ?"
        );

        String reponse1 = model.chat(prompt1.text());

        System.out.println("=== Test 3.1 : un paramètre ===");
        System.out.println("Prompt : " + prompt1.text());
        System.out.println("Réponse : " + reponse1);


        // Test 3.2 : deux paramètres (langue et texte)
        PromptTemplate template2 = PromptTemplate.from(
                "Traduis le texte suivant en {{langue}} : {{texte}}"
        );

        Prompt prompt2 = template2.apply(Map.of(
                "langue", "espagnol",
                "texte", "Bonjour, je suis étudiant en informatique."
        ));

        String reponse2 = model.chat(prompt2.text());

        System.out.println();
        System.out.println("=== Test 3.2 : deux paramètres ===");
        System.out.println("Prompt : " + prompt2.text());
        System.out.println("Réponse : " + reponse2);
    }
}
