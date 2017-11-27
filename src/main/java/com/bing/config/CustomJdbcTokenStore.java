package com.bing.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;
@Component
public class CustomJdbcTokenStore extends JdbcTokenStore {
	@Autowired//构造类注入
	public CustomJdbcTokenStore(DataSource dataSource) {
		super(dataSource);
	}
}
