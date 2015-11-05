package pl.uservices.butelkatr;

/**
 * Created by i304608 on 05.11.2015.
 */
public class BottleDTO {
	public Long quantity;

	public BottleDTO()
	{
		this(0L);
	}
	
	public BottleDTO(Long quantity) {
		this.quantity = quantity;
	}
}