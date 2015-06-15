package pl.devoxx.butelkatr.bottling;

import com.codahale.metrics.MetricRegistry;
import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import pl.devoxx.butelkatr.bottling.model.BottleRequest;

import java.util.concurrent.Future;

class BottlerService {

    private ServiceRestClient restClient;

    public BottlerService(ServiceRestClient restClient, MetricRegistry metricRegistry) {
        this.restClient = restClient;
    }

    void bottle(BottleRequest bottleRequest) {
        restClient.forService("prezentatr").put().onUrl("/feed/butelkatr/"+ CorrelationIdHolder.get())
                .withoutBody().andExecuteFor().aResponseEntity().ofType(ResponseEntity.class);

        bottleBeer(bottleRequest.getWort());
    }

    @Async
    private Future<Void> bottleBeer(Integer wortAmount) {
        try {
            Thread.sleep(wortAmount * 5);
        } catch (InterruptedException e) {
            // i love this construct
        }

        restClient.forService("prezentatr").put().onUrl("/feed/bottles/" + CorrelationIdHolder.get() + "/" + wortAmount / 10)
                .withoutBody().andExecuteFor().aResponseEntity().ofType(ResponseEntity.class);

        return new AsyncResult<>(null);
    }
}
