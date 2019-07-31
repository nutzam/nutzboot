package org.mybatis.example;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.nutz.boot.NbApp;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(create="init")
public class MybatisStarterTest {
    
    @Inject
    protected SqlSessionFactory sqlSessionFactory;
    
    public void init() {
        SqlSession session = sqlSessionFactory.openSession();
        try {
          Blog blog = session.selectOne("org.mybatis.example.BlogMapper.selectBlog", 101);
          System.out.println(blog);
        } finally {
          session.close();
        }
    }

    public static void main(String[] args) {
        new NbApp().run();
    }

}
