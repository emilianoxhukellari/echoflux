package com.example.application.module.transcribe.media.downloader;

@FunctionalInterface
public interface MediaDownloadProgressCallback {

    void onDownloading(int progress);

}
