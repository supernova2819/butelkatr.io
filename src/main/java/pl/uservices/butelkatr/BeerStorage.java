package pl.uservices.butelkatr;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.google.common.base.Optional;


@Component
public class BeerStorage {
	
	private  Queue<Integer> beerQuantityQueue = new ConcurrentLinkedQueue<>();
	
	private  AtomicLong beerQuantitySum = new AtomicLong(0);
	
	public  void addBeer(Integer quantity)
	{
		beerQuantityQueue.offer(quantity);
		beerQuantitySum.addAndGet(quantity);
	}
	
	public  Optional<Integer> getBeer()
	{
		Integer beerQuantity = beerQuantityQueue.poll();
		if (beerQuantity != null){
			beerQuantitySum.addAndGet(-beerQuantity);
			return Optional.of(beerQuantity);
		}
		
		return Optional.absent();
	}
	
	public  long getTotalBeer(){
		return beerQuantitySum.get();
	}
	
	public  int getQueueSize(){
		return beerQuantityQueue.size();
	}
	
}
