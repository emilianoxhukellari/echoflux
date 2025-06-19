package echoflux.domain.application_user.data;

import echoflux.core.core.country.Country;
import echoflux.domain.core.data.BaseProjection;

import java.time.ZoneId;

public interface ScalarApplicationUserProjection extends BaseProjection<Long> {

    String getUsername();

    String getName();

    String getPassword();

    Boolean getEnabled();

    Country getCountry();

    ZoneId getZoneId();

}
