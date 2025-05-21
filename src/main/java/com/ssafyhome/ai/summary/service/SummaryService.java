package com.ssafyhome.ai.summary.service;

import com.ssafyhome.ai.summary.dto.SummaryDto;
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
public class SummaryService {

    private final OpenAiChatModel openAiChatModel;

    public String summarizeArea(SummaryDto dto) {
        String content = String.format(
                "당신은 부동산 전문 분석가입니다.\n" +
                        "\n" +
                        "다음은 사용자가 선택한 특정 지역의 정보를 작성하는 요청입니다.\n" +
                        "- 응답은 **문단 단위로 구성된 설명 형식**으로 제공해주세요.\n" +
                        "- 그리고 이모티콘을 사용하여 사용자 친화적으로 작성해주세요.\n" +
                        "- 다음 항목을 반드시 포함하여 다음과 같은 양식으로 350자 이내로 대답해주세요:\n" +
                        "\n" +
                        "1. 실거래가 요약: 전체 매물 수, 최저가~최고가, 평균 가격\n" +
                        "2. 전용면적 특징: 평균 면적, 고층 매물 비중 등\n" +
                        "3. 안전 등급: 선호/양호/위험 등급 분포와 특이사항\n" +
                        "4. 교육 인프라: 반경 1km 내 초·중·고 학교 수\n" +
                        "5. 교통 접근성: 주요 지하철역과 거리\n" +
                        "6. 생활 인프라: 병원, 대형마트 등 유무\n" +
                        "7. 입지 분석 요약: 어떤 실거주자에게 적합한지 한 문장으로 정리\n" +
                        "\n" +
                        "다음 지역에 대해 요약해주세요:\n" +
                        "- 시도: %s\n" +
                        "- 시군구: %s\n" +
                        "- 읍면동: %s",
                dto.getSido(), dto.getSigun(), dto.getUmd());

        UserMessage userMessage = new UserMessage(content);
        List<Message> messages = List.of(userMessage);

        ChatOptions options = ChatOptions.builder()
                .temperature(0.7)
                .maxTokens(350)
                .build();

        Prompt prompt = new Prompt(messages.toString(), options);

        return openAiChatModel.call(prompt).getResult().getOutput().getText();
    }
}
