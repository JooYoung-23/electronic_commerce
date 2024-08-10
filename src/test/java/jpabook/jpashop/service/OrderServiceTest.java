package jpabook.jpashop.service;

import static org.junit.Assert.*;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

  @Autowired
  EntityManager em;
  @Autowired
  OrderService orderService;
  @Autowired
  OrderRepository orderRepository;

  @Test
  public void 상품_주문() throws Exception {
    //Given
    Member member = createMember();

    String name = "JPA";
    int price = 10000;
    int stockQuantity = 10;
    Book book = createBook(name, price, stockQuantity);

    //When
    int orderCount = 2;
    Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

    //Then
    Order getOrder = orderRepository.findOne(orderId);

    assertEquals("상품 주문 시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
    assertEquals("주문한 상품 수 검증", 1, getOrder.getOrderItems().size());
    assertEquals("주문한 상품의 총 가격 검증", price * orderCount, getOrder.getTotalPrice());
    assertEquals("재고 수량 검증", stockQuantity - orderCount, book.getStockQuantity());
  }

  private Book createBook(String name, int price, int stockQuantity) {
    Book book = new Book();
    book.setName(name);
    book.setPrice(price);
    book.setStockQuantity(stockQuantity);
    em.persist(book);
    return book;
  }

  private Member createMember() {
    Member member = new Member();
    member.setName("kim");
    member.setAddress(new Address("서울", "강남", "12345"));
    em.persist(member);
    return member;
  }

  @Test
  public void 주문_취소() throws Exception {
    //Given
    Member member = createMember();

    String name = "JPA";
    int price = 10000;
    int stockQuantity = 10;
    Book book = createBook(name, price, stockQuantity);


    int orderCount = 2;
    Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

    //When
    orderService.cancelOrder(orderId);

    //Then
    Order getOrder = orderRepository.findOne(orderId);

    assertEquals("상품 주문 시 상태는 CANCEL", OrderStatus.CANCEL, getOrder.getStatus());
    assertEquals("재고 수량 검증", stockQuantity, book.getStockQuantity());

  }

  @Test(expected = NotEnoughStockException.class)
  public void 재고_수량_초과_검증() throws Exception {
    //Given
    Member member = createMember();

    String name = "JPA";
    int price = 10000;
    int stockQuantity = 10;
    Book book = createBook(name, price, stockQuantity);

    int orderCount = stockQuantity + 1;
    //When
    orderService.order(member.getId(), book.getId(), orderCount);

    //Then
    fail("재고 수량 부족 예외가 발생해야 한다.");
  }
}