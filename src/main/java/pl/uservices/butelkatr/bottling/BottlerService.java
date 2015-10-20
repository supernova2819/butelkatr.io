package pl.uservices.butelkatr.bottling;

import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import lombok.extern.slf4j.Slf4j;
import pl.uservices.butelkatr.bottling.model.BottleRequest;
import pl.uservices.butelkatr.bottling.model.Version;

@Slf4j
class BottlerService {

    private ServiceRestClient restClient;
    private BottlingWorker bottlingWorker;
    private RetryExecutor retryExecutor;

    public BottlerService(ServiceRestClient restClient, BottlingWorker bottlingWorker,  RetryExecutor retryExecutor) {
        this.restClient = restClient;
        this.bottlingWorker = bottlingWorker;
        this.retryExecutor = retryExecutor;
    }

    void bottle(BottleRequest bottleRequest) {
        notifyPrezentatr();

        bottlingWorker.bottleBeer(bottleRequest.getWort(), CorrelationIdHolder.get());
    }

    void notifyPrezentatr() {
        restClient.forService("prezentatr")
                .put().onUrl("/feed/butelkatr")
                .withoutBody()
                .withHeaders().contentType(Version.PREZENTATR_V1)
                .andExecuteFor().ignoringResponse();
    }
}
