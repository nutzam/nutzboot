package io.nutz.demo.simple;

import org.nutz.boot.NbApp;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import io.nutz.demo.simple.bean.User;

@IocBean(create="init")
public class MainLauncher {
    
    @Inject
    protected Dao dao;
    
    public void init() {
        dao.create(User.class, true);
        dao.insert(new User("apple", 40, "北京"));
        dao.insert(new User("ball", 30, "未知"));
        dao.insert(new User("cat", 50, "温哥华"));
        dao.insert(new User("fox", 51, "纽约"));
        dao.insert(new User("bra", 25, "济南"));
        dao.insert(new User("lina", 50, "深圳"));
        dao.query(User.class, null);
        dao.query(User.class, null);
        dao.query(User.class, null);
    }

    public static void main(String[] args) throws Exception {
        long expect = 869300035495617L;
        String vhave = "0869300035495617";
        String partA = vhave.substring(0, 8);
        String partB = vhave.substring(8);
        System.out.println(partA);
        System.out.println(partB);
        System.out.println(partA + partB);
        int partA_int = Integer.parseInt(partA, 10);
        int partB_int = Integer.parseInt(partB, 10);
        System.out.println(partA_int);
        System.out.println(partB_int);

        System.out.println((partA_int*100000000L) / (1L<<28) );
        System.out.println((partA_int*100000000L) % (1L<<28) );
        
        System.out.println("--------------------");
        System.out.println(869300035495617L >> 32); // 202399
        System.out.println(869300035495617L % (1L << 32)); // 2949752513
    }

}
