package pl.uservices.butelkatr;

public class BottleFactory {
	public static BottleDTO produceBottle(Integer beerQuantity)
	{
		Integer bottleQuantity = beerQuantity * 2;
		return new BottleDTO(bottleQuantity);
	}
}
