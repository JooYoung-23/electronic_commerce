package jpabook.jpashop.domain;

import static jakarta.persistence.FetchType.*;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_item")
@Getter @Setter
public class OrderItem {

  @Id @GeneratedValue
  @Column(name = "order_item_id")
  private Long id;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "item_id")
  private Item item;

  @ManyToOne(fetch = LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  private int orderPrice;
  private int count;
}
