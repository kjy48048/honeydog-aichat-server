package com.myapp.web.springboot.appchat.dto;

import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 앱용 채팅 통신 성공 체크용 서비스
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 10.
 * </pre>
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AppChatCallResponseDto extends CommonResponseDto {
    String returnMessage;
}
