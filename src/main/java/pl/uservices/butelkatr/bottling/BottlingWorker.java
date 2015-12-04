package pl.uservices.butelkatr.bottling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Trace;
import org.springframework.cloud.sleuth.TraceManager;
import org.springframework.cloud.sleuth.trace.TraceContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.uservices.butelkatr.bottling.model.Version;

@Component
@Slf4j
public class BottlingWorker {


    private Integer bottles = 0;

    private Integer bottled = 0;

    private TraceManager traceManager;
    private PrezentatrClient prezentatrClient;

    @Autowired
    public BottlingWorker(TraceManager traceManager, PrezentatrClient prezentatrClient) {
        this.traceManager = traceManager;
        this.prezentatrClient = prezentatrClient;
    }

    @Async
    public void bottleBeer(Integer wortAmount, Span correlationId) {
        TraceContextHolder.setCurrentTrace(new Trace(correlationId));
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

        Trace trace = this.traceManager.startSpan("calling_prezentatr", correlationId);
        prezentatrClient.updateBottles(bottles);
        traceManager.close(trace);
    }
}
