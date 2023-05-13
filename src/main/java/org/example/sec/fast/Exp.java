package org.example.sec.fast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;

public class Exp {
    static User user = new User("wq", 18);
    @Test
    public void smp1() {
        //要有无参构造函数
        String s = JSON.toJSONString(user);
        User u = JSON.parseObject(s, User.class);
        System.out.println(u);
        String s2 = JSON.toJSONString(user, SerializerFeature.WriteClassName);
        System.out.println(s2);
//JSONObject类型
        System.out.println(JSON.parse(s));
        //User类型
        System.out.println(JSON.parse(s2));
        String s3 = "{\"@type\":\"org.example.sec.fast.User\",\"age\":18,\"name\":\"wq\",\"nick\":\"wq\"}";
        u = JSON.parseObject(s3, User.class);
        System.out.println(u);
        s3 = "{\"@type\":\"org.example.sec.fast.User\",\"xx\":18,\"xx\":\"wq\",\"nick\":\"wq\"}";
        u = JSON.parseObject(s3, User.class);
        System.out.println(u);
        s3 = "{\"@type\":\"org.example.sec.fast.User\",\"xx\":18}";
        u = JSON.parseObject(s3, User.class);
        //重点就在于其会自动执行getter/setter，简单的来解释下原理就是通过反射调用get方法获取值
        System.out.println(u);
    }
}
