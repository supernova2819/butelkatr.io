package pl.uservices.butelkatr.bottling;

import static org.springframework.cloud.sleuth.Trace.TRACE_ID_NAME;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.trace.TraceContextHolder;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pl.uservices.butelkatr.bottling.model.BottleRequest;
import pl.uservices.butelkatr.bottling.model.Version;

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
    public void bottle(@RequestBody BottleRequest bottleRequest, HttpEntity requestEntity,
                       @RequestHeader("PROCESS-ID") String processId,
                       @RequestHeader(value = TestConfigurationHolder.TEST_COMMUNICATION_TYPE_HEADER_NAME,
                               defaultValue = "REST_TEMPLATE", required = false)
                                   TestConfigurationHolder.TestCommunicationType testCommunicationType) {
        TestConfigurationHolder.TEST_CONFIG.set(TestConfigurationHolder.builder().testCommunicationType(testCommunicationType).build());
        log.info("Current traceid is {}", TraceContextHolder.isTracing() ? TraceContextHolder.getCurrentSpan().getTraceId() : "");
        log.info("TraceId from headers {}", requestEntity.getHeaders().get(TRACE_ID_NAME));
        log.info("Process ID from headers {}", processId);
        bottlerService.bottle(bottleRequest, processId);
    }

}
