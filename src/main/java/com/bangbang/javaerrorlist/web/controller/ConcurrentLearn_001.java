package com.bangbang.javaerrorlist.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author XuBang
 * @version 1.0
 * @date 2023/1/10 22:56
 * @Description 并发工具类 常见异常 实操
 */
@RestController("/concurrentLearn")
public class ConcurrentLearn_001 {
    private static final ThreadLocal<Integer> currentUser = ThreadLocal.withInitial(() -> null);

    /**
     * 类似这样的写法，会产生一个问题：当多个用户进行请求的时候，线程池中线程数一定，就会存在"线程重用"的情况，就会出现：
     * 后一个用户 获取到 前一个用户的信息；进而造成数据混乱。
     * @param userId
     * @return
     */
    @GetMapping("/wrong")
    public Map wrong(@RequestParam("userId") Integer userId) {
        //设置用户信息之前先查询一次ThreadLocal中的用户信息
        String before  = Thread.currentThread().getName() + ":" + currentUser.get();
        //设置用户信息到ThreadLocal
        currentUser.set(userId);
        //设置用户信息之后再查询一次ThreadLocal中的用户信息
        String after  = Thread.currentThread().getName() + ":" + currentUser.get();
        //汇总输出两次查询结果
        Map result = new HashMap();
        result.put("before", before);
        result.put("after", after);
        return result;
    }


    @GetMapping("/right")
    public Map right(@RequestParam("userId") Integer userId){
        Map result;
        try {
            String before = Thread.currentThread().getName()+":"+currentUser.get();
            currentUser.set(userId);
            String after = Thread.currentThread().getName()+":"+currentUser.get();
            result = new HashMap<>();
            result.put("before",before);
            result.put("after",after);
        } finally {
            //在finally中，清空currentUser，保证：重用线程，数据不会混乱。
            currentUser.remove();
        }
        return result;
    }
}