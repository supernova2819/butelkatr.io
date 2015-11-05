package pl.uservices.butelkatr;

/**
 * Created by i304608 on 05.11.2015.
 */
public class BeerDTO {
	public Integer quantity;

	public BeerDTO(){
		this(0);
	}
	
	
	public BeerDTO(Integer quantity) {
		this.quantity = quantity;
	}
}