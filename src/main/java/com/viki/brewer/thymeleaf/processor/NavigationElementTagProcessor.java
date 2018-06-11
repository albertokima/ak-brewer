package com.viki.brewer.thymeleaf.processor;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.templatemode.TemplateMode;

public class NavigationElementTagProcessor extends AbstractElementTagProcessor{

	private static final String NOME_TAG = "navigation";
	private static final int PRECEDENCIA = 1000;
	
	public NavigationElementTagProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, dialectPrefix, NOME_TAG, true, null, false, PRECEDENCIA);
	}
	
	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			IElementTagStructureHandler structureHandler) {
		IModelFactory modelFactory = context.getModelFactory();
		
		IAttribute page  = tag.getAttribute("page");
		IAttribute type = tag.getAttribute("type");
		
		IModel model = modelFactory.createModel();
		if (type.getValue().equals("pager")) {
			model.add(modelFactory.createStandaloneElementTag(
					"th:block", "th:replace", 
					String.format("fragments/Navegacao :: pager (%s)", page.getValue()))
			);
		} else if (type.getValue().equals("default")){
			model.add(modelFactory.createStandaloneElementTag(
					"th:block", "th:replace", 
					String.format("fragments/Navegacao :: default (%s)", page.getValue()))
			);
		} else if (type.getValue().equals("pagination")){
			model.add(modelFactory.createStandaloneElementTag(
					"th:block", "th:replace", 
					String.format("fragments/Navegacao :: pagination (%s)", page.getValue()))
			);
		}
		
		structureHandler.replaceWith(model, true);
	}

}
