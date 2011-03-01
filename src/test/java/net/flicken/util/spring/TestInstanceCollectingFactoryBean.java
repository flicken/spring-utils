package net.flicken.util.spring;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import net.flicken.util.spring.InstanceCollectingFactoryBean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
public class TestInstanceCollectingFactoryBean extends AbstractTestBeanFilteringSupport {
	InstanceCollectingFactoryBean collectingFactoryBean = new InstanceCollectingFactoryBean();
	
	@Test public void testCollectionIsCreated()
	{
		List<?> registryList = (List<?>) context.getBean("registryList");
		assertThat(registryList.size(), equalTo(PLUGIN_COUNT));
	}
	
	@Test public void testCollectionTypeIsCorrect() {
		collectingFactoryBean.setApplicationContext(context);
		collectingFactoryBean.setBeanClass(Plugin.class);

		List<Class<?>> collectionTypes =
			Arrays.<Class<?>>asList(ArrayList.class,
					HashSet.class, HashMap.class, Hashtable.class,
					Object[].class
					);
		for (Class<?> clazz : collectionTypes) {
			System.out.println("Testing type: " + clazz.getSimpleName());
			collectingFactoryBean.setCollectionType(clazz);
			Object plugins = collectingFactoryBean.getObject();
			assertThat(plugins, is(clazz));
			if (plugins instanceof Collection)
			{
				assertThat(((Collection<?>)plugins).size(), equalTo(3));
			}
			else if (plugins instanceof Map)
			{
				assertThat(((Map<?,?>)plugins).size(), equalTo(3));
			}
			else {
				assertThat(((Object[])plugins).length, equalTo(3));
			}
		}
	}
	
	@Override
	protected InstanceCollectingFactoryBean getBeanFilterer() {
		return collectingFactoryBean;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void registerBeans() {
		registry.reset();
		collectingFactoryBean.setApplicationContext(context);
		List<Plugin> plugins = (List<Plugin>) collectingFactoryBean.getObject();
		registry.setPlugins(plugins);
	}
}
