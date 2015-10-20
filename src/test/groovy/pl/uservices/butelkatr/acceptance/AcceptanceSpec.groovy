package pl.uservices.butelkatr.acceptance

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import pl.uservices.butelkatr.bottling.model.Version
import pl.uservices.butelkatr.base.MicroserviceMvcWiremockSpec

import static java.net.URI.create
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

@ContextConfiguration
class AcceptanceSpec extends MicroserviceMvcWiremockSpec {

    def 'should call external services to aggregate ingredients'() {
        expect:
            mockMvc.perform(post(create('/bottle'))
                .header('Content-Type', Version.V1)
                .content('{"wort": 1000}'))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
    }

}
