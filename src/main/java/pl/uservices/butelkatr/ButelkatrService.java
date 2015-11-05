package pl.uservices.butelkatr;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.nurkiewicz.asyncretry.RetryExecutor;
import com.ofg.infrastructure.discovery.ServiceAlias;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by i304608 on 05.11.2015.
 */
@Service
public class ButelkatrService {

	private ServiceRestClient serviceRestClient;

	private RetryExecutor retryExecutor;

	private Queue<Integer> beerQuantityQueue = new ConcurrentLinkedQueue<>();

	@Autowired
	public ButelkatrService(ServiceRestClient serviceRestClient,
			RetryExecutor retryExecutor) {
		this.serviceRestClient = serviceRestClient;
		this.retryExecutor = retryExecutor;
	}

	public void informBeerCreated(Integer quantity) {
		beerQuantityQueue.offer(quantity);
		produceBottles();
	}

	
	
	@Async
	@Scheduled(fixedRate= 5000)
	private void produceBottles() {
		createBottles();
		fillBottles();
	}

	private void  createBottles(){
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private void fillBottles() {
		Integer beerQuantity = beerQuantityQueue.poll();
		if (beerQuantity == null)
			return;

		BottleDTO bottle = BottleFactory.produceBottle(beerQuantity);
		notifyPresenter(bottle);
	}
	
	private void notifyPresenter(BottleDTO bootles){
		serviceRestClient
		.forService(new ServiceAlias("prezentatr"))
		.retryUsing(retryExecutor)
		.post()
		.withCircuitBreaker(
				HystrixCommand.Setter
						.withGroupKey(HystrixCommandGroupKey.Factory
								.asKey("prezentatorBottle")))
		.onUrl("/bottle").body(bootles)
		.withHeaders().contentType("application/prezentator.v1+json")
		.andExecuteFor().ignoringResponseAsync();
	}
}
