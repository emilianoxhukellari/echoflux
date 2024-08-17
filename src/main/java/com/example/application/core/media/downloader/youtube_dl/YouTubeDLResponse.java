package com.example.application.core.media.downloader.youtube_dl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YouTubeDLResponse {

    private int exitCode;
    private String output;
    private String error;

}
