package pl.uservices.butelkatr.bottling;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.ofg.infrastructure.correlationid.CorrelationIdUpdater;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.uservices.butelkatr.bottling.model.Version;

@Component
@Slf4j
public class BottlingWorker {

    private ServiceRestClient restClient;

    private Integer bottles = 0;

    private Integer bottled = 0;

    private Meter bottleCountrMeter;
    private Trace trace;

    @Autowired
    public BottlingWorker(ServiceRestClient restClient, MetricRegistry metricRegistry, Trace trace) {
        this.restClient = restClient;
        this.bottleCountrMeter = metricRegistry.meter("bottles");
        this.trace = trace;
        metricRegistry.register("bottlesInProgress", (Gauge<Integer>) () -> bottled);
    }

    @Async
    public void bottleBeer(Integer wortAmount, String correlationId) {
        CorrelationIdUpdater.updateCorrelationId(correlationId);
        log.info("Bottling beer...");

        int bottlesCount = wortAmount / 10;

        synchronized (this) {
            bottled += bottlesCount;
        }

        try {
            Thread.sleep(wortAmount * 5);
        } catch (InterruptedException e) {
            // i love this construct
        }

        synchronized (this) {
            bottles += bottlesCount;

            bottled -= bottlesCount;

            bottleCountrMeter.mark(bottlesCount);
        }

        //TraceScope scope = this.trace.startSpan("calling_prezentatr", new AlwaysSampler(), null);
        restClient.forService("prezentatr").put().onUrl("/feed/bottles/" + bottles)
                .withoutBody()
                .withHeaders().contentType(Version.PREZENTATR_V1)
                .andExecuteFor().aResponseEntity().ofType(ResponseEntity.class);
        //scope.close();
    }
}
