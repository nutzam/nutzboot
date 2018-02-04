package org.nutz.boot.starter.freemarker;

import freemarker.template.TemplateDirectiveModel;

/**
 * @author 科技㊣²º¹³
 * 2014年1月1日 下午5:42:54
 * http://www.rekoe.com
 * QQ:5382211
 */
public class FreemarkerDirective {
	private String name;
	private TemplateDirectiveModel templateDirectiveModel;
	public FreemarkerDirective(String name, TemplateDirectiveModel templateDirectiveModel) {
		super();
		this.name = name;
		this.templateDirectiveModel = templateDirectiveModel;
	}
	public String getName() {
		return name;
	}
	public TemplateDirectiveModel getTemplateDirectiveModel() {
		return templateDirectiveModel;
	}
}
