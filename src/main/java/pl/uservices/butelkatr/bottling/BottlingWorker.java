package pl.uservices.butelkatr.bottling;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.TraceManager;
import org.springframework.cloud.sleuth.trace.TraceContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import pl.uservices.butelkatr.bottling.model.Version;

@Component
@Slf4j
public class BottlingWorker {


    private Map<String, State> PROCESS_STATE = new ConcurrentHashMap<>();
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
    public void bottleBeer(Integer wortAmount, String processId, TestConfigurationHolder configurationHolder) {
        TestConfigurationHolder.TEST_CONFIG.set(configurationHolder);
        increaseBottles(wortAmount, processId);
        notifyPresentingService(processId);
    }

    private void notifyPresentingService(String processId) {
        Trace scope = this.traceManager.startSpan("calling_presenting", TraceContextHolder.getCurrentSpan());
        switch (TestConfigurationHolder.TEST_CONFIG.get().getTestCommunicationType()) {
            case FEIGN:
                callPresentingViaFeign(processId);
                break;
            default:
                useRestTemplateToCallPresenting(processId);
        }
        traceManager.close(scope);
    }

    private void increaseBottles(Integer wortAmount, String processId) {
        log.info("Bottling beer...");
        State stateForProcess = PROCESS_STATE.getOrDefault(processId, new State());
        Integer bottled = stateForProcess.bottled;
        Integer bottles = stateForProcess.bottles;
        int bottlesCount = wortAmount / 10;
        bottled += bottlesCount;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // i love this construct
        }
        bottles += bottlesCount;
        bottled -= bottlesCount;
        stateForProcess.setBottled(bottled);
        stateForProcess.setBottles(bottles);
        PROCESS_STATE.put(processId, stateForProcess);
    }

    private void callPresentingViaFeign(String processId) {
        presentingClient.updateBottles(PROCESS_STATE.get(processId).getBottles(), processId);
    }

    private void useRestTemplateToCallPresenting(String processId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("PROCESS-ID", processId);
        headers.add("Content-Type", Version.PRESENTING_V1);
        headers.add(TestConfigurationHolder.TEST_COMMUNICATION_TYPE_HEADER_NAME, TestConfigurationHolder.TEST_CONFIG.get().getTestCommunicationType().name());
        String serviceName = "presenting";
        String url = "feed/bottles/" + PROCESS_STATE.get(processId).getBottles();
        URI uri = URI.create("http://" + serviceName + "/" + url);
        HttpMethod method = HttpMethod.PUT;
        RequestEntity<String> requestEntity = new RequestEntity<>("some body", headers, method, uri);
        restTemplate.exchange(requestEntity, String.class);
    }

    @Data
    private static class State {
        private Integer bottles = 0;
        private Integer bottled = 0;
    }
}
