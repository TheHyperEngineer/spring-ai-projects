// src/main/java/engineer/hyper/agentic/ws/ChatMessage.java
package engineer.hyper.agentic.ws;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessage {
    private String role;    // "user", "system", or agent name
    private String content; // text
}