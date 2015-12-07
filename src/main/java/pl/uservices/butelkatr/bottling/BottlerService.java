package pl.uservices.butelkatr.bottling;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import pl.uservices.butelkatr.bottling.model.BottleRequest;
import pl.uservices.butelkatr.bottling.model.Version;

@Slf4j
class BottlerService {

    private BottlingWorker bottlingWorker;
    private PresentingClient presentingClient;
    private RestTemplate restTemplate;

    public BottlerService(BottlingWorker bottlingWorker, PresentingClient presentingClient,
                          RestTemplate restTemplate) {
        this.bottlingWorker = bottlingWorker;
        this.presentingClient = presentingClient;
        this.restTemplate = restTemplate;
    }

    void bottle(BottleRequest bottleRequest, String processId) {
        notifyPrezentatr(processId);
        bottlingWorker.bottleBeer(bottleRequest.getWort(), processId, TestConfigurationHolder.TEST_CONFIG.get());
    }

    void notifyPrezentatr(String processId) {
        switch (TestConfigurationHolder.TEST_CONFIG.get().getTestCommunicationType()) {
            case FEIGN:
                callPresentingViaFeign(processId);
                break;
            default:
                useRestTemplateToCallPresenting(processId);
        }
    }

    private void callPresentingViaFeign(String processId) {
        presentingClient.bottlingFeed(processId);
    }

    private void useRestTemplateToCallPresenting(String processId) {
        log.info("Notifying presenting about beer. Process id [{}]", processId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("PROCESS-ID", processId);
        headers.add("Content-Type", Version.PRESENTING_V1);
        headers.add(TestConfigurationHolder.TEST_COMMUNICATION_TYPE_HEADER_NAME, TestConfigurationHolder.TEST_CONFIG.get().getTestCommunicationType().name());
        String serviceName = "presenting";
        String url = "feed/bottling";
        URI uri = URI.create("http://" + serviceName + "/" + url);
        HttpMethod method = HttpMethod.PUT;
        RequestEntity<String> requestEntity = new RequestEntity<>("some body", headers, method, uri);
        restTemplate.exchange(requestEntity, String.class);
    }
}