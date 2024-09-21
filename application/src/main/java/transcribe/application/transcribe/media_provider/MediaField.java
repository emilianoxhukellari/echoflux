package transcribe.application.transcribe.media_provider;

import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.tabs.TabSheetVariant;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.transcribe.media_provider.impl.PublicMediaProvider;
import transcribe.application.transcribe.media_provider.impl.LocalMediaProvider;
import transcribe.domain.transcription.data.MediaOrigin;

import java.util.Optional;

public class MediaField extends CustomField<MediaValue> {

    private final LocalMediaProvider localMedia;
    private final PublicMediaProvider publicMedia;
    private final TabSheet tabSheet;

    public MediaField() {
        localMedia = new LocalMediaProvider();
        localMedia.setHeight("180px");

        publicMedia = new PublicMediaProvider();
        publicMedia.setHeight("180px");

        localMedia.onReady(r -> {
            publicMedia.clearAndCleanup();
            setValue(r);
        });
        localMedia.onClientCleared(() -> Optional.ofNullable(getValue())
                .map(MediaValue::mediaOrigin)
                .filter(MediaOrigin.LOCAL::equals)
                .ifPresent(_ -> clear()));

        publicMedia.onReady(r -> {
            localMedia.clearAndCleanup();
            setValue(r);
        });
        publicMedia.onClientCleared(() -> Optional.ofNullable(getValue())
                .map(MediaValue::mediaOrigin)
                .filter(MediaOrigin.PUBLIC::equals)
                .ifPresent(_ -> clear()));

        this.tabSheet = new TabSheet();
        tabSheet.addClassName("transcribe-tab-sheet");
        tabSheet.addThemeVariants(TabSheetVariant.LUMO_TABS_EQUAL_WIDTH_TABS, TabSheetVariant.LUMO_TABS_HIDE_SCROLL_BUTTONS);

        tabSheet.add(newPublicTab(), publicMedia);
        tabSheet.add(newLocalTab(), localMedia);

        add(tabSheet);
    }

    public void clearAndCleanup() {
        localMedia.clearAndCleanup();
        publicMedia.clearAndCleanup();
        clear();
    }

    private static Tab newPublicTab() {
        var facebookIcon = LineAwesomeIcon.FACEBOOK.create();
        facebookIcon.setSize("2rem");

        var youtubeIcon = LineAwesomeIcon.YOUTUBE.create();
        youtubeIcon.setSize("2rem");

        return new Tab(facebookIcon, youtubeIcon);
    }

    private static Tab newLocalTab() {
        var uploadIcon = VaadinIcon.UPLOAD_ALT.create();
        uploadIcon.setSize("2rem");

        return new Tab(uploadIcon);
    }

    @Override
    protected MediaValue generateModelValue() {
        return getValue();
    }

    @Override
    protected void setPresentationValue(MediaValue mediaValue) {
    }

    @Override
    public void setInvalid(boolean invalid) {
        super.setInvalid(invalid);
        if (invalid) {
            tabSheet.addClassName("invalid-field");
        } else {
            tabSheet.removeClassName("invalid-field");
        }
    }

}
