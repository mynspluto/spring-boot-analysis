/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.actuate.autoconfigure.web.server.undertow;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextType;
import org.springframework.boot.actuate.autoconfigure.web.server.AccessLogCustomizer;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.ServerProperties.Undertow;
import org.springframework.boot.autoconfigure.web.embedded.UndertowWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.UndertowServletWebServerFactoryCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * {@link ManagementContextConfiguration @ManagementContextConfiguration} for
 * Undertow-based servlet web endpoint infrastructure when a separate management context
 * running on a different port is required.
 *
 * @author Andy Wilkinson
 */
@ConditionalOnClass(Undertow.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(ManagementServerProperties.class)
@ManagementContextConfiguration(value = ManagementContextType.CHILD, proxyBeanMethods = false)
class UndertowServletManagementChildContextConfiguration {

	@Bean
	UndertowAccessLogCustomizer undertowManagementAccessLogCustomizer(ManagementServerProperties properties) {
		return new UndertowAccessLogCustomizer(properties);
	}

	@Bean
	ServletManagementWebServerFactoryCustomizer servletManagementWebServerFactoryCustomizer(
			ListableBeanFactory beanFactory) {
		return new ServletManagementWebServerFactoryCustomizer(beanFactory, ServletWebServerFactoryCustomizer.class,
				UndertowServletWebServerFactoryCustomizer.class, UndertowWebServerFactoryCustomizer.class);
	}

	static class UndertowAccessLogCustomizer extends AccessLogCustomizer<UndertowServletWebServerFactory>
			implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

		UndertowAccessLogCustomizer(ManagementServerProperties properties) {
			super(properties);
		}

		@Override
		public void customize(UndertowServletWebServerFactory factory) {
			factory.setAccessLogPrefix(customizePrefix(factory.getAccessLogPrefix()));
		}

	}

}
