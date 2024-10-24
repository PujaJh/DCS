/**
 * 
 */
package com.amnex.agristack.gateway.config;

import javax.naming.spi.ObjectFactory;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

/**
 * @author majid.belim
 *
 */
@Configuration
public class FeignResponseDecoderConfig {
	@Bean
	public Decoder decoder(ObjectMapper objectMapper) {
		return new JacksonDecoder(objectMapper);
	}

	@Bean
	public Encoder encoder(ObjectMapper objectMapper) {
		return new JacksonEncoder(objectMapper);
	}
}