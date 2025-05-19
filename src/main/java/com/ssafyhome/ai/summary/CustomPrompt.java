package com.ssafyhome.ai.summary;

import lombok.Setter;
import org.springframework.ai.chat.messages.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.ModelRequest;

import java.util.List;

@RequiredArgsConstructor
public class CustomPrompt implements ModelRequest<List<Message>> {

    private final List<Message> messages;
    @Setter
    private ChatOptions modelOptions;

    @Override
    public ChatOptions getOptions() {
        return modelOptions;
    }

    @Override
    public List<Message> getInstructions() {
        return messages;
    }

}
