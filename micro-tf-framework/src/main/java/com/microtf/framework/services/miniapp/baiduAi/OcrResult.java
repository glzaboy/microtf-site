package com.microtf.framework.services.miniapp.baiduAi;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 百度ai ocr 接口识别结果
 *
 * @author glzaboy
 */
@NoArgsConstructor
@Data
public class OcrResult {

    @JsonProperty("paragraphs_result")
    private List<ParagraphsResultDTO> paragraphsResult;
    @JsonProperty("paragraphs_result_num")
    private Integer paragraphsResultNum;
    @JsonProperty("words_result")
    private List<WordsResultDTO> wordsResult;
    @JsonProperty("direction")
    private Integer direction;
    @JsonProperty("words_result_num")
    private Integer wordsResultNum;
    @JsonProperty("log_id")
    private Long logId;
    @JsonProperty("error_code")
    private String errorCode;
    @JsonProperty("error_msg")
    private String errorMsg;

    @NoArgsConstructor
    @Data
    public static class ParagraphsResultDTO {
        @JsonProperty("words_result_idx")
        private List<Integer> wordsResultIdx;
    }

    @NoArgsConstructor
    @Data
    public static class WordsResultDTO {

        @JsonProperty("words")
        private String words;
        @JsonProperty("location")
        private LocationDTO location;

        @NoArgsConstructor
        @Data
        public static class LocationDTO {
            @JsonProperty("top")
            private Integer top;
            @JsonProperty("left")
            private Integer left;
            @JsonProperty("width")
            private Integer width;
            @JsonProperty("height")
            private Integer height;
        }
    }
}
