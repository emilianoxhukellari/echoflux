package echoflux.domain.settings.endpoint.impl;

import echoflux.domain.core.security.Endpoint;
import echoflux.domain.settings.endpoint.SettingsEndpoint;
import echoflux.domain.settings.synchronizer.SettingsSynchronizer;
import lombok.RequiredArgsConstructor;

@Endpoint
@RequiredArgsConstructor
public class SettingsEndpointImpl implements SettingsEndpoint {

    private final SettingsSynchronizer settingsSynchronizer;

    @Override
    public void synchronizeAll() {
        settingsSynchronizer.synchronizeAll();
    }

    @Override
    public Long reset(String key) {
        return settingsSynchronizer.reset(key);
    }

}
