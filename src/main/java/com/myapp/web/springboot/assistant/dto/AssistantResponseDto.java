package com.myapp.web.springboot.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 *     설명: 오픈AI 어시스턴트 응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 * 예)
 * {
 *     "object": "list",
 *     "data": [
 *         {
 *             "id": "asst_Oo7Lzg9P57SsH9Hfro6JVH6V",
 *             "object": "assistant",
 *             "created_at": 1707803487,
 *             "name": "AI 진영",
 *             "description": null,
 *             "model": "gpt-3.5-turbo",
 *             "instructions": "This GPT, a 30-year-old Korean programmer specializing in MBTI discussions, not only offers detailed explanations about MBTI, compatibility between different MBTI types, and assessments based on personality descriptions but also ensures communication in Korean when approached with queries in Korean. It is designed to provide responses in Korean to maintain cultural relevance and ease of understanding for Korean-speaking users. The GPT's responses will be tailored to reflect a deep understanding of both the MBTI framework and the nuances of the Korean language, including appropriate use of honorifics and a polite tone, ensuring engaging and respectful interactions.",
 *             "tools": [],
 *             "file_ids": [],
 *             "metadata": {}
 *         }
 *     ],
 *     "first_id": "asst_Oo7Lzg9P57SsH9Hfro6JVH6V",
 *     "last_id": "asst_Oo7Lzg9P57SsH9Hfro6JVH6V",
 *     "has_more": false
 * }
 */
@Data
@NoArgsConstructor
public class AssistantResponseDto {
    private String object;
    private List<AssistantResponseData> data;
    @JsonProperty("first_id")
    private String firstId;
    @JsonProperty("last_id")
    private String lastId;
    private String hasMore;
}
