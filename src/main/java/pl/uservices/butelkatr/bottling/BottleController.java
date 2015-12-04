package pl.uservices.butelkatr.bottling;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.trace.TraceContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.uservices.butelkatr.bottling.model.BottleRequest;
import pl.uservices.butelkatr.bottling.model.Version;

import static org.springframework.cloud.sleuth.Trace.TRACE_ID_NAME;

@RestController
@RequestMapping(value = "/bottle", consumes = Version.V1, produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class BottleController {

    private final BottlerService bottlerService;

    @Autowired
    public BottleController(BottlerService bottlerService) {
        this.bottlerService = bottlerService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = Version.V1, consumes = Version.V1)
    public void bottle(@RequestBody BottleRequest bottleRequest, HttpEntity requestEntity) {
        log.info("Current traceid is {}", TraceContextHolder.isTracing() ? TraceContextHolder.getCurrentSpan().getTraceId() : "");
        log.info("TraceId from headers {}", requestEntity.getHeaders().get(TRACE_ID_NAME));
        bottlerService.bottle(bottleRequest);
    }

}
