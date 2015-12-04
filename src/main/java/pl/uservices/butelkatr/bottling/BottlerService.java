package pl.uservices.butelkatr.bottling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.trace.TraceContextHolder;
import pl.uservices.butelkatr.bottling.model.BottleRequest;

@Slf4j
class BottlerService {

    private BottlingWorker bottlingWorker;
    private PrezentatrClient prezentatrClient;

    public BottlerService(BottlingWorker bottlingWorker, PrezentatrClient prezentatrClient) {
        this.bottlingWorker = bottlingWorker;
        this.prezentatrClient = prezentatrClient;
    }

    void bottle(BottleRequest bottleRequest) {
        notifyPrezentatr();
        bottlingWorker.bottleBeer(bottleRequest.getWort(), TraceContextHolder.getCurrentSpan());
    }

    void notifyPrezentatr() {
        prezentatrClient.butelkatrFeed();
    }
}
