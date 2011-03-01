package net.flicken.util.spring;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class TestPreserveBeanTargetClass {
	@Resource ApplicationContext context;
	@Resource MyLogger logger;
	@Resource MyClass instance;
	@Resource Acceptor acceptor;

	@Test public void acceptorInjectedCorrectly()
	{
		assertNotNull("Instance must be injected", acceptor.getInstance()); 
	}
	
	@Test public void loggerCalled()
	{
		instance.sayHello("Brian");
		assertThat(logger.getNames(), hasItem("Brian"));
		assertThat(logger.getCount(), equalTo(1));
	}
	
	/**
	 * Accepts only class instances, not interfaces 
	 */
	public static class Acceptor {
		private MyClass instance;

		public MyClass getInstance() { return instance; }

		public void setInstance(MyClass instance) { this.instance = instance; }
	}

	public static interface MyClassI {
		String getName();
		void sayHello(String name);
	}
	
	public static class MyClass implements MyClassI {
		public MyClass() { }
		
		private String name;
		public String getName() { return this.name; }
		public void setName(String name) { this.name = name; }
		public void sayHello(String name) {
			System.out.println("Saying hello to: " + name + " from " + getName());
		}
	}
	
	public static class MyLogger {
		AtomicInteger count = new AtomicInteger();
		List<String> names = new ArrayList<String>();
		
		public void logHello(String name)
		{
			count.incrementAndGet();
			names.add(name);
			System.out.println("Someone said hello to: " + name);
			System.out.println("From: " + Arrays.toString(new RuntimeException().getStackTrace()));
		}
		
		public int getCount()
		{
			return count.get();
		}

		public List<String> getNames() {
			return names;
		}
	}
}
