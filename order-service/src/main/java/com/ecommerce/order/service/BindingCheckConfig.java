package com.ecommerce.order.service;

import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class BindingCheckConfig {

    //@Bean
    /*public ApplicationRunner checkBindings(BindingServiceProperties props) {
        return args -> props.getBindings().forEach((name, binding) ->
                System.out.printf("✅ Binding detectado: %s -> %s%n", name, binding.getDestination()));

    }*/
}