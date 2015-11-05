package pl.uservices.butelkatr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;

/**
 * Created by i304608 on 05.11.2015.
 */
@RestController
public class ButelkatrController {

	@Autowired
	ButelkatrService butelkatrService;
	
	@RequestMapping(value = "/beer", method = RequestMethod.POST, consumes = "application/butelkator.v1+json")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void createBeer(@RequestBody @Validated BeerDTO beerCreationDTO) {
		butelkatrService.informBeerCreated(beerCreationDTO.quantity);
	}
}
