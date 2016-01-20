package qcom.cas.sample.app;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;

@SpringBootApplication
@EnableMetrics(proxyTargetClass = true)
public class App extends SpringBootServletInitializer {

    @Value("${graphite.host}")
    private String GRAPHITE_SERVER_HOST;

    @Value("${graphite.port}")
    private int GRAPHITE_SERVER_PORT;

    @Value("${reporter.interval}")
    private long REPORTER_INTERVAL;

    @Value("${graphite.prefix}")
    private String GRAPHITE_PREFIX;

    public static void main( String[] args ) {
        SpringApplication.run(App.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure( SpringApplicationBuilder application ) {
        return application.sources(App.class);
    }

    @Autowired
    private MetricRegistry metricRegistry;

    @Bean
    public GraphiteReporter graphiteReporter() {
        // adding some addition metric
        metricRegistry.register("jvm.gc", new GarbageCollectorMetricSet());
        metricRegistry.register("jvm.mem", new MemoryUsageGaugeSet());
        metricRegistry.register("jvm.thread-states", new ThreadStatesGaugeSet());

        Graphite graphite = new Graphite(new InetSocketAddress(GRAPHITE_SERVER_HOST, GRAPHITE_SERVER_PORT));
        GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry).prefixedWith(GRAPHITE_PREFIX)
                .build(graphite);
        reporter.start(REPORTER_INTERVAL, TimeUnit.MILLISECONDS);
        return reporter;
    }

}
