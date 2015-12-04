package pl.uservices.butelkatr.bottling;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BottlingConfiguration {

    @Bean
    BottlerService bottlingService(BottlingWorker bottlingWorker, PrezentatrClient prezentatrClient) {
        return new BottlerService(bottlingWorker, prezentatrClient);
    }

}

