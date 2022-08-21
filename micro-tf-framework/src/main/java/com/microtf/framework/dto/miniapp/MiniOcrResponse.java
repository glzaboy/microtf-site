package com.microtf.framework.dto.miniapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microtf.framework.services.miniapp.baiduAi.OcrResult;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public  class MiniOcrResponse extends MiniAppResponse{

    @JsonProperty("paragraphs_result")
    private List<OcrResult.ParagraphsResultDTO> paragraphsResult;
    @JsonProperty("paragraphs_result_num")
    private Integer paragraphsResultNum;
    @JsonProperty("words_result")
    private List<OcrResult.WordsResultDTO> wordsResult;
    @JsonProperty("direction")
    private Integer direction;
    @JsonProperty("words_result_num")
    private Integer wordsResultNum;
    @JsonProperty("log_id")
    private Long logId;

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
        private OcrResult.WordsResultDTO.LocationDTO location;

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
