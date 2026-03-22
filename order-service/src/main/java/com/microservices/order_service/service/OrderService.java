package com.microservices.order_service.service;

import com.microservices.order_service.dto.InventoryResponse;
import com.microservices.order_service.dto.OrderLineItemsDto;
import com.microservices.order_service.dto.OrderRequest;
import com.microservices.order_service.model.Order;
import com.microservices.order_service.model.OrderLineItems;
import com.microservices.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WebClient webClient;

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .skuCode(orderLineItemsDto.getSkuCode())
                .build();
    }

    public void placeOrder(OrderRequest orderRequest) {
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDto()
                .stream()
                .map(this::mapToDto)
                .toList();

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItems(orderLineItems);

       List<String> skuCodes = order.getOrderLineItems().stream()
               .map(OrderLineItems::getSkuCode)
               .toList();

        //call inventory service and then place order if items are in stock
       InventoryResponse[] inventoryResponseArray = webClient.get()
                                    .uri("http://localhost:8082/api/inventory",
                                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                                    .retrieve()
                                    .bodyToMono(InventoryResponse[].class)
                                    .block();
       boolean allProductsInStock = Arrays.stream(inventoryResponseArray)
               .allMatch(InventoryResponse::isInStock);

       if(allProductsInStock){
           orderRepository.save(order);
       } else {
           throw new IllegalArgumentException("Product not in stock, please try again later");
       }

    }


}
