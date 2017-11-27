package com.bing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import com.bing.service.CustomJdbcClientDetailsService;
import com.bing.service.CustomUserDetailsService;

@Configuration
public class OAuth2ServerConfiguration {

	private static final String RESOURCE_ID = "restservice";

	/**
	 * 理论上，每个项目只有一个resourceid
	 * 
	 * @author WangBing
	 *
	 */
	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
		
		/**
		 * 网上说不配置该设置，会导致@preAuthorized("#oauth2.hasscope('write')")被拒绝，
		 * 但是测试中没有发现该情况
		 */
		/*@Bean
		public ResourceServerTokenServices myUserInfoTokenServices() {
			return new CustomUserInfoTokenServices(sso.getUserInfoUri(), sso.getClientId());
		}*/

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			// @formatter:off
			resources.resourceId(RESOURCE_ID);
			// @formatter:on
		}

		/**
		 * 开启PrePostEnable以后，就可以直接在方法上进行 @PreAuthorized("#oauth2.hasScope('read')")，否则需要
		 * http.authorizeRequests().anyRequest().access("#oauth2.hasScope('read')")
		 * 
		 */
		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http.authorizeRequests()//.antMatchers("/users").hasRole("ADMIN")// 根据角色判断
					.antMatchers("/home").hasRole("USER").antMatchers("/greeting").authenticated();
			http.authorizeRequests().antMatchers("/users").access("#oauth2.hasScope('write')");
			// @formatter:on
		}

	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {
		// private TokenStore tokenStore = new InMemoryTokenStore();
		@Autowired
		private CustomJdbcTokenStore customJdbcTokenStore;
		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		@Autowired
		private CustomUserDetailsService userDetailsService;

		@Autowired
		private CustomJdbcClientDetailsService customJdbcClientDetailsService;

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			// @formatter:off
			endpoints.tokenStore(this.customJdbcTokenStore).authenticationManager(this.authenticationManager)
					.userDetailsService(userDetailsService);
			// @formatter:on
		}
		/**
		 * client_credentials授权模式跟用户无绑定，所以不会传送用户信息
		 */
		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
			// @formatter:off
			clients.withClientDetails(customJdbcClientDetailsService);
			/*
			 * clients .inMemory() .withClient("clientapp")
			 * .authorizedGrantTypes("password", "refresh_token")
			 * .authorities("USER") .scopes("read", "write")
			 * .resourceIds(RESOURCE_ID) .secret("123456");
			 */
			// @formatter:on
		}

		@Bean
		@Primary
		public DefaultTokenServices tokenServices() {
			DefaultTokenServices tokenServices = new DefaultTokenServices();
			tokenServices.setSupportRefreshToken(true);
			tokenServices.setTokenStore(this.customJdbcTokenStore);
			return tokenServices;
		}

	}

}
