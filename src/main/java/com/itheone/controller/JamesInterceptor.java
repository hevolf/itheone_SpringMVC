package com.itheone.controller;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JamesInterceptor implements HandlerInterceptor{
	//在目标方法运行之间执行

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println(Thread.currentThread()+"----preHandle-------------"+request.getRequestURI()+ "==>" + System.currentTimeMillis());
		return true;
	}
	//在目标方法执行之后执行
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println(Thread.currentThread()+"----postHandle-------------" + request.getRequestURI()+ "==>" + System.currentTimeMillis());
	}

	//页面响应之后执行
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println(Thread.currentThread()+"----afterCompletion-------------"+ "==>" + System.currentTimeMillis());
	}

}
