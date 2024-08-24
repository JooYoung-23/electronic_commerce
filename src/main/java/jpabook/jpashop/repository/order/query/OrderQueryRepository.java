package jpabook.jpashop.repository.order.query;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

  private final EntityManager em;

  public List<OrderQueryDto> findAllToDto() {

    List<OrderQueryDto> result = findOrders();
    result.forEach(o -> {
      List<OrderItemQueryDto> orderItems = findOrderItems(o.getOrderId());
      o.setOrderItems(orderItems);
    });
    return result;
  }

  public List<OrderQueryDto> findAllToDtoOpt() {

    List<OrderQueryDto> result = findOrders();
    List<Long> orderIds = result.stream().map(o -> o.getOrderId()).collect(Collectors.toList());

    /*
     * Order의 길이가 1000이상이면 DB에서 In-Query에 대한 오류가 발생할 것인데, 어떻게 해결할까?
     * Batch Size 옵션과 유사하게 100 ~ 1000건 사이로 조회하도록 로직을 작성해야 한다고 생각한다.
     * _240824
     * */
    List<OrderItemQueryDto> orderItems = em.createQuery(
            "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id in :orderIds", OrderItemQueryDto.class)
        .setParameter("orderIds", orderIds)
        .getResultList();

    Map<Long, List<OrderItemQueryDto>> orderItemMap = orderItems.stream()
        .collect(Collectors.groupingBy(orderItemQueryDto -> orderItemQueryDto.getOrderId()));

    result.forEach(o -> o.setOrderItems(orderItemMap.get(o.getOrderId())));

    return result;
  }

  public List<OrderQueryFlatDto> findAllToOptFlat() {

    return em.createQuery(
            "select new jpabook.jpashop.repository.order.query.OrderQueryFlatDto(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count)"
                +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d" +
                " join o.orderItems oi" +
                " join oi.item i", OrderQueryFlatDto.class)
        .getResultList();
  }

  private List<OrderItemQueryDto> findOrderItems(Long orderId) {
    return em.createQuery(
            "select new jpabook.jpashop.repository.order.query.OrderItemQueryDto(oi.order.id, i.name, oi.orderPrice, oi.count)"
                +
                " from OrderItem oi" +
                " join oi.item i" +
                " where oi.order.id = :orderId", OrderItemQueryDto.class)
        .setParameter("orderId", orderId)
        .getResultList();
  }

  private List<OrderQueryDto> findOrders() {
    return em.createQuery(
            "select new jpabook.jpashop.repository.order.query.OrderQueryDto(o.id, m.name, o.orderDate, o.status, d.address)"
                +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderQueryDto.class)
        .getResultList();
  }
}
