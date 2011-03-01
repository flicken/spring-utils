package net.flicken.util.spring;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
public abstract class AbstractTestBeanFilteringSupport implements ApplicationContextAware { 
	protected static final int PLUGIN_COUNT = 3;
	
	@Autowired ApplicationContext context;
	@Autowired Registry registry;
	@Autowired Plugin1 plugin1;
	@Autowired Plugin2 plugin2;
	@Autowired ExtraPlugin extra;
	
	abstract protected void registerBeans();

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}
	
	protected BeanFilteringSupport getBeanFilterer()
	{
		Map<?,?> filterers = context.getBeansOfType(BeanFilteringSupport.class);
		return (BeanFilteringSupport) filterers.values().iterator().next();
	}
	
	@After public void resetRegistry()
	{
		registry.reset();
	}
	
	@Test public void matchOnPatternMatchSingle()
	{
		assertPatternResults("plugin1", plugin1);
	}

	@Test public void matchOnPatternMatchMultiple()
	{
		assertPatternResults("plugin*", plugin1, plugin2);
	}
	
	@Test public void matchOnPatternMatchAll()
	{
		assertPatternResults("*", plugin1, plugin2, extra);
	}
	
	@Test public void matchOnClassMatchAll()
	{
		assertClassResults(Plugin.class, plugin1, plugin2, extra);
	}

	@Test public void matchOnClassExcludeOne()
	{
		assertClassResults(AbstractExtraPlugin1.class, plugin1, extra);
	}
	
	@Test public void matchOnClassMatchOne()
	{
		assertClassResults(ExtraPlugin.class, extra);
	}
	
	protected void assertClassResults(Class<?> clazz, Plugin... plugins) {
		getBeanFilterer().setBeanClass(clazz);
		getBeanFilterer().setBeanPattern("*");
		registerBeans();
		
		assertThat(registry, hasOnlyPlugins(plugins));
	}

	protected void assertPatternResults(String pattern, Plugin... plugins) {
		getBeanFilterer().setBeanPattern(pattern);
		getBeanFilterer().setBeanClass(Plugin.class);
		registerBeans();

		assertThat(registry, hasOnlyPlugins(plugins));
	}


	protected Matcher<Registry> hasOnlyPlugins(final Plugin... plugins) {
		return new TypeSafeMatcher<Registry>() {
			final Plugin[] matchingPlugins = plugins;
			@Override
			public boolean matchesSafely(Registry registry) {
				System.out.println("Plugins: " + registry.getPlugins());
				System.out.println("Expected: " + Arrays.asList(matchingPlugins));
				assertThat(registry.getPlugins(), hasItems(matchingPlugins));
				assertThat(registry.getPlugins().size(), is(matchingPlugins.length));
				return true;
			}

			public void describeTo(Description description) {
				description.appendValueList("has plugins", ", ", ".", plugins);
			}
		};
	}

	public static class Registry
	{
		List<Plugin> plugins = new ArrayList<Plugin>();

		public void setPlugins(List<Plugin> plugins)
		{
			this.plugins = new ArrayList<Plugin>(plugins);
		}
		
		public void registerPlugin(Plugin plugin) {
			plugins.add(plugin);
		}
		
		public void reset() { 
			this.plugins.clear();
		}
		
		public List<Plugin> getPlugins() {
			return plugins;
		}
	}

	// Marker interface
	public static interface Plugin { }
	
	/*
	 * Class hierarchy:
	 *   Plugin
	 *     |
	 *     +- AbstractPlugin
	 *       |
	 *       +- Plugin2
	 *       |
	 *       +- AbstractExtraPlugin1
	 *          |
	 *          +- Plugin1
	 *          |
	 *          +- ExtraPlugin
	 *       
	 */
	public static abstract class AbstractPlugin implements Plugin {	}
	
	public static abstract class AbstractExtraPlugin1 extends AbstractPlugin {	}
	
	public static class Plugin1 extends AbstractExtraPlugin1 {	}
	
	public static class Plugin2 extends AbstractPlugin {	}
	
	public static class ExtraPlugin extends AbstractExtraPlugin1
	{	}
}
