package com.myapp.web.springboot.common.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;

/**
 * <pre>
 *     설명: 파일 유틸 클래스
 *     작성자: 김진영
 *     작성일 2023. 2. 21.
 * </pre>
 */
public class FileUtils {
    private FileUtils() {
    }

    /**
     * 인풋스트림을 특정 클래스(Dto) 매핑하기
     * @param is 인풋스트림
     * @return 특정 클래스(Dto) 리스트
     */
    private static <T> List<T> mapInputStreamFile(InputStream is, Class<T> clazz) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        List<T> returnList = null;

        try {
            returnList = objectMapper.readValue(is, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnList;
    }
    //todo: 채팅다운로드 작업 필요...
}
