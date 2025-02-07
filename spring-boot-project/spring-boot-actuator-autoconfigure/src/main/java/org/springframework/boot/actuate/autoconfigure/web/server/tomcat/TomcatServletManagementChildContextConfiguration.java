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

package org.springframework.boot.actuate.autoconfigure.web.server.tomcat;

import org.apache.catalina.Valve;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.valves.AccessLogValve;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextType;
import org.springframework.boot.actuate.autoconfigure.web.server.AccessLogCustomizer;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.embedded.TomcatVirtualThreadsWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.embedded.TomcatWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.TomcatServletWebServerFactoryCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;

/**
 * {@link ManagementContextConfiguration @ManagementContextConfiguration} for Tomcat-based
 * servlet web endpoint infrastructure when a separate management context running on a
 * different port is required.
 *
 * @author Andy Wilkinson
 */
@ConditionalOnClass(Tomcat.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(ManagementServerProperties.class)
@ManagementContextConfiguration(value = ManagementContextType.CHILD, proxyBeanMethods = false)
class TomcatServletManagementChildContextConfiguration {

	@Bean
	TomcatAccessLogCustomizer tomcatManagementAccessLogCustomizer(ManagementServerProperties properties) {
		return new TomcatAccessLogCustomizer(properties);
	}

	@Bean
	ServletManagementWebServerFactoryCustomizer servletManagementWebServerFactoryCustomizer(
			ListableBeanFactory beanFactory) {
		return new ServletManagementWebServerFactoryCustomizer(beanFactory, ServletWebServerFactoryCustomizer.class,
				TomcatWebServerFactoryCustomizer.class, TomcatServletWebServerFactoryCustomizer.class,
				TomcatVirtualThreadsWebServerFactoryCustomizer.class);
	}

	static class TomcatAccessLogCustomizer extends AccessLogCustomizer<TomcatServletWebServerFactory> {

		TomcatAccessLogCustomizer(ManagementServerProperties properties) {
			super(properties);
		}

		@Override
		public void customize(TomcatServletWebServerFactory factory) {
			AccessLogValve accessLogValve = findAccessLogValve(factory);
			if (accessLogValve == null) {
				return;
			}
			accessLogValve.setPrefix(customizePrefix(accessLogValve.getPrefix()));
		}

		private AccessLogValve findAccessLogValve(TomcatServletWebServerFactory factory) {
			for (Valve engineValve : factory.getEngineValves()) {
				if (engineValve instanceof AccessLogValve accessLogValve) {
					return accessLogValve;
				}
			}
			return null;
		}

	}

}
