package org.nutz.boot.tools;

import static org.nutz.lang.Strings.dup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.nutz.boot.AppContext;
import org.nutz.boot.annotation.PropDoc;
import org.nutz.lang.Strings;

public class PropDocReader {

	protected AppContext appContext;
	protected Map<String, PropDocBean> docs = new HashMap<>();
	
	public PropDocReader() {
	}

	public PropDocReader(AppContext appContext) {
		this.appContext = appContext;
	}
	
	public void load() throws Exception {
		for (Object starter : appContext.getStarters()) {
			addClass(starter.getClass());
		}
	}
	
	public void addClass(Class<?> klass) throws Exception {
		for (Field field : klass.getFields()) {
			if (!field.getName().startsWith("PROP_"))
				continue;
			PropDoc prop = field.getAnnotation(PropDoc.class);
			if (prop == null)
				continue;
			String key = Strings.isBlank(prop.key()) ? (String) field.get(null) : prop.key();
			
			PropDocBean doc = docs.get(key);
			if (doc == null) {
				doc = new PropDocBean();
				doc.key = key;
				doc.defaultValue = prop.defaultValue();
				doc.value = prop.value();
				doc.group = Strings.isBlank(prop.group()) ? doc.key.substring(0, doc.key.indexOf('.')) : prop.group();
				doc.need = prop.need();
				doc.possible = prop.possible();
				doc.users = new ArrayList<>();
				docs.put(key, doc);
			}
			doc.users.add(klass.getName());
		}
	}
	
	// 输出markdown格式
	public String toMarkdown() {
		String fm = "|%-4s|%-40s|%-10s|%-20s|%-10s|%-20s|%40s|\r\n";
		StringBuilder sb = new StringBuilder();
		sb.append(String.format(fm, "id", "key", "required", "Possible Values", "Default", "Description", "starters"));
		sb.append(String.format(fm, dup("-", 4), dup("-", 40), dup("-", 10), dup("-", 20), dup("-", 10), dup("-", 20), dup("-", 40)));
		int index = 0;
		ArrayList<String> keys = new ArrayList<>(docs.keySet());
		Collections.sort(keys);
		for (String key : keys) {
			PropDocBean doc = docs.get(key);
			sb.append(String.format(fm, index, key, doc.need ? "yes" : "no", Strings.join(",", doc.possible), doc.defaultValue, doc.value, Strings.join(",", doc.users)));
			index++;
		}
		return sb.toString().trim();
	}
	
	public Map<String, PropDocBean> getDocs() {
		return docs;
	}
	
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;
	}
}
