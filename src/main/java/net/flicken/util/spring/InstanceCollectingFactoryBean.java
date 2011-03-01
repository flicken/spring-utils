package net.flicken.util.spring;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Collects objects of the given type and/or name, returning a newly created
 * object.
 * 
 * Example, which collects all objects in context of type <code>com.example.Plugin</code>:
 * <pre><code>
 * 	&lt;bean id="registryList" class="net.flicken.util.spring.InstanceCollectingFactoryBean"&gt;
 *		&lt;property name="beanClass" value="com.example.Plugin"/&gt;
 * &lt;/bean&gt;
 * </code></pre>
 * 
 * The <code>collectionType</code> property allows changing the type of the collection returned.
 * Valid values include:
 * <ul>
 *   <li><code>Collection</code> subclass
 *   <li><code>Map</code> subclass
 *   <li>Any array class
 * </ul>
 * 
 * @author broberts
 */
public class InstanceCollectingFactoryBean extends BeanFilteringSupport 
		implements FactoryBean, ApplicationContextAware {
	private Class<?> collectionType;
	private ApplicationContext context;
	
	public InstanceCollectingFactoryBean()
	{
		collectionType = ArrayList.class;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getObject() {
		Map<String, ?> beanMap = gatherQualifiedBeans(context);

		Object beans = createCollection(beanMap.size());
		if (beans instanceof Map)
		{
			((Map)beans).putAll(beanMap);
		}
		else if (beans instanceof Collection)
		{
			((Collection)beans).addAll(beanMap.values());
			
		}
		else if (beans.getClass().isArray())
		{
	      System.arraycopy(beanMap.values().toArray(), 0,
	                       beans, 0,
	                       beanMap.size());
		}
	
		
		return beans;
	}

	protected Object createCollection(int size) throws IllegalStateException {
		try {
			if (getCollectionType().isArray())
			{
				return Array.newInstance(getCollectionType().getComponentType(), size);
			}
			return getCollectionType().newInstance();
		} catch (InstantiationException e) {
			return handleCreationException(e);
		} catch (IllegalAccessException e) {
			return handleCreationException(e);
		}
	}

	protected Collection<?> handleCreationException(Exception e) throws IllegalStateException {
		throw new IllegalStateException("Error creating object of type " + getObjectType() + ": " + e.getLocalizedMessage(), e);
	}

	public void setCollectionType(Class<?> collectionType)
	{
		boolean isCollectionType = (ClassUtils.isAssignable(Collection.class, collectionType)
				|| ClassUtils.isAssignable(Map.class, collectionType)
				|| collectionType.isArray());
		Assert.state(isCollectionType, "Expected collection type assignable from Collection, Map, or Array, got: " + collectionType);
		this.collectionType = collectionType;
	}
	
	public Class<?> getObjectType() {
		return getCollectionType();
	}

	public Class<?> getCollectionType() {
		return collectionType;
	}

	public boolean isSingleton() {
		return false;
	}

	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
}
