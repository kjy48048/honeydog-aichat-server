package com.myapp.web.springboot.assistant.controller;

import com.myapp.web.springboot.appchat.dto.AppChatSaveRequestDto;
import com.myapp.web.springboot.assistant.dto.*;
import com.myapp.web.springboot.assistant.dto.field.ToolOutputs;
import com.myapp.web.springboot.assistant.service.OpenAiAssistantApiService;
import com.myapp.web.springboot.assistant.service.OpenAiAssistantSystemService;
import com.myapp.web.springboot.common.enums.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * <pre>
 *     설명: 오픈AI 어시스턴트 API 컨트롤러
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/open-ai-assistant")
@Slf4j
public class OpenAiAssistantController {
    private final OpenAiAssistantApiService assistantApiService;
    private final OpenAiAssistantSystemService assistantSystemService;

    /**
     * 앱유저 신규 등록
     * @return 유저 UUID
     */
    @GetMapping("/assistants")
    public ResponseEntity<List<AssistantResponseData>> getAssistantList(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken) {
        log.info("=== getAssistantList ===");
        List<AssistantResponseData> assistantList = assistantApiService.getAssistantList(openAiToken);
        if(assistantList != null && assistantList.size() > 0) {
            log.info("=== assistantList size: {} ===", assistantList.size());
            return ResponseEntity.status(HttpStatus.CREATED).body(assistantList);
        } else {
            log.info("=== getAssistantList fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(assistantList);
        }
    }

    @PostMapping("/threads")
    public ResponseEntity<ThreadResponseDto> saveThread(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken) {
        log.info("=== saveThread ===");
        ThreadResponseDto responseDto = assistantApiService.createThread(openAiToken);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== saveThread success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== saveThread fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @GetMapping("/threads/{threadId}")
    public ResponseEntity<ThreadResponseDto> getThread(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken
            , @PathVariable String threadId) {
        log.info("=== getThread ===");
        ThreadResponseDto responseDto = assistantApiService.retrieveThread(openAiToken, threadId);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== getThread success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== getThread fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @PutMapping("/threads/{threadId}")
    public ResponseEntity<ThreadResponseDto> updateThread(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken
            , @PathVariable String threadId, @RequestBody Map<String, String> metadata) {
        log.info("=== updateThread ===");
        ThreadResponseDto responseDto = assistantApiService.modifyThread(openAiToken, threadId, metadata);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== updateThread success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== updateThread fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @DeleteMapping("/threads/{threadId}")
    public ResponseEntity<ThreadResponseDto> removeThread(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken
            , @PathVariable String threadId) {
        log.info("=== removeThread ===");
        ThreadResponseDto responseDto = assistantApiService.deleteThread(openAiToken, threadId);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== removeThread success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== removeThread fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @PostMapping("/threads/{threadId}/messages")
    public ResponseEntity<MessageResponseDto> createMessage(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken
            , @PathVariable String threadId, @RequestBody MessageRequestDto messageRequestDto) {
        log.info("=== createMessage ===");
        MessageResponseDto responseDto = assistantApiService.createMessage(openAiToken, threadId, messageRequestDto);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== createMessage success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== createMessage fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @GetMapping("/threads/{threadId}/messages")
    public ResponseEntity<MessageResponseListDto> getMessageList(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken
            , @PathVariable String threadId) {
        log.info("=== getMessageList ===");
        MessageResponseListDto responseDto = assistantApiService.getMessageList(openAiToken, threadId);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== getMessageList success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== createMessage fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @GetMapping("/threads/{threadId}/messages/{messageId}")
    public ResponseEntity<MessageResponseDto> retrieveMessage(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken
            , @PathVariable String threadId, @PathVariable String messageId) {
        log.info("=== retrieveMessage ===");
        MessageResponseDto responseDto = assistantApiService.retrieveMessage(openAiToken, threadId, messageId);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== retrieveMessage success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== retrieveMessage fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @PostMapping("/threads/{threadId}/runs")
    public ResponseEntity<RunsResponseDto> createRun(
            @RequestHeader(value = "OpenAI-Token", required = false) String openAiToken,
            @RequestHeader(value = "model", required = false) String model,
            @PathVariable String threadId, @RequestBody RunsRequestDto runsRequestDto) {
        log.info("=== createRun ===");
        RunsResponseDto responseDto = assistantApiService.createRun(openAiToken, model, threadId, runsRequestDto);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== createRun success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== createRun fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @GetMapping("/threads/{threadId}/runs/{runId}/steps")
    public ResponseEntity<RunsResponseStepDto> getRunsListSteps(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken
            , @PathVariable String threadId, @PathVariable String runId) {
        log.info("=== getRunsListSteps ===");
        RunsResponseStepDto responseDto = assistantApiService.getRunsListSteps(openAiToken, threadId, runId);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== getRunsListSteps success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== getRunsListSteps fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @GetMapping("/threads/{threadId}/runs/{runId}")
    public ResponseEntity<RunsResponseDto> retrieveRun(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken
            , @PathVariable String threadId, @PathVariable String runId) {
        log.info("=== retrieveRun ===");
        RunsResponseDto responseDto = assistantApiService.retrieveRun(openAiToken, threadId, runId);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== retrieveRun success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== retrieveRun fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    @PostMapping("/threads/{threadId}/runs/{runId}/submit_tool_outputs")
    public ResponseEntity<RunsResponseDto> submitToolOutputsToRun(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken
            , @PathVariable String threadId, @PathVariable String runId, @RequestBody ToolOutputs toolOutputs) {
        log.info("=== submitToolOutputsToRun ===");
        RunsResponseDto responseDto = assistantApiService.submitToolOutputsToRun(openAiToken, threadId, runId, toolOutputs);
        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))) {
            log.info("=== submitToolOutputsToRun success: {}...", responseDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            log.info("=== submitToolOutputsToRun fail...");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }

    /**
     *  테스트용....
     * @return 응답결과
     */
    @PostMapping("/chat/process")
    public OpenAiAssistantResponseDto processChat(@RequestHeader(value = "OpenAI-Token", required = false) String openAiToken,
                                                 @RequestBody AppChatSaveRequestDto requestDto,
                                                  @RequestParam(defaultValue = "gpt-3.5-turbo-0613") String model) throws Exception {
        log.info("=== processChat ===");
        CompletableFuture<OpenAiAssistantResponseDto> future = assistantSystemService.callOpenAiAssistant(openAiToken, requestDto)
                .thenCompose(aResult -> {
                    if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(aResult.getRspCode()))) {
                        log.info("=== processChat callOpenAiAssistant success ===");
                        log.info("=== responseDto: {} ===", aResult);
                        return assistantSystemService.getReturnMessageAndHandle(aResult, openAiToken, model);
                    } else {
                        // SUCCESS가 아닌 경우 바로 반환
                        log.info("=== processChat callOpenAiAssistant fail ===");
                        log.info("=== responseDto: {} ===", aResult);
                        return CompletableFuture.completedFuture(aResult);
                    }
                })
                .exceptionally(ex -> {
                    log.info("=== processChat callOpenAiAssistant error ===");
                    OpenAiAssistantResponseDto responseDto = new OpenAiAssistantResponseDto();
                    responseDto.setRspCode(ResponseCode.ERROR.name());
                    responseDto.setRspMsg(ex.getMessage());
                    return responseDto;
                });
        return future.get();
    }
}
