package net.flicken.util.spring;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.util.PatternMatchUtils;

/**
 * Filters beans in a bean factory based on:
 * <ul>
 *   <li><code>beanClass</code> - Class of bean
 *   <li><code>beanPattern(s)</code> - Simple (glob) pattern(s), see {@link PatternMatchUtils} for details on syntax.
 * </ul>
 * 
 * Can be extended or used as a composition object. 
 * 
 * @author broberts
 */
public class BeanFilteringSupport {

	protected Class<?> beanClass;
	protected String[] beanPatterns = new String[] { "*" };

	public BeanFilteringSupport() {
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, ?> gatherQualifiedBeans(ListableBeanFactory beanFactory) {
		
		Class<?> beanClass = getBeanClass();
		if (beanClass == null)
		{
			beanClass = Object.class;
		}
		Map<String,Object> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, beanClass);
		
		Iterator<Entry<String, Object>> it = beans.entrySet().iterator();
		while (it.hasNext())
		{
			Entry<String, ?> entry  = it.next();
			if ((!matchesBeanPattern(entry.getKey())))
			{
				it.remove();
			}
		}
		
		return beans;
	}

	protected boolean matchesBeanPattern(String name) {
		return PatternMatchUtils.simpleMatch(beanPatterns, name);
	}

	/**
	 * (Optional) Name pattern that beans must match in order to be registered.
	 * 
	 * Matches using Springs {@link PatternMatchUtils#simpleMatch(String[], String).
	 * 
	 * @see PatternMatchUtils#simpleMatch(String[], String)
	 * @param beanPatterns
	 */
	public void setBeanPatterns(String[] beanPatterns) {
		this.beanPatterns = beanPatterns;
	}

	/**
	 * Sets bean patterns to a single pattern.
	 * 
	 * @param beanPattern
	 * @see #setBeanPatterns(String[])
	 */
	public void setBeanPattern(String beanPattern) {
		setBeanPatterns(new String[] {beanPattern});
	}

	/**
	 * (Optional) Filter beans and method used on parameter object class.
	 * Defaults to parameter type of found {@link #targetMethod}.
	 *  
	 * @param clazz class which beans must be an instance of
	 */
	public void setBeanClass(Class<?> clazz) {
		this.beanClass = clazz;
	}

	/**
	 * Bean class
	 * @return bean class
	 */
	public Class<?> getBeanClass() {
		return beanClass;
	}

	/**
	 * Bean patterns
	 * @return bean patterns
	 */
	public String[] getBeanPatterns() {
		return beanPatterns;
	}

}
