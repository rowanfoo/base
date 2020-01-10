package com.dhamma.base.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class IgniteConfig {
    @Bean
    public IgniteConfiguration igniteConfiguration() {
        TcpDiscoverySpi spi = new TcpDiscoverySpi().setIpFinder(new TcpDiscoveryVmIpFinder(true));
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setPeerClassLoadingEnabled(true);
        // Override default discovery SPI.
        cfg.setDiscoverySpi(spi);
        cfg.setIgniteInstanceName("The Painter");
        return cfg;
    }

    @Bean(destroyMethod = "close")
    Ignite ignite(IgniteConfiguration cfg) throws IgniteException {
        // Start Ignite node.
        Ignite ig = Ignition.start(cfg);
        return ig;

    }

}

