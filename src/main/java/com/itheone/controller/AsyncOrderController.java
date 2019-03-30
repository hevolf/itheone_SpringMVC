package com.itheone.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * @author caohaifengx@163.com 2019-03-29 18:01
 */
@Controller
public class AsyncOrderController {

    //1、DeferredResult方式
    //其实相当于我们说的tomcat的线程1，来处理用户请求，并将请求的操作放到Queue队列里
    @ResponseBody
    @RequestMapping("/createOrder")
    public DeferredResult<Object> createOrder(){
        DeferredResult<Object> deferredResult = new DeferredResult<>((long)10000, "create fail...");

        JamesDeferredQueue.save(deferredResult);

        return deferredResult;
    }

    ////其实相当于我们说的tomcat的线程N，来处理用户请求，并将请求的操作放到Queue队列里
    @ResponseBody
    @RequestMapping("/create")
    public String create(){
        //创建订单（按真实操作应该是从订单服务取，这里直接返回）
        String order = UUID.randomUUID().toString();//模拟从订单服务获取的订单信息（免调接口）
        DeferredResult<Object> deferredResult = JamesDeferredQueue.get();
        deferredResult.setResult(order);//？？？？？？？？？为什么能直接返回order
        return "create success, orderId == "+order;
    }

    //2.spring_mvc  Callable方式异步返回  全程发两次 request 拦截器会拦截两次preHandle
    //a.springmvc 开启异步处理 并在一个隔离线程中将Callable提交给处理器处理
    //b.DispatcherServlet 和所有的 Filter exit Servlet container thread，但respons保持open状态
    //c.Callable处理完后，SpringMVC重发request到Servlet container唤醒原来处理过程
    //d.再次调用DispatcherServlet 并获取Callable中result
    @ResponseBody
    @RequestMapping("/order01")
    public Callable<String> order01(){
        System.out.println("主线程开始..."+Thread.currentThread()+"==>"+System.currentTimeMillis());

        //声明使用Callable完成异步操作
        Callable<String> callable = () -> {
            System.out.println("副线程开始..." + Thread.currentThread() + "==>" + System.currentTimeMillis());
            Thread.sleep(2000);
            System.out.println("副线程开始..." + Thread.currentThread() + "==>" + System.currentTimeMillis());
            return "order buy successful........";
        };

        System.out.println("主线程结束..."+Thread.currentThread()+"==>"+System.currentTimeMillis());
        return callable;
    }

}

