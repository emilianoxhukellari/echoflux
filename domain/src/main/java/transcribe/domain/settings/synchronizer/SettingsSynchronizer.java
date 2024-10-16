package transcribe.domain.settings.synchronizer;

public interface SettingsSynchronizer {

    void synchronize();

    void reset(String key);

}
