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

package org.springframework.boot.actuate.autoconfigure.web.server.jetty;

import java.io.File;

import org.eclipse.jetty.server.CustomRequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.RequestLogWriter;
import org.eclipse.jetty.server.Server;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextType;
import org.springframework.boot.actuate.autoconfigure.web.server.AccessLogCustomizer;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementServerProperties;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.embedded.JettyVirtualThreadsWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.embedded.JettyWebServerFactoryCustomizer;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

/**
 * {@link ManagementContextConfiguration @ManagementContextConfiguration} for Jetty-based
 * servlet web endpoint infrastructure when a separate management context running on a
 * different port is required.
 *
 * @author Andy Wilkinson
 */
@ConditionalOnClass(Server.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties(ManagementServerProperties.class)
@ManagementContextConfiguration(value = ManagementContextType.CHILD, proxyBeanMethods = false)
class JettyServletManagementChildContextConfiguration {

	@Bean
	JettyAccessLogCustomizer jettyManagementAccessLogCustomizer(ManagementServerProperties properties) {
		return new JettyAccessLogCustomizer(properties);
	}

	@Bean
	ServletManagementWebServerFactoryCustomizer servletManagementWebServerFactoryCustomizer(
			ListableBeanFactory beanFactory) {
		return new ServletManagementWebServerFactoryCustomizer(beanFactory, ServletWebServerFactoryCustomizer.class,
				JettyWebServerFactoryCustomizer.class, JettyVirtualThreadsWebServerFactoryCustomizer.class);
	}

	static class JettyAccessLogCustomizer extends AccessLogCustomizer<JettyServletWebServerFactory>
			implements WebServerFactoryCustomizer<JettyServletWebServerFactory> {

		JettyAccessLogCustomizer(ManagementServerProperties properties) {
			super(properties);
		}

		@Override
		public void customize(JettyServletWebServerFactory factory) {
			factory.addServerCustomizers(this::customizeServer);
		}

		private void customizeServer(Server server) {
			RequestLog requestLog = server.getRequestLog();
			if (requestLog instanceof CustomRequestLog customRequestLog) {
				customizeRequestLog(customRequestLog);
			}
		}

		private void customizeRequestLog(CustomRequestLog requestLog) {
			if (requestLog.getWriter() instanceof RequestLogWriter requestLogWriter) {
				customizeRequestLogWriter(requestLogWriter);
			}
		}

		private void customizeRequestLogWriter(RequestLogWriter writer) {
			String filename = writer.getFileName();
			if (StringUtils.hasLength(filename)) {
				File file = new File(filename);
				file = new File(file.getParentFile(), customizePrefix(file.getName()));
				writer.setFilename(file.getPath());
			}
		}

	}

}
