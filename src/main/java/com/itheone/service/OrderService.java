package com.itheone.service;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
	public String goBuy(String orderId){
		return "orderId===="+orderId;
	}
}
