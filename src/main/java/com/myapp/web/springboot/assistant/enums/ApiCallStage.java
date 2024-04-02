package com.myapp.web.springboot.assistant.enums;

/**
 * DB에 히스토리용으로 저장하는 open api assistants api call 기록용 저장 단계 처리...
 */
public enum ApiCallStage {
    ListAssistantsAPI, RetrieveThread, CreateThread, CreateMessage, CreateRun, SubmitToolOutputsToRun, RetrieveRun, RunsListStep, RetrieveMessage
}
