package pl.uservices.butelkatr;

/**
 * Created by i304608 on 05.11.2015.
 */
public class BottleDTO {
	private Integer quantity;

	public Integer getQuantity() {
		return quantity;

	}

	public BottleDTO()
	{
		this(0);
	}
	
	public BottleDTO(Integer quantity) {
		this.quantity = quantity;
	}
}