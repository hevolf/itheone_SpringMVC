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
    //****接口等待 10s （线程1接口返回DefferResult对象，该对象保存在queue中，
    // 另起线程2从queue中获取该对象并设值，设值完毕，线程1返回deferredResult到response）
    @ResponseBody
    @RequestMapping("/createOrder")
    public DeferredResult<Object> createOrder(){
        DeferredResult<Object> deferredResult = new DeferredResult<>((long)10000, "create fail...");

        JamesDeferredQueue.save(deferredResult);

        return deferredResult;
    }

    ////其实相当于我们说的tomcat的线程N，来处理用户请求，并将请求的操作放到Queue队列里
    //客户端请求服务
    //SpringMVC调用Controller，Controller返回一个DeferredResult对象
    //SpringMVC调用ruquest.startAsync
    //DispatcherServlet以及Filters等从应用服务器线程中结束，但Response仍旧是打开状态，也就是说暂时还不返回给客户端
    //某些其它线程将结果设置到DeferredResult中，SpringMVC将请求发送给应用服务器继续处理
    //DispatcherServlet再次被调用并且继续处理DeferredResult中的结果，最终将其返回给客户端

    //给deferredResult设值（设值完，同步至response返回）
    @ResponseBody
    @RequestMapping("/create")
    public void create(){
        //创建订单（按真实操作应该是从订单服务取，这里直接返回）
        String order = UUID.randomUUID().toString();//模拟从订单服务获取的订单信息（免调接口）
        DeferredResult<Object> deferredResult = JamesDeferredQueue.get();
        deferredResult.setResult(order);//给deferredResult设置  设值完毕返回response
        System.out.println("create2 success, orderId == "+order);
        //return "create success, orderId == "+order;
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

