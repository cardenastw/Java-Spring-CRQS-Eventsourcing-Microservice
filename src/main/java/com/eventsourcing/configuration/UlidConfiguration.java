package com.eventsourcing.configuration;

import de.huxhorn.sulky.ulid.ULID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
@RequiredArgsConstructor
public class UlidConfiguration
{

    @Bean
    public ULID idGenerator() {
        return new ULID();
    }
}