package com.myapp.web.springboot.functionchat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.web.springboot.assistant.dto.field.ToolOutput;
import com.myapp.web.springboot.functionchat.dto.TarotListResponseDto;
import com.myapp.web.springboot.functionchat.enums.FunctionName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <pre>
 *      설명: Function 채팅 응답용 출력물 관리 서비스
 *      작성자: kimjinyoung
 *      작성일: 2024. 03. 25.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class FunctionChatSystemService {
    private final TarotService tarotService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 챗gpt에서 function 채팅을 했을 때 필요한 값 전달
     * @param name function 명
     * @param arguments 함수에 사용할 입력값
     * @return 도구 응답용 출력물
     */
    public ToolOutput getToolOutput(String name, String arguments, String toolCallId) {
        ToolOutput toolOutput = new ToolOutput();
        toolOutput.setToolCallId(toolCallId);

        if(FunctionName.RANDOM_TAROT.equals(FunctionName.fromFunctionName(name))) {
            // 타로 처리...
            try {
                JsonNode rootNode = objectMapper.readTree(arguments);

                // "requestNumber" 키의 값을 추출
                JsonNode requestNumberNode = rootNode.get("requestNumber");
                if (requestNumberNode != null) {
                    long requestNumber = requestNumberNode.asLong();
                    TarotListResponseDto responseDto = tarotService.getRandomTarots(requestNumber);
                    log.info("requestNumber: {}", requestNumber);
                    toolOutput.setOutput(objectMapper.writeValueAsString(responseDto));
                }
            } catch (Exception e) {
                log.error("getToolOutputs error: {}", e.getMessage(), e);
            }

        }

        return toolOutput;
    }
}
