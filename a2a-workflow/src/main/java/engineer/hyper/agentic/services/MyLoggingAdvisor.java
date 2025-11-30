package engineer.hyper.agentic.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.model.ModelOptionsUtils;

@Slf4j
public class MyLoggingAdvisor implements BaseAdvisor {

    private final int order;

    public MyLoggingAdvisor(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        print("REQUEST", chatClientRequest.prompt().getInstructions());
        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        print("RESPONSE", chatClientResponse.chatResponse().getResults());
        return chatClientResponse;
    }

    private void print(String label, Object object) {
        log.info("{}:{}\n", label, ModelOptionsUtils.toJsonString(object));
    }

}