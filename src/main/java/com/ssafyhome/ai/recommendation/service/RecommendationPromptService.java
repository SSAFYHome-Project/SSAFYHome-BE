package com.ssafyhome.ai.recommendation.service;

import com.ssafyhome.ai.recommendation.dto.RecommendationDto;
import org.springframework.stereotype.Service;

@Service
public class RecommendationPromptService {

    public String createChatbotPrompt(RecommendationDto dto, String schoolOrWorkAddress) {
        String childrenAgeLine = (dto.getChildrenAge() == null || dto.getChildrenAge().isEmpty())
                ? ""
                : String.format("- 아이 나이: %s\n", dto.getChildrenAge());

        return String.format(
                "다음은 한 사용자의 주거 조건입니다.\n\n" +
                        "- 희망 거주 지역: %s\n" +
                        "- 거래 유형: %s\n" +
                        "- 예산: %s\n" +
                        "- 평수: %s\n" +
                        "- 가족 구성: %s\n" +
                        "%s" + // 아이 나이 줄 (조건부 출력)
                        "- 주요 교통수단: %s\n" +
                        "- 야간 귀가 여부: %s\n" +
                        "- 선호하는 분위기: %s\n" +
                        "- 학교 또는 직장 주소: %s (※ 이 항목이 비어 있다면 고려하지 않아도 됩니다.)\n\n" +
                        "이 사용자의 조건을 고려했을 때, %s ㅌ내에서 거주하기에 적합한 동네를 1~2곳 추천해 주세요.\n" +
                        "추천 동네명과 함께, 각 동네가 이 조건들과 어떻게 잘 맞는지 이유도 구체적으로 설명해 주세요.\n" +
                        "특히 학교나 직장 주소가 제공된 경우, 해당 지역과의 거리나 접근성도 함께 고려해 주세요.\n\n" +
                        "답변은 400자 이내로 사용자에게 설명하는 친절하고 자연스러운 말투로 작성해 주세요. (직관적인 이모티콘을 사용해도 좋아요!)",
                dto.getRegion(),
                dto.getTransactionType(),
                dto.getPriceRange(),
                dto.getAreaSize(),
                dto.getFamilyType(),
                childrenAgeLine,
                dto.getTransport(),
                dto.getNightReturn(),
                dto.getMood(),
                (schoolOrWorkAddress == null || schoolOrWorkAddress.isEmpty()) ? "제공되지 않음" : schoolOrWorkAddress,
                dto.getRegion()
        );
    }

}
