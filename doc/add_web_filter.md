# 添加Web过滤器

## 可选的方式

* 通过 web.xml 添加, 不推荐. 你没看错,web.xml依然可用,位于 src/main/resources/webapp/WEB-INF/web.xml,请注意路径
* 通过WebFilterFace注册,推荐.

通过WebFilterFace注册有什么好处:

* 方便注入需要的service或其他ioc对象
* 控制初始化过程,方便调整filter参数

本文提及的方法,可以应用到WebServletFace接口,道理是一样的,接口方法是类似的

## 通过 WebFilterFace注册

示例代码A, 只实现WebFilterFace

```java
@IocBean // 必须声明成一个Ioc对象
public class MyAbcFilter implements WebFilterFace {

	@Inject
	PropertiesProxy conf; // 可以注入任意你需要的对象

	public String getName() {
		return "myAbcFilter"; // 这个名字必须全局唯一
	}

	public String getPathSpec() {
		return "/*"; // 通常是/*, 按需要写
	}
	
	public EnumSet<DispatcherType> getDispatches() {
		// 包含哪些请求呢? 通常是request和forward,请查阅DispatcherType类获取全部类型
		EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD);
	}
	
	public Filter getFilter() {
		// 最重要的一步了
		return new XXXFilter(); // 返回你需要注册的那个Filter实例
		// 并非强制要你new一个Filter,该filter从ioc注入,从静态变量获取,都没有问题的
	}
	
	public Map<String, String> getInitParameters() {
		// 如果你的Filter实例需要配置一些参数,这里填一下,否则返回个null就行
		Map<String, String> params = new HashMap<String, String>();
		params.put("abc", "123");
		return params;
	}
	
	public int getOrder() {
		// 数字越小, 放在的位置越靠前
		return 0;
	}
}
```

示例代码B, 继承原Filter并实现WebFilterFace

```java
@IocBean // 必须声明成一个Ioc对象
public class MyAbcFilter extends implements WebFilterFace {
	// 与上一个示例的区别在于getFilter这一步
	public Filter getFilter() {
		return this; // 返回自身
	}
}
```

示例代码C, 使用WebFilterReg, 这是2.3.6新增的类

```java
@IocBean // 以ioc bean注册ioc bean
public class MyFilters {

	public WebFilterFace createMyAbcFilter() {
		WebFilterReg reg = new WebFilterReg();
		reg.setName("abcFilter");
		reg.setFilter(new MyAbcFilter());
		reg.setPathSpecs(new String[]{"/*"});
		// 或者简写为 WebFilterReg reg = new WebFilterReg("abcFilter", new MyAbcFilter(), "/*");
		req.setOrder(1);
		return reg;
	}
}
```

等价的web.xm配置

```xml
			<filter>
				<filter-name>abcFilter</filter-name>
				<filter-class>xxx.yyy.zzz.MyAbcFilter</filter-class>
				<init-param>
					<param-name>abc</param-name>
					<param-value>123</param-value>
				</init-param>
			</filter>

			<filter-mapping>
				<filter-name>abcFilter</filter-name>
				<url-pattern>/*</url-pattern>
				<dispatcher>REQUEST</dispatcher>
				<dispatcher>FORWARD</dispatcher>
			</filter-mapping>
```