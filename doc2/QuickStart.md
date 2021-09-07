## 快速预览一下NB的项目吧

pom.xml

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.nutz</groupId>
            <artifactId>nutzboot-parent</artifactId>
            <version>${nutzboot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
<dependencies>
    <dependency>
        <groupId>org.nutz</groupId>
        <artifactId>nutzboot-starter-jetty</artifactId>
    </dependency>
    <dependency>
        <groupId>org.nutz</groupId>
        <artifactId>nutzboot-starter-nutz-mvc</artifactId>
    </dependency>
</dependencies>
```

src/main/java/io/nutz/demo/simple/MainLauncher.java

```java
package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.*;
import org.nutz.mvc.annotation.*;

@IocBean
public class MainLauncher {

    @Ok("raw")
    @At("/time/now")
    public long now() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) throws Exception {
        new NbApp().run();
    }
}
```

[![asciicast](https://asciinema.org/a/40Brr8ZNsHx1ILfjhJ7zppJ3v.png)](https://asciinema.org/a/40Brr8ZNsHx1ILfjhJ7zppJ3v)

请访问 [https://get.nutz.io](https://get.nutz.io) 获取属于您的基础代码