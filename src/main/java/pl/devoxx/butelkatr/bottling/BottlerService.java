package pl.devoxx.butelkatr.bottling;

import com.codahale.metrics.MetricRegistry;
import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import org.springframework.http.ResponseEntity;
import pl.devoxx.butelkatr.bottling.model.BottleRequest;
import pl.devoxx.butelkatr.bottling.model.Version;

class BottlerService {

    private ServiceRestClient restClient;
    private BottlingWorker bottlingWorker;

    public BottlerService(ServiceRestClient restClient, BottlingWorker bottlingWorker,  MetricRegistry metricRegistry) {
        this.restClient = restClient;
        this.bottlingWorker = bottlingWorker;
    }

    void bottle(BottleRequest bottleRequest) {
        restClient.forService("prezentatr").put().onUrl("/feed/butelkatr/"+ CorrelationIdHolder.get())
                .withoutBody()
                    .withHeaders().contentType(Version.PREZENTATR_V1)
                .andExecuteFor().aResponseEntity().ofType(ResponseEntity.class);

        bottlingWorker.bottleBeer(bottleRequest.getWort(), CorrelationIdHolder.get());
    }
}
