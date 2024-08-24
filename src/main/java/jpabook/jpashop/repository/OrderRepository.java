package jpabook.jpashop.repository;

import static jpabook.jpashop.domain.QMember.member;
import static jpabook.jpashop.domain.QOrder.order;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.QMember;
import jpabook.jpashop.domain.QOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

  private final EntityManager em;

  public void save(Order order) {
    em.persist(order);
  }

  public Order findOne(Long id) {
    return em.find(Order.class, id);
  }


  /*
  추후 Querydsl로 리팩토링
   */
  public List<Order> findAll(OrderSearch orderSearch) {
    QOrder order = QOrder.order;
    QMember member = QMember.member;

    JPAQueryFactory query = new JPAQueryFactory(em);
    return query
        .select(order)
        .from(order)
        .join(order.member, member)
        .where(statusEq(orderSearch.getOrderStatus()),
            nameLike(orderSearch.getMemberName()))
        .limit(1000)
        .fetch();
  }
  private BooleanExpression statusEq(OrderStatus statusCond) {
    if (statusCond == null) {
      return null;
    }
    return order.status.eq(statusCond);
  }
  private BooleanExpression nameLike(String nameCond) {
    if (!StringUtils.hasText(nameCond)) {
      return null;
    }
    return member.name.like(nameCond);
  }

  public List<Order> findAllByFetchJoinToOne(int offset, int limit) {
    return em.createQuery(
            "select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d", Order.class)
        .setFirstResult(offset)
        .setMaxResults(limit)
        .getResultList();
  }

  public List<Order> findAllByFetchJoinToMany() {
    return em.createQuery(
            "select distinct o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d" +
                " join fetch o.orderItems oi" +
                " join fetch oi.item i", Order.class)
        .getResultList();
  }
}
