package com.myapp.web.springboot.aichatter.controller;


import com.myapp.web.springboot.aichatter.dto.AiChatterRequestDto;
import com.myapp.web.springboot.aichatter.dto.AiChatterResponseDto;
import com.myapp.web.springboot.aichatter.dto.AiChatterResponseListDto;
import com.myapp.web.springboot.aichatter.service.AiChatterService;
import com.myapp.web.springboot.assistant.dto.AssistantResponseData;
import com.myapp.web.springboot.assistant.service.OpenAiAssistantApiService;
import com.myapp.web.springboot.common.enums.ResponseCode;
import com.myapp.web.springboot.common.utils.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     설명: Ai 채터 API 컨트롤러
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 06.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/ai-chatter")
@Slf4j
public class AiChatterApiController {
    private final AiChatterService aiChatterService;
    private final OpenAiAssistantApiService openAiAssistantApiService;
    private final EncryptionUtil encryptionUtil;

    @PostMapping("")
    public ResponseEntity<AiChatterResponseDto> create(@RequestBody AiChatterRequestDto requestDto) {
        // validation check
        // 1. request body data check...
        if(StringUtils.isNullOrEmpty(requestDto.getUserUuid())
                || StringUtils.isNullOrEmpty(requestDto.getNick())
                || StringUtils.isNullOrEmpty(requestDto.getOpenAiToken())
                || StringUtils.isNullOrEmpty(requestDto.getModel())) {
            log.info("requestDto some field empty...");
            AiChatterResponseDto responseDto = new AiChatterResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg("PARAM_EMPTY");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
        // 2. save data duplicate check...
        if(aiChatterService.findByUserAndNick(requestDto.getUserUuid(), requestDto.getNick()) != null) {
            log.info("requestDtp save, but duplicated fail...");
            AiChatterResponseDto responseDto = new AiChatterResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg("DUPLICATED_DATA");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        };
        List<AssistantResponseData> assistantResponseDataList = openAiAssistantApiService.getAssistantList(requestDto.getOpenAiToken());
        if(assistantResponseDataList == null || assistantResponseDataList.size() < 1) {
            log.info("not found ai assistants...");
            AiChatterResponseDto responseDto = new AiChatterResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg("NOT_FOUND_AI");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
        AssistantResponseData assistantResponseData = assistantResponseDataList
                .stream()
                .filter(data -> data.getName().equals(requestDto.getNick()))
                .findFirst()
                .orElse(null);

        if(assistantResponseData == null) {
            log.info("not found ai assistants...");
            AiChatterResponseDto responseDto = new AiChatterResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg("NOT_FOUND_AI");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }

        String encryptedToken = encryptionUtil.encrypt(requestDto.getOpenAiToken());
        return this.getCommonResponseDto(aiChatterService.createAiUser(requestDto, encryptedToken));
    }

    @GetMapping("")
    public ResponseEntity<AiChatterResponseDto> findByUserAndNick(@RequestParam String userUuid, @RequestParam String nick) {
        if(StringUtils.isNullOrEmpty(userUuid)
                || StringUtils.isNullOrEmpty(nick)) {
            log.info("request some param empty...");
            AiChatterResponseDto responseDto = new AiChatterResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg("request some param empty...");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
        return this.getCommonResponseDto(aiChatterService.findByUserAndNick(userUuid, nick));
    }

    @GetMapping("/list")
    public ResponseEntity<AiChatterResponseListDto> findListByUser(@RequestParam String userUuid) {
        if(StringUtils.isNullOrEmpty(userUuid)) {
            log.info("request some param empty...");
            AiChatterResponseListDto responseDto = new AiChatterResponseListDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg("request some param empty...");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
        List<AiChatterResponseDto> responseList = aiChatterService.findByUser(userUuid);
        AiChatterResponseListDto responseListDto = new AiChatterResponseListDto();
        if(responseList != null) {
            responseListDto.setResponseList(responseList);
            responseListDto.setRspCode(ResponseCode.SUCCESS.name());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseListDto);
        } else {
            responseListDto.setRspCode(ResponseCode.ERROR.name());
            responseListDto.setRspMsg("internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseListDto);
        }
    }

    @PutMapping("")
    public ResponseEntity<AiChatterResponseDto> update(@RequestBody AiChatterRequestDto requestDto) {
        if(StringUtils.isNullOrEmpty(requestDto.getUserUuid())) {
            log.info("requestDto some field empty...");
            AiChatterResponseDto responseDto = new AiChatterResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg("requestDto some field empty...");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
        return this.getCommonResponseDto(aiChatterService.updateAiUser(requestDto));
    }

    @DeleteMapping("/user-uuid/{userUuid}/nick/{nick}")
    public ResponseEntity<AiChatterResponseDto> delete(@PathVariable String userUuid, @PathVariable String nick) {
        if(StringUtils.isNullOrEmpty(userUuid) || StringUtils.isNullOrEmpty(nick)) {
            log.info("request param is empty");
            AiChatterResponseDto responseDto = new AiChatterResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg("request param is empty...");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseDto);
        }
        Long aiChatterId = aiChatterService.deleteAiUser(userUuid, nick);
        AiChatterResponseDto responseDto = new AiChatterResponseDto();
        if(aiChatterId != null) {
            responseDto.setRspCode(ResponseCode.SUCCESS.name());
            responseDto.setRspMsg(aiChatterId.toString());
        } else {
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg("internal server error");
        }
        return aiChatterId != null
                ? ResponseEntity.status(HttpStatus.CREATED).body(responseDto)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }

    @GetMapping("/password/test")
    public String passwordTest(@RequestParam String text, @RequestParam(required = false) String mode) {
        return !StringUtils.isNullOrEmpty(mode) ? encryptionUtil.encrypt(text) : encryptionUtil.decrypt(text);
    }

    private ResponseEntity<AiChatterResponseDto> getCommonResponseDto(AiChatterResponseDto responseDto) {
        if(responseDto != null) {
            responseDto.setRspCode(ResponseCode.SUCCESS.name());
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            responseDto = new AiChatterResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg("internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }
}
