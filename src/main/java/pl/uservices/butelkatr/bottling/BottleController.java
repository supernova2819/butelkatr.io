package pl.uservices.butelkatr.bottling;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.uservices.butelkatr.bottling.model.BottleRequest;
import pl.uservices.butelkatr.bottling.model.Version;

@RestController
@RequestMapping(value = "/bottle", consumes = Version.V1, produces = MediaType.APPLICATION_JSON_VALUE)
public class BottleController {

    private final BottlerService bottlerService;

    @Autowired
    public BottleController(BottlerService bottlerService) {
        this.bottlerService = bottlerService;
    }

    @RequestMapping(method = RequestMethod.POST, produces = Version.V1, consumes = Version.V1)
    public void bottle(@RequestBody BottleRequest bottleRequest) {
        bottlerService.bottle(bottleRequest);
    }

}
