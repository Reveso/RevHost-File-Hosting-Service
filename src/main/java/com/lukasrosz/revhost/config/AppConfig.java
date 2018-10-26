package com.lukasrosz.revhost.config;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@Configuration
public class AppConfig {
	
	@Autowired
	private Environment env;

	@Bean
	public DataSource revhostDataSource() throws IOException {
		MysqlDataSource revhostDataSource = new MysqlDataSource();

		System.out.println(">>> revhost-jdbc.url=" + env.getProperty("revhost-jdbc.url"));
		System.out.println(">>> revhost-jdbc.user=" + env.getProperty("revhost-jdbc.user"));

		revhostDataSource.setUrl(env.getProperty("revhost-jdbc.url"));
		revhostDataSource.setUser(env.getProperty("revhost-jdbc.user"));
		revhostDataSource.setPassword(env.getProperty("revhost-jdbc.password"));
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

}
