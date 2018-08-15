package com.lukasrosz.revhost;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@SpringBootApplication
@EnableAutoConfiguration(exclude=HibernateJpaAutoConfiguration.class)
public class RevhostApplication {

	public static void main(String[] args) {
		SpringApplication.run(RevhostApplication.class, args);
	}
	
	@Autowired
	private Environment env;

	@Bean
	public DataSource revhostDataSource() throws IOException {
		
		// create connection pool
		ComboPooledDataSource revhostDataSource = new ComboPooledDataSource();

		// set the jdbc driver class
		try {
			revhostDataSource.setDriverClass(env.getProperty("revhost-jdbc.driver"));
		} catch (PropertyVetoException e) {
			throw new RuntimeException(e);
		}

		// log the connection props
		System.out.println(">>> revhost-jdbc.url=" + env.getProperty("revhost-jdbc.url"));
		System.out.println(">>> revhost-jdbc.user=" + env.getProperty("revhost-jdbc.user"));

		// set database connection props
		revhostDataSource.setJdbcUrl(env.getProperty("revhost-jdbc.url"));
		revhostDataSource.setUser(env.getProperty("revhost-jdbc.user"));
		revhostDataSource.setPassword(env.getProperty("revhost-jdbc.password"));

		// set connection pool props
		revhostDataSource.setInitialPoolSize(getIntProperty("connection.pool.initialPoolSize", env));
		revhostDataSource.setMinPoolSize(getIntProperty("connection.pool.minPoolSize", env));
		revhostDataSource.setMaxPoolSize(getIntProperty("connection.pool.maxPoolSize", env));
		revhostDataSource.setMaxIdleTime(getIntProperty("connection.pool.maxIdleTime", env));

		return revhostDataSource;
	}

	@Bean
	@Autowired
	public LocalSessionFactoryBean revhostSessionFactory(DataSource revhostDataSource) {
		LocalSessionFactoryBean revhostSessionFactory = new LocalSessionFactoryBean();
		revhostSessionFactory.setDataSource(revhostDataSource);
		revhostSessionFactory.setPackagesToScan(new String[] { "com.lukasrosz.revhost" });

		revhostSessionFactory.setHibernateProperties(hibernateProperties(env));
		return revhostSessionFactory;
	}

	@Bean
	@Autowired
	public HibernateTransactionManager revhostTransactionManager(SessionFactory revhostSessionFactory) {
		HibernateTransactionManager revhostTransactionManager = new HibernateTransactionManager();

		revhostTransactionManager.setSessionFactory(revhostSessionFactory);
		return revhostTransactionManager;
	}
	
	private Properties hibernateProperties(Environment env) {
		return new Properties() {
			private static final long serialVersionUID = 1L;

			{
				setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
				setProperty("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
			}
		};
	}
	
	private int getIntProperty(String propName, Environment env) {
		String propVal = env.getProperty(propName);
		int intPropVal = Integer.parseInt(propVal);

		return intPropVal;
	}
}
