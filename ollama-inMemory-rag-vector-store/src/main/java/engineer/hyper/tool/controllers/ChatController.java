package engineer.hyper.tool.controllers;

import engineer.hyper.tool.util.MarkdownHelper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/")
@RequiredArgsConstructor
class ChatController {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    @PostMapping("/api/chat")
    ResponseEntity<Output> chat(@RequestBody @Valid Input input,
                                @CookieValue(name = "X-CONV-ID", required = false) String convId) {
        String conversationId = convId == null ? UUID.randomUUID().toString() : convId;
        var response = this.chatClient.prompt()
                .user(input.prompt())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call().content();
        ResponseCookie cookie = ResponseCookie.from("X-CONV-ID", conversationId)
                .path("/")
                .maxAge(3600)
                .build();
        var htmlResponse = MarkdownHelper.toHTML(response);
        Output output = new Output(htmlResponse);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(output);
    }

    record Input(@NotBlank String prompt) {
    }

    record Output(String content) {
    }


    @GetMapping("/api/embedding")
    public Map<String, Object> embed(
            @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {

        float[] floats1 = embeddingModel.embed("My name is Siva");
        List<float[]> floats2 = embeddingModel.embed(List.of("My name is Siva", "I am a software engineer"));
        Document doc = new Document("My name is Siva", Map.of("source", "uploaded-docs"));
        float[] floats3 = embeddingModel.embed(doc);

        EmbeddingRequest req = new EmbeddingRequest(List.of("My name is Siva", "I am a software engineer"), null);
        EmbeddingResponse response = embeddingModel.call(req);

        EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(message));
        return Map.of("embedding", embeddingResponse);
    }

    @GetMapping("/api/search")
    ResponseEntity<Output> searchVectorStore(@RequestParam String query) {
        log.info("Searching vector store for query: {}", query);
        List<Document> results = vectorStore.similaritySearch(query);
        String content = results.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));
        Output output = new Output(content);
        return ResponseEntity.ok().body(output);
    }
}