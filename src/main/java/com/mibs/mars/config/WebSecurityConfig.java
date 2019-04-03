package com.mibs.mars.config;

import javax.persistence.EntityManagerFactory;

import org.hibernate.SessionFactory;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;


import com.mibs.mars.service.UsersDetailsService;


@Configuration
@EnableWebSecurity
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 600)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
		
		httpSecurity
			 .authorizeRequests()
			 .antMatchers("/restore").permitAll()
			 .antMatchers("/forgot_password").permitAll()
			 .antMatchers("/verifyMail").permitAll()
		 	 .antMatchers("/css/**").permitAll()
		 	 .antMatchers("/font-awesome/**").permitAll()
			 .antMatchers("/js/**").permitAll()
			 .antMatchers("/img/**").permitAll()
			 .antMatchers("/fragments/**").permitAll()
			 .antMatchers("/admin/**").permitAll()
	        .anyRequest().authenticated()
	        .and()
	    .formLogin()
	        .loginPage("/login").failureUrl("/login-error").defaultSuccessUrl("/")
	      
	        .permitAll()
	        .and()
	     .logout()                                    
	        .permitAll();
		}
		@Autowired
		public void configureGlobal(AuthenticationManagerBuilder auth, UsersDetailsService usersDetailsService) throws Exception {
		
			auth.userDetailsService(usersDetailsService);
			auth.inMemoryAuthentication().withUser("admin").password("123").roles("ADMIN");
		}
		@Bean
		public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
			return new SecurityEvaluationContextExtension();
		}
		@Bean(name="sessionFactory")
		public SessionFactory sessionFactory(HibernateEntityManagerFactory factory) {
		   return factory.getSessionFactory();
		}
}