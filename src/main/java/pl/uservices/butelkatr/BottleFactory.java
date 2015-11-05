package pl.uservices.butelkatr;

public class BottleFactory {
	
	public static BottleDTO produceBottle(Integer beerQuantity)
	{
		return produceBottle(Long.valueOf(beerQuantity));
	}
	
	public static BottleDTO produceBottle(Long beerQuantity)
	{
		long bottleQuantity = beerQuantity * 2;
		return new BottleDTO(bottleQuantity);
	}
}
