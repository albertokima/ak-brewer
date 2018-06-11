package com.viki.brewer.thymeleaf;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import com.viki.brewer.thymeleaf.processor.ClassForErrorAttributeTagProcessor;
import com.viki.brewer.thymeleaf.processor.MenuAttributeTagProcessor;
import com.viki.brewer.thymeleaf.processor.MessageElementTagProcessor;
import com.viki.brewer.thymeleaf.processor.NavigationElementTagProcessor;
import com.viki.brewer.thymeleaf.processor.OrderElementTagProcessor;

@Component
public class BrewerDialect extends AbstractProcessorDialect {

	public BrewerDialect() {
		super("Brewer", "brewer", StandardDialect.PROCESSOR_PRECEDENCE);
	}
	
	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		final Set<IProcessor> processadores = new HashSet<>();
		processadores.add(new ClassForErrorAttributeTagProcessor(dialectPrefix));
		processadores.add(new MessageElementTagProcessor(dialectPrefix));
		processadores.add(new OrderElementTagProcessor(dialectPrefix));
		processadores.add(new NavigationElementTagProcessor(dialectPrefix));
		processadores.add(new MenuAttributeTagProcessor(dialectPrefix));
		
		return processadores;
	}

}