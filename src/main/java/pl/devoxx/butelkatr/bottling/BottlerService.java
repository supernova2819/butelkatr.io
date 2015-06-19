package pl.devoxx.butelkatr.bottling;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.correlationid.CorrelationIdHolder;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;
import pl.devoxx.butelkatr.bottling.model.BottleRequest;
import pl.devoxx.butelkatr.bottling.model.Version;

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
        restClient.forService("prezentatr").retryUsing(retryExecutor)
                .put().onUrl("/feed/butelkatr/"+ CorrelationIdHolder.get())
                .withoutBody()
                    .withHeaders().contentType(Version.PREZENTATR_V1)
                .andExecuteFor().ignoringResponseAsync();

        bottlingWorker.bottleBeer(bottleRequest.getWort(), CorrelationIdHolder.get());
    }
}
