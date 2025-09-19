package com.fourstory.fourstory_api.config;


import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.fourstory.fourstory_api.repository.tgame",
        entityManagerFactoryRef = "tgameEntityManagerFactory",
        transactionManagerRef = "tgameTransactionManager"
)
public class TGameConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.tgame")
    public DataSourceProperties tgameDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "tgameDataSource")
    public DataSource tgameDataSource() {
        return tgameDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "tgameEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean tgameEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(tgameDataSource());
        emf.setPackagesToScan("com.fourstory.fourstory_api.model.tgame");
        emf.setPersistenceUnitName("tgame");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        return emf;
    }

    @Bean(name = "tgameTransactionManager")
    public PlatformTransactionManager tgameTransactionManager(
            @Qualifier("tgameEntityManagerFactory") EntityManagerFactory tgameEntityManagerFactory
    ) {
        return new JpaTransactionManager(tgameEntityManagerFactory);
    }
}
