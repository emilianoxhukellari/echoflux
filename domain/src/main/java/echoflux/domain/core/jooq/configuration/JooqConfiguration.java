package echoflux.domain.core.jooq.configuration;

import org.jooq.SQLDialect;
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("jooqConfig")
public class JooqConfiguration {

    @Bean
    public DefaultConfigurationCustomizer defaultConfigurationCustomizer() {
        return c -> c.set(SQLDialect.POSTGRES);
    }

}
