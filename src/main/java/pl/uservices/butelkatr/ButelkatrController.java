package pl.uservices.butelkatr;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
	private ServiceRestClient serviceRestClient;

	@Autowired
	private RetryExecutor retryExecutor;
	
	@RequestMapping(value = "/beer", method = RequestMethod.POST, consumes = "application/butelkator.v1+json")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void createBeer(@RequestBody BeerCreationDTO beerCreationDTO) {
		try {
			Thread.sleep(2000);
			
			serviceRestClient.forService("prezentatr")
			.retryUsing(retryExecutor)
			.post()
			.withCircuitBreaker(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("prezentatorBottle")))
			.onUrl("/bottle")
			.body( new BottleDTO(0))
			.withHeaders().contentType("application/prezentator.v1+json")
			.andExecuteFor()
			.ignoringResponseAsync();
			
		} catch (InterruptedException e) {
			// ignore
		}
	}

	public ServiceRestClient getServiceRestClient() {
		return serviceRestClient;
	}

	public void setServiceRestClient(ServiceRestClient serviceRestClient) {
		this.serviceRestClient = serviceRestClient;
	}
}