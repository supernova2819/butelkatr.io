package pl.uservices.butelkatr;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Created by i304608 on 05.11.2015.
 */
@RestController
public class ButelkatrController {
	@RequestMapping(value = "/beer", method = RequestMethod.POST, consumes = "application/butelkator.v1+json")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void createBeer(@RequestBody BeerCreationDTO beerCreationDTO) {
	}
}