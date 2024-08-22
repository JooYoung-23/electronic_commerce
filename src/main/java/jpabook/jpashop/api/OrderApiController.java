package jpabook.jpashop.api;

import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

  private final OrderRepository orderRepository;
  private final OrderQueryRepository orderQueryRepository;

  @GetMapping("/api/v1/orders")
  public List<Order> ordersV1() {
    List<Order> all = orderRepository.findAll(new OrderSearch());
    for (Order order : all) {
      order.getMember().getName();
      order.getDelivery().getAddress();
      List<OrderItem> orderItems = order.getOrderItems();
      orderItems.stream().forEach(o -> o.getItem().getName());
    }
    return all;
  }

  @GetMapping("/api/v2/orders")
  public List<OrderDto> ordersV2() {
    List<OrderDto> orders = orderRepository.findAll(new OrderSearch()).stream()
        .map(o -> new OrderDto(o))
        .collect(toList());
    return orders;
  }

  @GetMapping("/api/v3/orders")
  public List<OrderDto> ordersV3() {
    List<OrderDto> orders = orderRepository.findAllByFetchJoinToMany().stream()
        .map(o -> new OrderDto(o))
        .collect(toList());
    return orders;
  }

  @GetMapping("/api/v3.1/orders")
  public List<OrderDto> ordersV3Paging(
      @RequestParam(value = "offset", defaultValue = "0") int offset,
      @RequestParam(value = "limit", defaultValue = "100") int limit) {
    List<OrderDto> orderDtos = orderRepository.findAllByFetchJoinToOne(offset, limit).stream()
        .map(o -> new OrderDto(o))
        .collect(toList());
    return orderDtos;
  }

  @GetMapping("/api/v4/orders")
  public List<OrderQueryDto> ordersV4() {
    return orderQueryRepository.findAllToDto();
  }

  @Data
  static class OrderDto {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate; //주문시간
    private OrderStatus orderStatus;
    private Address address;
    private List<OrderItemDto> orderItems;

    public OrderDto(Order order) {
      this.orderId = order.getId();
      this.name = order.getMember().getName();
      this.orderDate = order.getOrderDate();
      this.orderStatus = order.getStatus();
      this.address = order.getDelivery().getAddress();
      this.orderItems = order.getOrderItems()
          .stream()
          .map(orderItem -> new OrderItemDto(orderItem))
          .collect(toList());
    }
  }

  @Data
  static class OrderItemDto {

    private String itemName;//상품 명
    private int orderPrice; //주문 가격
    private int count; //주문 수량
    public OrderItemDto(OrderItem orderItem) {
      itemName = orderItem.getItem().getName();
      orderPrice = orderItem.getOrderPrice();
      count = orderItem.getCount();
    }
  }
}
