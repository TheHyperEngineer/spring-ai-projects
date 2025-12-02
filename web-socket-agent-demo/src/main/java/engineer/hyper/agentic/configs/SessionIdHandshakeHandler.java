package engineer.hyper.agentic.configs;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class SessionIdHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        // Read "session-id" from headers set by the STOMP client connectHeaders
        List<String> ids = request.getHeaders().get("session-id");
        String id = (ids != null && !ids.isEmpty()) ? ids.get(0) : UUID.randomUUID().toString();

        // Return a Principal whose name is the per-tab session UUID
        return () -> id;
    }
}