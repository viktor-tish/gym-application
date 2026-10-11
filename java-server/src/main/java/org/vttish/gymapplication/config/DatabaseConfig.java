package org.vttish.gymapplication.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@RequiredArgsConstructor
public class DatabaseConfig {
  private final Environment env;

  @Bean
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();

    config.setJdbcUrl(env.getRequiredProperty("db.url"));
    config.setUsername(env.getRequiredProperty("db.username"));
    config.setPassword(env.getRequiredProperty("db.password"));
    config.setDriverClassName(env.getRequiredProperty("db.driver"));

    return new HikariDataSource(config);
  }

  @Bean(initMethod = "migrate")
  public Flyway flyway(DataSource dataSource) {
    return Flyway.configure()
        .dataSource(dataSource)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true)
        .load();
  }

  @Bean
  @DependsOn("flyway")
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

    em.setDataSource(dataSource);
    em.setPackagesToScan("org.vttish.gymapplication");
    em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

    Properties properties = new Properties();

    properties.setProperty("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
    properties.setProperty("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));
    properties.setProperty("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));
    properties.setProperty(
        "hibernate.hbm2ddl.auto",
        env.getRequiredProperty("hibernate.hbm2ddl.auto")
    );
    properties.setProperty(
        "hibernate.physical_naming_strategy",
        "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy"
    );

    em.setJpaProperties(properties);

    return em;
  }

  @Bean
  public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
    JpaTransactionManager transactionManager = new JpaTransactionManager();

    transactionManager.setEntityManagerFactory(entityManagerFactory);

    return transactionManager;
  }
}
