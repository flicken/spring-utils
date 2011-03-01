package net.flicken.util.spring;

import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
/**
 * Sets {@link AutoProxyUtils#PRESERVE_TARGET_CLASS_ATTRIBUTE preserveTargetClass} attribute
 * on given bean name(s).
 * 
 * This means that when an AOP-bean is proxied, it is
 * CGLib-enhanced, so that injection based on class instead of 
 * interface is possible.  
 * 
 * @see AutoProxyUtils#PRESERVE_TARGET_CLASS_ATTRIBUTE
 * @author broberts
 */
public class PreserveBeanTargetClass implements BeanFactoryPostProcessor {
	String[] beanNames;
	
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory)
			throws BeansException {
		for (int i = 0; i < beanNames.length; i++) {
			BeanDefinition beanDef = beanFactory.getBeanDefinition(beanNames[i]);
			beanDef.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
		}
	}

	public String[] getBeanNames() {
		return beanNames;
	}

	public void setBeanName(String beanName) {
		this.setBeanNames(new String[] {beanName});
	}

	public void setBeanNames(String[] beanNames) {
		this.beanNames = beanNames;
	}

}
