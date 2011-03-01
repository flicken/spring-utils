package net.flicken.util.spring;

import net.flicken.util.spring.RegisteringBeanPostProcessor;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration()
public class TestRegisteringBeanPostProcessor extends AbstractTestBeanFilteringSupport {
	protected void registerBeans() {
		registry.reset();
		getBeanFilterer().registerBeans(context);
	}

	@Override
	protected RegisteringBeanPostProcessor getBeanFilterer() {
		return (RegisteringBeanPostProcessor) super.getBeanFilterer();
	}
}
