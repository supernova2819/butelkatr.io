package pl.uservices.butelkatr.bottling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.sleuth.TraceManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.uservices.butelkatr.bottling.model.Version;

import java.net.URI;

@Component
@Slf4j
public class BottlingWorker {


    private Integer bottles = 0;

    private Integer bottled = 0;

    private TraceManager traceManager;
    private PresentingClient presentingClient;
    private RestTemplate restTemplate;

    @Autowired
    public BottlingWorker(TraceManager traceManager,
                          PresentingClient presentingClient,
                          @LoadBalanced RestTemplate restTemplate) {
        this.traceManager = traceManager;
        this.presentingClient = presentingClient;
        this.restTemplate = restTemplate;
    }

    @Async
    public void bottleBeer(Integer wortAmount, String correlationId) {
        log.info("Bottling beer...");

        int bottlesCount = wortAmount / 10;

        synchronized (this) {
            bottled += bottlesCount;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // i love this construct
        }

        synchronized (this) {
            bottles += bottlesCount;

            bottled -= bottlesCount;

        }

        //Trace trace = this.traceManager.startSpan("calling_prezentatr", correlationId);
        //callPresentingViaFeign(correlationId);
        useRestTemplateToCallPresenting(correlationId);
        //traceManager.close(trace);
    }

    private void callPresentingViaFeign(String correlationId) {
        presentingClient.updateBottles(bottles, correlationId);
    }

    //TODO: Toggle on property or sth
    private void useRestTemplateToCallPresenting(String processId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("PROCESS-ID", processId);
        headers.add("Content-Type", Version.PRESENTING_V1);
        String serviceName = "presenting";
        String url = "feed/bottles/" + bottles;
        URI uri = URI.create("http://" + serviceName + "/" + url);
        HttpMethod method = HttpMethod.PUT;
        RequestEntity<String> requestEntity = new RequestEntity<>("some body", headers, method, uri);
        restTemplate.exchange(requestEntity, String.class);
    }
}
