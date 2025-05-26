package com.ssafyhome.ai.recommendation.service;

import com.ssafyhome.ai.recommendation.dto.RecommendationDto;
import com.ssafyhome.common.util.UserUtils;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.SchoolWorkAddress;
import com.ssafyhome.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final OpenAiChatModel openAiChatModel;
    private final UserService userService;
    private final RecommendationPromptService promptService;

    public String recommendationArea(CustomUserDetails userDetails, RecommendationDto dto) {
        try {
            String userEmail = UserUtils.getEmailFromUserDetails(userDetails);
            log.info("Processing recommendation request for user: {}", userEmail);

            // 유저 DB에서 학교/직장 주소 조회
            SchoolWorkAddress address = userService.findSchoolAndWorkAddress(userEmail);

            // 프롬프트 생성
            String promptText = promptService.createChatbotPrompt(dto, address.getWorkAddress(), address.getSchoolAddress());
            log.debug("Generated prompt: {}", promptText);

            // GPT 요청 메시지 구성
            UserMessage userMessage = new UserMessage(promptText);
            List<Message> messages = List.of(userMessage);

            ChatOptions options = ChatOptions.builder()
                    .temperature(0.7)
                    .maxTokens(500)
                    .build();

            Prompt prompt = new Prompt(messages, options);

            String result = openAiChatModel.call(prompt).getResult().getOutput().getText();
            log.info("Successfully generated recommendation for user: {}", userEmail);

            return result;

        } catch (RestClientException e) {
            log.error("OpenAI API call failed: {}", e.getMessage(), e);
            throw new RuntimeException("AI 서비스 연결에 실패했습니다. 잠시 후 다시 시도해주세요.", e);
        } catch (Exception e) {
            log.error("Unexpected error in recommendation service: {}", e.getMessage(), e);
            throw new RuntimeException("추천 서비스 처리 중 오류가 발생했습니다.", e);
        }
    }
}
