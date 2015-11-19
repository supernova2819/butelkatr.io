package pl.uservices.butelkatr.bottling;

import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.discovery.ServiceResolver;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Configuration
class BottlingConfiguration {

    @Bean
    BottlerService bottlingService(ServiceRestClient serviceRestClient, BottlingWorker bottlingWorker,
                                   RetryExecutor retryExecutor) {
        return new BottlerService(serviceRestClient, bottlingWorker, retryExecutor);
    }

    @Bean
    public Sampler<?> defaultSampler() {
        return new AlwaysSampler();
    }

    @Bean
    @Primary
    public ServiceRestClient serviceRestClientWithRestTemplate(RestTemplate restTemplate, ServiceResolver serviceResolver, ZookeeperDependencies zookeeperDependencies, Trace trace) {
        return new ServiceRestClient(restTemplate, serviceResolver, zookeeperDependencies, trace);
    }

    @Autowired RestTemplate restTemplate;
    @Autowired @Qualifier("requestFactory") ClientHttpRequestFactory clientHttpRequestFactory;

    @PostConstruct
    void postConstruct() {
        restTemplate.setRequestFactory(clientHttpRequestFactory);
    }
}

