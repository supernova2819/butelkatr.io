package pl.uservices.butelkatr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.discovery.ServiceAlias;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;

/**
 * Created by i304608 on 05.11.2015.
 */
@Service
public class ButelkatrService {

	private Logger log = LoggerFactory.getLogger(getClass());

	private ServiceRestClient serviceRestClient;

	private RetryExecutor retryExecutor;
	
	private BeerStorage beerStorage;

	@Autowired
	public ButelkatrService(ServiceRestClient serviceRestClient,
			RetryExecutor retryExecutor, BeerStorage beerStorage) {
		this.serviceRestClient = serviceRestClient;
		this.retryExecutor = retryExecutor;
		this.beerStorage = beerStorage;
	}

	@Async
	public void informBeerCreated(Long quantity) {
		beerStorage.addBeer(quantity);
		notifyBottlesTotal();
		log.info("Bottling process started.");
		produceBottles();
	}
	
	private void produceBottles() {
		createBottles();
		fillBottles();
	}

	private void createBottles() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void fillBottles() {
		Optional<Long> beerQuantity = beerStorage.getBeer();
		if (beerQuantity.isPresent()) {
			BottleDTO bottle = BottleUtil.produceBottle(beerQuantity.get());
			log.info("Produced {0} bottles", bottle.quantity);
			notifyBottlesProduced(bottle);
		}
	}
	
	private BottleDTO getBottleQueue(){
		return BottleUtil.produceBottle(beerStorage.getTotalBeer());
	}

	private void notifyBottlesTotal() {
		serviceRestClient
				.forService(new ServiceAlias("prezentatr"))
				.retryUsing(retryExecutor)
				.post()
				.withCircuitBreaker(
						HystrixCommand.Setter
								.withGroupKey(HystrixCommandGroupKey.Factory
										.asKey("prezentatorBottle")))
				.onUrl("/bottleQueue").body(getBottleQueue()).withHeaders()
				.contentType("application/prezentator.v1+json").andExecuteFor()
				.ignoringResponseAsync();
	}
	
	private void notifyBottlesProduced(BottleDTO bootles) {
		serviceRestClient
				.forService(new ServiceAlias("prezentatr"))
				.retryUsing(retryExecutor)
				.post()
				.withCircuitBreaker(
						HystrixCommand.Setter
								.withGroupKey(HystrixCommandGroupKey.Factory
										.asKey("prezentatorBottle")))
				.onUrl("/bottle").body(bootles).withHeaders()
				.contentType("application/prezentator.v1+json").andExecuteFor()
				.ignoringResponseAsync();
	}
}
