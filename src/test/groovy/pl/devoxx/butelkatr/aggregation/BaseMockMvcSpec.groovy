package pl.devoxx.butelkatr.aggregation
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc
import pl.devoxx.butelkatr.aggregation.model.Ingredient
import pl.devoxx.butelkatr.aggregation.model.Ingredients
import pl.devoxx.butelkatr.aggregation.model.Order
import spock.lang.Specification

abstract class BaseMockMvcSpec extends Specification {

    protected static final int QUANTITY = 200

    IngredientsAggregator ingredientsAggregator = Stub()

    def setup() {
        setupMocks()
        RestAssuredMockMvc.standaloneSetup(new IngredientsController(ingredientsAggregator))
    }

    void setupMocks() {
        ingredientsAggregator.fetchIngredients(_) >> { Order order ->
            return new Ingredients(order.items.collect { new Ingredient(it, QUANTITY)})
        }
    }

}
