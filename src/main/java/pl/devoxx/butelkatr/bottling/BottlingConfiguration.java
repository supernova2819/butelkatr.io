package pl.devoxx.butelkatr.bottling;

import com.codahale.metrics.MetricRegistry;
import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BottlingConfiguration {
    @Bean
    BottlerService bottlingService(ServiceRestClient serviceRestClient,
                                   MetricRegistry metricRegistry) {
        return new BottlerService(serviceRestClient, metricRegistry);
    }
}

