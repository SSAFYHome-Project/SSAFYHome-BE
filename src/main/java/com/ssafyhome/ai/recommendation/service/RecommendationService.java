package com.ssafyhome.ai.recommendation.service;

import com.ssafyhome.ai.recommendation.dto.RecommendationDto;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final OpenAiChatModel openAiChatModel;
    private final UserService userService;
    private final RecommendationPromptService promptService;

    public String recommendationArea(CustomUserDetails userDetails, RecommendationDto dto) {
        String userEmail = userDetails.getUsername();

        // 유저 DB에서 학교/직장 주소 조회
        String schoolOrWorkAddress = userService.findSchoolOrWorkAddress(userEmail);

        // 프롬프트 생성
        String promptText = promptService.createChatbotPrompt(dto, schoolOrWorkAddress);

        // GPT 요청 메시지 구성
        UserMessage userMessage = new UserMessage(promptText);
        List<Message> messages = List.of(userMessage);

        ChatOptions options = ChatOptions.builder()
                .temperature(0.7)
                .maxTokens(500)
                .build();

        Prompt prompt = new Prompt(messages, options);
        return openAiChatModel.call(prompt).getResult().getOutput().getText();
    }
}
