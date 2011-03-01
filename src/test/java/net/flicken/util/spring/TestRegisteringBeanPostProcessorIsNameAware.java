package net.flicken.util.spring;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.flicken.util.spring.RegisteringBeanPostProcessor;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration()
public class TestRegisteringBeanPostProcessorIsNameAware 
extends TestRegisteringBeanPostProcessor 
{
	@Test public void testRegex()
	{
		Pattern p = Pattern.compile(RegisteringBeanPostProcessor.BEAN_METHOD_SUFFIX_PATTERN_STRING);
		Matcher matcher = p.matcher("bean.method-suffix");
		assertThat(matcher.matches(), equalTo(true));
		assertThat(matcher.group(1), equalTo("bean"));
		assertThat(matcher.group(2), equalTo("method"));
		assertThat(matcher.group(3), equalTo("-suffix"));
	}
	
	@Test public void testNameWasParsed() {
		RegisteringBeanPostProcessor rb = (RegisteringBeanPostProcessor) context.getBean("registry.registerPlugin");
		assertThat(rb.getMethodName(), equalTo("registerPlugin"));
		assertThat(rb.getTarget(),     equalTo(context.getBean("registry")));
	}
	// Note: Additional tests in super classes, which ensure name-aware-ness works as expected. 
}
