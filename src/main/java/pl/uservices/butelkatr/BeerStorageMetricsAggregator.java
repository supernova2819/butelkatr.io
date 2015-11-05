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
        private final Meter totalBeer;
        private final Meter beerQueueLength;

        @Autowired
        BeerStorageMetricsAggregator(MetricRegistry metricRegistry, BeerStorage beerStorage) {
            this.beerStorage = beerStorage;
            this.totalBeer = metricRegistry.meter("totalBeer");
            this.beerQueueLength = metricRegistry.meter("beerQueueLength");
            setupMeters(metricRegistry);
        }

        private void setupMeters(MetricRegistry metricRegistry) {
            metricRegistry.register("totalBeer", (Gauge<Long>) () -> beerStorage.getTotalBeer());
            metricRegistry.register("beerQueueLength", (Gauge<Integer>) () -> beerStorage.getQueueSize());
        }

//        public void doSomethingMeaningful(long sample) {
//            // do something and mark the metric
//            totalBeer.mark(sample);
//            // do something else
//        }

}
