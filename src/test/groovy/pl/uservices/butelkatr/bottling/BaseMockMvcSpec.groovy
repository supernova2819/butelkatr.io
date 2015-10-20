package pl.uservices.butelkatr.bottling
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc
import spock.lang.Specification

abstract class BaseMockMvcSpec extends Specification {

    protected static final int QUANTITY = 200

    BottlerService bottlerService = Stub()

    def setup() {
        setupMocks()
        RestAssuredMockMvc.standaloneSetup(new BottleController(bottlerService))
    }

    void setupMocks() {
    }

}
