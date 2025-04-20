package echoflux.application.transcribe.media_provider.impl;

import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.upload.receivers.FileData;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;
import echoflux.application.core.upload.UploadFileFactory;
import echoflux.application.transcribe.media_provider.MediaProvider;
import echoflux.application.transcribe.media_provider.MediaValue;
import echoflux.core.core.error.PropagatedException;
import echoflux.core.core.utils.EfFiles;
import echoflux.core.core.utils.EfUris;
import echoflux.core.core.utils.EfFunctions;
import echoflux.domain.transcription.data.MediaOrigin;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

public class LocalMediaProvider extends HorizontalLayout implements MediaProvider {

    private final FileBuffer fileBuffer;
    private final Upload upload;
    private Consumer<MediaValue> onReady;
    private Runnable onClientCleared;
    private Path path;

    public LocalMediaProvider() {
        this.fileBuffer = new FileBuffer(UploadFileFactory.INSTANCE);

        this.upload = new Upload();
        upload.setReceiver(fileBuffer);
        upload.setAcceptedFileTypes("audio/*", "video/*");
        upload.setDropLabelIcon(LineAwesomeIcon.UPLOAD_SOLID.create());
        upload.setDropLabel(new NativeLabel("Upload media file"));
        upload.addClassName("upload");
        upload.setSizeFull();

        upload.addSucceededListener(e -> {
            path = Optional.ofNullable(fileBuffer.getFileData())
                    .map(FileData::getFile)
                    .map(File::toPath)
                    .orElse(null);

            if (!StringUtils.containsAny(e.getMIMEType(), "audio/", "video/")) {
                clearAndCleanup();
                throw new PropagatedException("Unsupported media type. Please upload an audio or video file.");
            }

            EfFunctions.consumeIfPresent(
                    onReady,
                    new MediaValue(EfUris.toUri(path), FilenameUtils.getBaseName(e.getFileName()), MediaOrigin.LOCAL)
            );
        });
        upload.addFileRemovedListener(_ -> {
            cleanup();
            EfFunctions.runIfPresent(onClientCleared);
        });

        add(upload);
        setSizeFull();
        setPadding(false);
    }

    @Override
    public void onReady(Consumer<MediaValue> onReady) {
        this.onReady = onReady;
    }

    @Override
    public void onClientCleared(Runnable onClientCleared) {
        this.onClientCleared = onClientCleared;
    }

    @Override
    public void clearAndCleanup() {
        upload.getElement().executeJs(
                "this.dispatchEvent(new CustomEvent('file-abort', { detail: { file: this.files[0] } }));"
        );
        cleanup();
    }

    private void cleanup() {
        EfFiles.deleteIfExists(path);
        this.path = null;
    }

}
