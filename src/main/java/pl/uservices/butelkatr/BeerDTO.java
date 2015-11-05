package pl.uservices.butelkatr;

import javax.validation.constraints.Min;

/**
 * Created by i304608 on 05.11.2015.
 */
public class BeerDTO {
	
	@Min(0)
	public Long quantity;

	public BeerDTO(){
		this(0L);
	}
	
	
	public BeerDTO(Long quantity) {
		this.quantity = quantity;
	}
}