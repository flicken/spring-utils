package net.flicken.util.spring;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanRemoverTest {
	public static ThreadLocal<Boolean> REMOVE = new ThreadLocal<Boolean>();
	
	public static boolean shouldRemove() {
		return REMOVE.get();
	}
	
	@Test public void create() {
		REMOVE.set(Boolean.FALSE);
		ClassPathXmlApplicationContext cxt = new ClassPathXmlApplicationContext("BeanRemoverTest-context.xml", BeanRemoverTest.class);
		@SuppressWarnings("unchecked")
		Map<String, Object> beans = (Map<String, Object>)cxt.getBeansOfType(BeanRemoverTest.class);
		assertThat(beans.size(), equalTo(1));
		assertThat(beans.keySet(), Matchers.hasItem("bean-hidden"));
	}

	@Test public void skip() {
		REMOVE.set(Boolean.TRUE);
		ClassPathXmlApplicationContext cxt = new ClassPathXmlApplicationContext("BeanRemoverTest-context.xml", BeanRemoverTest.class);
		@SuppressWarnings({ "unchecked" })
		Map<String, Object> beans = cxt.getBeansOfType(BeanRemoverTest.class);
		assertThat(beans.size(), equalTo(0));
	}
}
