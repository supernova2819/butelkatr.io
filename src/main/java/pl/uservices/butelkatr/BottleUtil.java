package pl.uservices.butelkatr;

public class BottleUtil {
	
	public static BottleDTO produceBottle(Long beerQuantity)
	{
		long bottleQuantity = beerQuantity * 2;
		return new BottleDTO(bottleQuantity);
	}
}
