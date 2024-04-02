package com.myapp.web.springboot.assistant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.web.springboot.assistant.dto.*;
import com.myapp.web.springboot.assistant.dto.field.ToolOutputs;
import com.myapp.web.springboot.common.enums.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     설명: 오픈AI 어시스턴트 API  서비스
 *     작성자: kimjinyoung
 *     작성일: 2024. 2. 13.
 * </pre>
 */
@Service
@Slf4j
public class OpenAiAssistantApiService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String BASE_URL = "https://api.openai.com/v1";


    public OpenAiAssistantApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * ai 채터 명으로 ai assistant data 가져오기
     * @param nick ai 채터
     * @return ai assistant data
     */
    public AssistantResponseData getAssistantData(String nick, String openAiToken) {
        List<AssistantResponseData> assistantDataList = this.getAssistantList(openAiToken);
        return assistantDataList
                .stream()
                .filter(assistantData -> nick.equals(assistantData.getName()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 오픈AI 어시스턴트 목록 가져오기
     * @return 오픈AI 어시스턴트 목록 가져오기
     */
    public List<AssistantResponseData> getAssistantList(String openAiToken) {
        // 요청할 URL
        String url = BASE_URL + "/assistants";

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        List<AssistantResponseData> assistantResponseList = null;
        try {
            ResponseEntity<AssistantResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    AssistantResponseDto.class);

            if(response.getBody() != null) {
                assistantResponseList = response.getBody().getData();
            } else {
                log.info("getAssistantList fail... response body is null.");
            }
        } catch (Exception e) {
            log.error("getAssistantList error... e: {}", e.getMessage(), e);
        }
        return assistantResponseList;
    }


    /**
     * 오픈AI 쓰레드 만들기
     * @return 오픈AI 쓰레드
     */
    public ThreadResponseDto createThread(String openAiToken) {
        // 요청할 URL
        String url = BASE_URL + "/threads";

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ThreadResponseDto responseDto;
        try {
            ResponseEntity<ThreadResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    ThreadResponseDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("createThread fail... response body is null.");
                responseDto = new ThreadResponseDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("createThread error... e: {}", e.getMessage(), e);
            responseDto = new ThreadResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }

        return responseDto;
    }

    /**
     * 오픈AI 쓰레드 조회하기
     * @param threadId 조회할 threadId
     * @return 오픈AI 쓰레드
     */
    public ThreadResponseDto retrieveThread(String openAiToken, String threadId) {
        // 요청할 URL
        String url = BASE_URL + "/threads/" + threadId;

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ThreadResponseDto responseDto;
        try {
            ResponseEntity<ThreadResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ThreadResponseDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("retrieveThread fail... response body is null.");
                responseDto = new ThreadResponseDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("retrieveThread error... e: {}", e.getMessage(), e);
            responseDto = new ThreadResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }

        return responseDto;
    }

    /**
     * 오픈AI 쓰레드 수정하기
     * @param threadId 수정할 threadId
     * @return 오픈AI 쓰레드
     * 안 사용할 것 같음...
     */
    public ThreadResponseDto modifyThread(String openAiToken, String threadId, Map<String, String> metadata) {
        // 요청할 URL
        String url = BASE_URL + "/threads/" + threadId;

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        // metadata 맵을 JSON 문자열로 변환
        String jsonBody;
        ThreadResponseDto responseDto;
        try {
            jsonBody = objectMapper.writeValueAsString(metadata);

            // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<ThreadResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    ThreadResponseDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("modifyThread fail... response body is null.");
                responseDto = new ThreadResponseDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("modifyThread error... e: {}", e.getMessage(), e);
            responseDto = new ThreadResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }

        return responseDto;
    }

    /**
     * 오픈AI 쓰레드 삭제하기
     * @param threadId 삭제할 threadId
     * @return 오픈AI 쓰레드
     */
    public ThreadResponseDto deleteThread(String openAiToken, String threadId) {
        // 요청할 URL
        String url = BASE_URL + "/threads/" + threadId;

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        ThreadResponseDto responseDto;
        try {
            ResponseEntity<ThreadResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    ThreadResponseDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("deleteThread fail... response body is null.");
                responseDto = new ThreadResponseDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("deleteThread error... e: {}", e.getMessage(), e);
            responseDto = new ThreadResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }

        return responseDto;
    }

    /**
     * 메세지 만들기
     * @param threadId 쓰레드 ID
     * @param messageRequestDto 요청DTO(role, content)
     * @return 메세지 응답 DTO
     */

    public MessageResponseDto createMessage(String openAiToken, String threadId, MessageRequestDto messageRequestDto) {
        // 요청할 URL
        String url = BASE_URL + "/threads/" + threadId + "/messages";

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        String ROLE_USER = "user";
        messageRequestDto.setRole(ROLE_USER);

        // metadata 맵을 JSON 문자열로 변환
        String jsonBody;
        MessageResponseDto responseDto;
        try {
            jsonBody = objectMapper.writeValueAsString(messageRequestDto);

            // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<MessageResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    MessageResponseDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("createMessage fail... response body is null.");
                responseDto = new MessageResponseDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("createMessage error... e: {}", e.getMessage(), e);
            responseDto = new MessageResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }

        return responseDto;
    }

    /**
     * 메세지 리스트 조회하기
     * @param threadId 쓰레드 ID
     * @return 응답 dto
     */
    public MessageResponseListDto getMessageList(String openAiToken, String threadId) {
        // 요청할 URL
        String url = BASE_URL + "/threads/" + threadId + "/messages";

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        MessageResponseListDto responseDto;
        try {
            ResponseEntity<MessageResponseListDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    MessageResponseListDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("getMessageList fail... response body is null.");
                responseDto = new MessageResponseListDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("getMessageList error... e: {}", e.getMessage(), e);
            responseDto = new MessageResponseListDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }

        return responseDto;
    }

    /**
     * 메세지 검색하기
     * @param threadId 쓰레드 ID
     * @param messageId 메세지 ID
     * @return 응답 dto
     */
    public MessageResponseDto retrieveMessage(String openAiToken, String threadId, String messageId) {
        // 요청할 URL
        String url = BASE_URL + "/threads/" + threadId + "/messages/" + messageId;

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        MessageResponseDto responseDto;
        try {
            ResponseEntity<MessageResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    MessageResponseDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("retrieveMessage fail... response body is null.");
                responseDto = new MessageResponseDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("retrieveMessage error... e: {}", e.getMessage(), e);
            responseDto = new MessageResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }
        return responseDto;
    }


    /**
     * 메세지 실행시키기
     * @param threadId 쓰레드ID
     * @param runsRequestDto 어시스턴트ID
     * @return 런 응답 DTO
     */
    public RunsResponseDto createRun(String openAiToken, String model, String threadId, RunsRequestDto runsRequestDto) {
        // 요청할 URL
        String url = BASE_URL + "/threads/" + threadId + "/runs";

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        runsRequestDto.setModel(model);

        // metadata 맵을 JSON 문자열로 변환
        String jsonBody;
        RunsResponseDto responseDto;
        try {
            jsonBody = objectMapper.writeValueAsString(runsRequestDto);

            // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<RunsResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    RunsResponseDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("createRun fail... response body is null.");
                responseDto = new RunsResponseDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("createRun error... e: {}", e.getMessage(), e);
            responseDto = new RunsResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }

        return responseDto;
    }

    /**
     * 메세지 런 상태 검색하기
     * @param threadId 쓰레드 ID
     * @param runId 런 ID
     * @return 응답 dto
     */
    public RunsResponseStepDto getRunsListSteps(String openAiToken, String threadId, String runId) {
        // 요청할 URL
        String url = BASE_URL + "/threads/" + threadId + "/runs/" + runId + "/steps";

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        RunsResponseStepDto responseDto;
        try {
            ResponseEntity<RunsResponseStepDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    RunsResponseStepDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("getRunsListSteps fail... response body is null.");
                responseDto = new RunsResponseStepDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("getRunsListSteps error... e: {}", e.getMessage(), e);
            responseDto = new RunsResponseStepDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }
        return responseDto;
    }

    /**
     * 메세지 런 상태 검색하기
     * @param threadId 쓰레드 ID
     * @param runId 런 ID
     * @return 응답 dto
     */
    public RunsResponseDto retrieveRun(String openAiToken, String threadId, String runId) {
        // 요청할 URL
        String url = BASE_URL + "/threads/" + threadId + "/runs/" + runId;

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        // HttpEntity 객체 생성 (헤더 포함, 바디는 null)
        HttpEntity<String> entity = new HttpEntity<>(null, headers);

        RunsResponseDto responseDto;
        try {
            ResponseEntity<RunsResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    RunsResponseDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("retrieveRun fail... response body is null.");
                responseDto = new RunsResponseDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("retrieveRun error... e: {}", e.getMessage(), e);
            responseDto = new RunsResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }
        return responseDto;
    }

    /**
     * Function Chat Run 진행하기 위해 tool output 제출
     * @param threadId 쓰레드 ID
     * @param runId 런 ID
     * @return 응답 dto
     */
    public RunsResponseDto submitToolOutputsToRun(String openAiToken, String threadId, String runId, ToolOutputs toolOutputs) {
        // 요청할 URL
        String url = BASE_URL + "/threads/" + threadId + "/runs/" + runId + "/submit_tool_outputs";

        // 헤더 설정
        HttpHeaders headers = this.getCommonHeaders(openAiToken);

        // toolOutputs을 JSON 문자열로 변환
        String jsonBody;

        RunsResponseDto responseDto;
        try {
            jsonBody = objectMapper.writeValueAsString(toolOutputs);

            // HttpEntity 객체 생성 (헤더 포함)
            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            ResponseEntity<RunsResponseDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    RunsResponseDto.class);

            if(response.getBody() != null) {
                responseDto = response.getBody();
                responseDto.setRspCode(ResponseCode.SUCCESS.name());
            } else {
                log.info("submitToolOutputsToRun fail... response body is null.");
                responseDto = new RunsResponseDto();
                responseDto.setRspCode(ResponseCode.FAIL.name());
                responseDto.setRspMsg("return body is null");
            }
        } catch (Exception e) {
            log.error("submitToolOutputsToRun error... e: {}", e.getMessage(), e);
            responseDto = new RunsResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
        }
        return responseDto;
    }

    /**
     * 공통헤더 가져오기
     * @param openAiToken 오픈AI 토큰(커스텀 AI 만들 때 가져옴, 없으면 기본값 사용)
     * @return 공통헤더
     */
    private HttpHeaders getCommonHeaders(String openAiToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + openAiToken);
        headers.add("Content-Type", "application/json");
        headers.add("OpenAI-Beta", "assistants=v1");
        return headers;
    }
}
