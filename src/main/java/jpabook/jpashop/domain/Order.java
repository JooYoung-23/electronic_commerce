package jpabook.jpashop.domain;

import static jakarta.persistence.FetchType.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

  @Id
  @GeneratedValue
  @Column(name = "order_id")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderItem> orderItems = new ArrayList<>();

  private LocalDateTime orderDate;

  @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "delivery_id")
  private Delivery delivery;

  @Enumerated(EnumType.STRING)
  private OrderStatus status;  //주문상태 [ORDER, CANCEL]
}
