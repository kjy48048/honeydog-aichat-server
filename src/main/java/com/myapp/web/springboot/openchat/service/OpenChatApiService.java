package com.myapp.web.springboot.openchat.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.ImageResult;
import com.theokanning.openai.service.OpenAiService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     설명: 오픈 챗 API 서비스
 *     작성자: kimjinyoung
 *     작성일: 2023. 2. 21.
 * </pre>
 */
@Service
@Slf4j
public class OpenChatApiService {
    private final String OPENAI_TOKEN;   // 오픈 AI 토큰
    private final String OPENAI_MODEL_ID;   // 오픈 AI 모델
    private OpenAiService aiService;
    private final Map<String, List<ChatMessage>> messagesMap = new HashMap<>(); // chatMessage....

    @Autowired
    public OpenChatApiService(@Value("${openai.token}") String openaiToken) {
        this.OPENAI_TOKEN = openaiToken;
        this.OPENAI_MODEL_ID = "gpt-3.5-turbo-0613";
    }

    @PostConstruct
    public void init() {
        this.aiService = new OpenAiService(OPENAI_TOKEN, Duration.ofSeconds(60));
    }

    /**
     * OPEN AI 메세지 보내기(예전 버전)
     * - OpenAiService createCompletion 사용
     * @param message 메세지
     * @param userName 유저명
     * @return 응답 메세지
     */
    public String sendMessageOld(String message, String userName) {
        String responseMessage = "";

        try {
            userName = StringUtils.hasText(userName) ? userName : "testUser";
            CompletionRequest completionRequest = CompletionRequest.builder()
                    .prompt(message)
                    .model(OPENAI_MODEL_ID)
                    .temperature(0D)
                    .maxTokens(2048)
                    // .echo(true)
                    .user(userName)
                    .build();
            CompletionResult completionResult = aiService.createCompletion(completionRequest);
            responseMessage = completionResult.getChoices().get(0).getText().trim();
            log.info("completionResult: {}", completionResult);
        } catch (Exception e) {
            responseMessage = "ChatGPT 메세지 전달 중 오류가 발생하였습니다.";
            log.error("send Open Chat Api Failed..., userName: {}, message: {}", userName, message, e);
            log.error("OPENAI_TOKEN: {}, OPENAI_MODEL_ID: {}", OPENAI_TOKEN, OPENAI_MODEL_ID);
        }
        return responseMessage;
    }

    /**
     * OPEN AI 메세지 보내기 테스트 버전...
     * - OpenAiService createChatCompletion 사용
     * - 유저명으로 이전 메세지 기억해서(메모리DB) 리스트로 보내는 방식으로 테스트...
     * @param message 메세지
     * @param userName 유저명
     * @return 응답 메세지
     */
    public String sendMessageTest(String message, String userName) {
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        List<ChatMessage> messages;

        if(messagesMap.containsKey(userName)) {
            messages = messagesMap.get(userName);
        } else {
            messages = new ArrayList<>();
        }
        messages.add(userMessage);
        ChatMessage responseMessage = this.sendMessageOperation(messages, userName);
        messages.add(responseMessage);
        messagesMap.put(userName, messages);

        return responseMessage.getContent();
    }

    /**
     * OPEN AI 메세지 보내기
     * - OpenAiService createChatCompletion 사용
     * @param message 메세지
     * @param userName 유저명
     * @return 응답 메세지
     */
    public String sendMessage(String message, String userName) {
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(userMessage);
        ChatMessage responseMessage = this.sendMessageOperation(messages, userName);
        return responseMessage.getContent();
    }

    private ChatMessage sendMessageOperation(List<ChatMessage> messages, String userName) {
        ChatMessage responseChatMessage;
        try {
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model(OPENAI_MODEL_ID)
                    .messages(messages)
                    .maxTokens(2048)
                    .build();
            ChatCompletionResult completionResult = aiService.createChatCompletion(chatCompletionRequest);
            responseChatMessage = aiService.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();

            log.info("completionResult: {}", completionResult);
        } catch (Exception e) {
            log.error("send Open Chat Api Failed..., userName: {}, messages: {}", userName, messages, e);
            log.error("OPENAI_TOKEN: {}, OPENAI_MODEL_ID: {}", OPENAI_TOKEN, OPENAI_MODEL_ID);
            throw e;
        }
        return responseChatMessage;
    }

    /**
     * 오픈 AI 이미지 만들기
     * @param message 메세지
     * @param userName 유저명
     * @return 만들어진 이미지 링크
     */
    public String findImageLink(String message, String userName){
        userName = StringUtils.hasText(userName) ? userName : "testUser";
        CreateImageRequest createImageRequest = CreateImageRequest.builder()
                .prompt(message)
                .size("1024x1024")
                .user(userName)
                .build();

        ImageResult imageResult = aiService.createImage(createImageRequest);
        log.info("imageResult: {}", imageResult);
        return imageResult.getData().get(0).getUrl().trim();
    }
}
