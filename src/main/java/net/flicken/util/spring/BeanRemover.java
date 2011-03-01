package net.flicken.util.spring;

import java.util.logging.Logger;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

/**
 * Removes beans from a Spring application context, based on {@link #when} property.
 * 
 * Typical use:
 * 
 * <code><pre>
 *  &lt;bean id="optionallyHiddenBean" class="MyClass"/&gt;
 *
 *  &lt;bean class="net.flicken.util.spring.BeanRemover"&gt;
 *		&lt;property name="beanPattern" value="optionallyHiddenBean"/&gt;
 *		&lt;property name="when"&gt;
 *			&lt;bean class="MyUtil" factory-method="shouldRemove"&gt;&lt;/bean&gt;
 *		&lt;/property&gt;
 *  &lt;/bean&gt;
 * </pre></code>
 * 
 * For more information about the {@link #setBeanPattern(String) bean patterns}, see {@link BeanFilteringSupport}.
 * 
 * @see BeanFilteringSupport
 * @author broberts
 */
public class BeanRemover extends BeanFilteringSupport implements BeanFactoryPostProcessor {
	private boolean when = false;
	
	public void setWhen(boolean shouldInstantiate) {
		this.when = shouldInstantiate;
	}
	
	public boolean isWhen() {
		return when;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
			throws BeansException {
		if (isWhen()) {
			if (beanFactory instanceof BeanDefinitionRegistry) {
				BeanDefinitionRegistry beanReg = (BeanDefinitionRegistry) beanFactory;
				for (String beanName : gatherBeanNames(beanFactory)) {
					if (matchesBeanPattern(beanName)) {
						log.info("Removing matching bean definition '"+beanName+"'.");
						beanReg.removeBeanDefinition(beanName);
					}
				}
			}
		}
	}

	/**
	 * Filter on beanClass if set.
	 */
	private String[] gatherBeanNames(ConfigurableListableBeanFactory beanFactory) {
		Class<?> beanClass = getBeanClass();
		if (beanClass == null) {
			return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, beanClass);
		} else {
			return beanFactory.getBeanDefinitionNames();
		}
	}
	
	private static Logger log = Logger.getLogger(BeanRemover.class.getName());
}
