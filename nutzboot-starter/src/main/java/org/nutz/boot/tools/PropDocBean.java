package org.nutz.boot.tools;

import java.util.List;

/**
 * 把注解信息转为Java Pojo表达
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class PropDocBean {

    public String key;
    public String value;
    public String type;
    public String group;
    public String[] possible;
    public String defaultValue;
    public boolean need;
    public List<String> users;
}
