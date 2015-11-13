package pl.uservices.butelkatr.bottling;

import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.discovery.ServiceConfigurationResolver;
import com.ofg.infrastructure.discovery.ServiceResolver;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.cloud.sleuth.Sampler;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.instrument.web.TraceFilter;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.cloud.zookeeper.discovery.dependency.ZookeeperDependencies;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.regex.Pattern;

@Configuration
class BottlingConfiguration {

    @Value("${spring.sleuth.instrument.web.skipPattern:}")
    private String skipPattern;

    @Autowired
    private Trace trace;

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
    public FilterRegistrationBean traceWebFilter(ApplicationEventPublisher publisher) {
        Pattern pattern = org.springframework.util.StringUtils.hasText(this.skipPattern) ? Pattern.compile(this.skipPattern)
                : TraceFilter.DEFAULT_SKIP_PATTERN;
        TraceFilter filter = new TraceFilter(this.trace, pattern);
        filter.setApplicationEventPublisher(publisher);
        return new FilterRegistrationBean(filter);
    }


    @Bean
    @Primary
    public ServiceRestClient serviceRestClientWithRestTemplate(RestTemplate restTemplate, ServiceResolver serviceResolver, ZookeeperDependencies zookeeperDependencies) {
        return new ServiceRestClient(restTemplate, serviceResolver, zookeeperDependencies);
    }

    @Autowired RestTemplate restTemplate;
    @Autowired @Qualifier("requestFactory") ClientHttpRequestFactory clientHttpRequestFactory;

    @PostConstruct
    void postConstruct() {
        restTemplate.setRequestFactory(clientHttpRequestFactory);
    }
}

