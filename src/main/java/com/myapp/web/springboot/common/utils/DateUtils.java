package com.myapp.web.springboot.common.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * <pre>
 *     설명: 날짜 유틸 클래스
 *     작성자: 김진영
 *     작성일 2023. 11. 01.
 * </pre>
 */
public class DateUtils {
    private static final DateTimeFormatter a_HH_mm = DateTimeFormatter.ofPattern("a HH시 mm분", Locale.KOREAN);
    private static final DateTimeFormatter MM_dd = DateTimeFormatter.ofPattern("MM월 dd일");
    private static final DateTimeFormatter YYYY_MM_dd = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private static final DateTimeFormatter MM_dd_a_HH_mm = DateTimeFormatter.ofPattern("MM월 dd일 a HH시 mm분", Locale.KOREAN);
    private static final DateTimeFormatter YYYY_MM_dd_E_a_HH_mm = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 E요일 a HH시 mm분", Locale.KOREAN);
    private DateUtils() {
        // private constructor to prevent instantiation
    }

    /**
     * 수정일과 현재일 비교해서 포매팅된 날짜 가져오기
     * 앱 채팅목록에서 사용...
     * @param modifiedDate 수정일
     * @return 조건별 포매팅한 문자열
     */
    public static String dateParseForRoomList(LocalDateTime modifiedDate) {
        LocalDate nowDate = LocalDate.now();

        // {당일}: 오전/오후 00:00
        // {어제}: 어제
        // {같은해}: 00월 00일
        // {나머지}: 0000.00.00
        if(modifiedDate.toLocalDate().equals(nowDate)) {
            return modifiedDate.format(a_HH_mm);
        } else if(modifiedDate.toLocalDate().equals(nowDate.minusDays(1))) {
            return "어제";
        } else if(modifiedDate.getYear() == nowDate.getYear()) {
            return modifiedDate.format(MM_dd);
        }
        return modifiedDate.format(YYYY_MM_dd);
    }

    /**
     * 생성일 비교해서 포매팅된 날짜 가져오기
     * 앱 채팅에서 사용...
     * @param createdDate 생성일
     * @return 조건별 포매팅한 문자열
     */
    public static String dateParseForChatList(LocalDateTime createdDate) {
        LocalDate nowDate = LocalDate.now();

        // {당일}: 오전/오후 00:00
        // {같은해}: 00월 00일 오전/오후 00:00
        // {나머지}: yyyy년 MM월 dd일 E요일 오전/오후 00:00
        if(createdDate.toLocalDate().equals(nowDate)) {
            return createdDate.format(a_HH_mm);
        } else if(createdDate.getYear() == nowDate.getYear()) {
            return createdDate.format(MM_dd_a_HH_mm);
        }
        return createdDate.format(YYYY_MM_dd_E_a_HH_mm);
    }
}
