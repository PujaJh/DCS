/**
 * 
 */
package com.amnex.agristack.gateway.config;

import org.springframework.context.annotation.Configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import reactor.core.publisher.Mono;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author majid.belim
 *
 */
@Configuration
public class SpringCloudConfig {

	@Autowired
	private LoggingFilter loggingFilter;

	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {

		RouteLocator routeLocator = builder.routes()
				.route("agristackQA", r -> r.path("/crop-survey-api-qa/**")
				.filters(f -> f.circuitBreaker(c -> c.setName("agristackQaCB").setFallbackUri("/defaultUrl")))
				.uri("lb://AGRI-STACKQA"))
				.route("agristackBeta",r -> r.path("/crop-survey-api-beta/**")
				.filters(f->f.circuitBreaker(c -> c.setName("agristackQaCB").setFallbackUri("/defaultUrl")))
				.uri("lb://AGRI-STACKBETA"))
				.route("agristackloadtest",r -> r.path("/crop-survey-api-lt/**")
				.filters(f->f.circuitBreaker(c -> c.setName("agristackQaCB").setFallbackUri("/defaultUrl")))
				.uri("lb://AGRI-STACKLT"))
				.route("agristackBetaMIS",r -> r.path("/crop-survey-api-beta-mis/**")
						.filters(f->f.circuitBreaker(c -> c.setName("agristackBetaMIS").setFallbackUri("/defaultUrl")))
						.uri("lb://AGRI-STACKBETAMIS"))
				.route("agristackBetaAr",r -> r.path("/crop-survey-api-beta-ar/**")
				.filters(f->f.circuitBreaker(c -> c.setName("agristackQaCB").setFallbackUri("/defaultUrl")))
				.uri("lb://AGRI-STACKBETA-AR"))						
//				
//				.filter(loggingFilter)
				.build();
		return routeLocator;
	}

//	@Bean
//	public ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory(
//			CircuitBreakerRegistry circuitBreakerRegistry) {
//		ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory = new ReactiveResilience4JCircuitBreakerFactory();
//		reactiveResilience4JCircuitBreakerFactory.configureCircuitBreakerRegistry(circuitBreakerRegistry);
//		return reactiveResilience4JCircuitBreakerFactory;
//	}
//	
	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
				.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(1800)).build()).build());
	}

}
