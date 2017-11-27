package com.bing.service;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Component;
@Component
public class CustomJdbcClientDetailsService extends JdbcClientDetailsService {
	@Autowired
	public CustomJdbcClientDetailsService(DataSource dataSource) {
		super(dataSource);
	}
}
