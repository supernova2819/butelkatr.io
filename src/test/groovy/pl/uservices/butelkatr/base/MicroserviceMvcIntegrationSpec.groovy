package pl.uservices.butelkatr.base

import com.ofg.infrastructure.base.MvcIntegrationSpec
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.test.context.ContextConfiguration
import pl.uservices.butelkatr.Application

@ContextConfiguration(classes = [Application], loader = SpringApplicationContextLoader)
class MicroserviceMvcIntegrationSpec extends MvcIntegrationSpec {
}
