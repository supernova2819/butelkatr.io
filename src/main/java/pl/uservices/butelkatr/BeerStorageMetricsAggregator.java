package pl.uservices.butelkatr;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by i304608 on 05.11.2015.
 */
@Component
public class BeerStorageMetricsAggregator {

        private final BeerStorage beerStorage;

        @Autowired
        BeerStorageMetricsAggregator(MetricRegistry metricRegistry, BeerStorage beerStorage) {
            this.beerStorage = beerStorage;
            setupGauges(metricRegistry);
        }

        private void setupGauges(MetricRegistry metricRegistry) {
            metricRegistry.register("totalBeerInQueue", (Gauge<Long>) () -> beerStorage.getTotalBeer());
            metricRegistry.register("beerQueueLength", (Gauge<Integer>) () -> beerStorage.getQueueSize());
            metricRegistry.register("totalBeerProcessed", (Gauge<Long>) () -> beerStorage.getTotalBeerProcessed());
        }
}
