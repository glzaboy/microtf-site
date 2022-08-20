package com.microtf.framework.dto.miniapp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public  class UploadResponse extends MiniAppResponse{
    private String url;
    private String id;
}
