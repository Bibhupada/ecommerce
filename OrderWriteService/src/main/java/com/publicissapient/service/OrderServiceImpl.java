package com.publicissapient.service;

import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.couchbase.client.deps.com.fasterxml.jackson.core.JsonProcessingException;
import com.couchbase.client.deps.com.fasterxml.jackson.databind.ObjectMapper;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.publicissapient.DAO.OrderDAO;
import com.publicissapient.DAO.pojo.Order;
import com.publicissapient.DAO.pojo.PaymentDetail;
import com.publicissapient.DAO.pojo.Product;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDAO orderDAO;
	@Autowired
	private RestTemplate restTemplate;

	//@HystrixCommand(fallbackMethod = "errorHandler")
	public String saveOrder(Order orderInfo) {
		
		
		ObjectMapper objectMapper = new ObjectMapper();

		JsonObject orderJson = null;
		JsonDocument customerDoc = null;
		String response = "order created with orderId";
		String responsefromPayment=null;
		try {
			
			boolean checkInventroy = checkInventroy(orderInfo);
			if(checkInventroy) {
			PaymentDetail paymentDetail=new PaymentDetail();
			paymentDetail.setUserId(orderInfo.getUserId());
			paymentDetail.setCardNo(orderInfo.getCardNo());
			
			orderInfo.setOrderId(getGeneratedOrderId());
			PaymentDetail postForObject = restTemplate.postForObject("http://payment-service/paymentInfo/"+orderInfo.getOrderId(), paymentDetail, PaymentDetail.class);
			responsefromPayment=postForObject.getStatus();
			if(responsefromPayment.equals("SUCCESS")) {
				orderInfo.setStatus("confirmed");
				orderJson = JsonObject.fromJson(objectMapper.writeValueAsString(orderInfo));
				customerDoc = JsonDocument.create("order" + "::" + orderJson.get("orderId"), orderJson);
				response = orderDAO.saveOrder(customerDoc);
			}else {
				orderJson = JsonObject.fromJson(objectMapper.writeValueAsString(orderInfo));
				customerDoc = JsonDocument.create("order" + "::" + orderJson.get("orderId"), orderJson);
				response = orderDAO.saveOrder(customerDoc);
			}
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return response+orderInfo.getOrderId();
	}

	private boolean checkInventroy(Order orderInfo) {
		
		Product product = restTemplate.getForObject("http://inventory-service/inventory/"+orderInfo.getItemId(), Product.class);
		if(product.getQuantity() >= Long.parseLong(orderInfo.getQuantity())) {
			product.setQuantity(product.getQuantity()-Long.parseLong(orderInfo.getQuantity()));
			 restTemplate.put("http://inventory-service/inventory/", product);
			 return true;
		}
		return false;
		
		
	}

	private long getGeneratedOrderId() {
		
		return new Date().getTime();
	}

	public String errorHandler(Order orderInfo) {

		return "Service is down . Please try again later.";
	}
}
