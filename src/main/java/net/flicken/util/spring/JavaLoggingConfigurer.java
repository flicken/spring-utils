/**
 * 
 */
package net.flicken.util.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.Vector;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Allows configuration of Java Logging API from Spring context.
 * 
 * <code><pre>
 *	&lt;bean id="loggingConfiguration" class="net.flicken.util.spring.JavaLoggingConfigurer"&gt;
 *  	&lt;property name="resources"&gt;
 *			&lt;util:list&gt;
 *			&lt;value&gt;classpath*:/logging.properties&lt;/value&gt;
 *			&lt;/util:list&gt;
 *		&lt;/property&gt;
 *	&lt;/bean&gt;
 * </pre></code>
 * 
 * @see #setResources(Resource[])
 * @author broberts
 */
public class JavaLoggingConfigurer implements InitializingBean {
		private Resource[] resources;
		private boolean ignoreResourceNotFound = true;

		protected InputStream getResourcesAsStream() throws SecurityException, IOException {
			// Using Vector for Enumeration, in order to use SequenceInputStream for concatenating streams
			Vector<InputStream> inputStreams = new Vector<InputStream>();
			for (Resource resource : resources) {
				try {
					inputStreams.add(resource.getInputStream());
				} catch (IOException e) {
					if (!isIgnoreResourceNotFound()) {
						log.warning("Cannot load logging configuration from " + resource + ": " + e.getMessage());
						throw e;
					}
				}
			}
			return new SequenceInputStream(inputStreams.elements());
		}

		public void afterPropertiesSet() throws Exception {
			Assert.notNull(resources, "Must set resources");

			if (isIgnoreResourceNotFound()) {
				Assert.isTrue(resources.length > 0, "Must have at least one resource.");
			}
			
			InputStream resourcesInputStream = getResourcesAsStream();
			LogManager.getLogManager().readConfiguration(resourcesInputStream);
		}
		
		public Resource[] getResources() {
			return resources;
		}

		public void setResources(Resource[] resources) {
			this.resources = resources;
		}

		public void setResource(Resource resource) {
			setResources(new Resource[] {resource});
		}

		public boolean isIgnoreResourceNotFound() {
			return ignoreResourceNotFound;
		}

		public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
			this.ignoreResourceNotFound = ignoreResourceNotFound;
		}

		private static Logger log = Logger.getLogger(JavaLoggingConfigurer.class.getName());
	}