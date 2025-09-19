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
        basePackages = "com.fourstory.fourstory_api.repository.tglobal",
        entityManagerFactoryRef = "tglobalEntityManagerFactory",
        transactionManagerRef = "tglobalTransactionManager"
)
public class TGlobalConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.tglobal")
    public DataSourceProperties tglobalDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "tglobalDataSource")
    public DataSource tglobalDataSource() {
        return tglobalDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "tglobalEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean tgameEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(tglobalDataSource());
        emf.setPackagesToScan("com.fourstory.fourstory_api.model.tglobal");
        emf.setPersistenceUnitName("tglobal");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        return emf;
    }

    @Bean(name = "tglobalTransactionManager")
    public PlatformTransactionManager tglobalTransactionManager(
            @Qualifier("tglobalEntityManagerFactory") EntityManagerFactory tgameEntityManagerFactory
    ) {
        return new JpaTransactionManager(tgameEntityManagerFactory);
    }
}
