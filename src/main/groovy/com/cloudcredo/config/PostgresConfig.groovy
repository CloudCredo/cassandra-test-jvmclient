package com.cloudcredo.config
import com.cloudcredo.domain.PostgressqlDO
import org.cloudfoundry.runtime.env.CloudEnvironment
import org.cloudfoundry.runtime.env.RdbmsServiceInfo
import org.cloudfoundry.runtime.service.relational.RdbmsServiceCreator
import org.hibernate.dialect.PostgreSQL82Dialect
import org.hibernate.ejb.HibernatePersistence
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

import javax.sql.DataSource
/**
 * @author: chris
 * @date: 05/03/2013
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = [ "com.cloudcredo.repositories" ])
class PostgresConfig {

    @Bean
    DataSource dataSource() {
        CloudEnvironment cloud = new CloudEnvironment();
        Collection<RdbmsServiceInfo> postgresService = cloud.getServiceInfos(RdbmsServiceInfo.class);
        RdbmsServiceCreator dataSourceCreator = new RdbmsServiceCreator();
        return dataSourceCreator.createService(postgresService.iterator().next());
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(PostgressqlDO.class.getPackage().getName());
        em.setPersistenceProvider(new HibernatePersistence());
        em.setJpaPropertyMap(jpaProperties());
        return em;
    }

    @Bean
    PlatformTransactionManager transactionManager() throws Exception {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }

    private Map<String, String> jpaProperties() {
        Map<String, String> p = new HashMap<String, String>();
        p.put(org.hibernate.cfg.Environment.HBM2DDL_AUTO, "create");
        p.put(org.hibernate.cfg.Environment.DIALECT, PostgreSQL82Dialect.class.getName());
        p.put(org.hibernate.cfg.Environment.SHOW_SQL, "true");
        return p;
    }

}
