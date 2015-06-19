package pl.devoxx.butelkatr.bottling;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import pl.devoxx.butelkatr.bottling.model.Version;

import java.util.concurrent.Future;

@Component
public class BottlingWorker {

    private ServiceRestClient restClient;

    private Integer bottles = 0;

    private Meter bottleCountrMeter;
    private Meter bottlesInProgress;

    @Autowired
    public BottlingWorker(ServiceRestClient restClient, MetricRegistry metricRegistry) {
        this.restClient = restClient;
        this.bottleCountrMeter = metricRegistry.meter("bottles");
        this.bottlesInProgress = metricRegistry.meter("bottlesInProgress");
    }

    @Async
    public void bottleBeer(Integer wortAmount, String correlationId) {
        int bottlesCount = wortAmount / 10;

        bottlesInProgress.mark(bottlesCount);

        try {
            Thread.sleep(wortAmount * 5);
        } catch (InterruptedException e) {
            // i love this construct
        }

        synchronized (this) {
            bottles += bottlesCount;

            bottleCountrMeter.mark(bottlesCount);
        }

        restClient.forService("prezentatr").put().onUrl("/feed/bottles/" +
                correlationId + "/" + bottles)
                .withoutBody()
                .withHeaders().contentType(Version.PREZENTATR_V1)
                .andExecuteFor().aResponseEntity().ofType(ResponseEntity.class);
    }
}
