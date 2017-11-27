/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bing.controller;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bing.domain.Greeting;
import com.bing.domain.User;

@RestController
public class GreetingController {

	private static final String template = "Hello, %s!";

	private final AtomicLong counter = new AtomicLong();
	/**
	 * 如果使用client_credentials授权模式，则抛异常，
	 * 因为该模式和用户信息没有关系
	 * @param user
	 * @return
	 */
	@RequestMapping("/greeting")
	public Greeting greeting(@AuthenticationPrincipal User user) {
		return new Greeting(counter.incrementAndGet(),
				String.format(template, user.getName()));
	}

}
