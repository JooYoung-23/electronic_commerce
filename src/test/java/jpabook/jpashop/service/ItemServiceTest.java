package jpabook.jpashop.service;

import static org.junit.Assert.*;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import org.junit.Test;

public class ItemServiceTest {

  @Test(expected = NotEnoughStockException.class)
  public void 재고_수량_변경() throws Exception {
      //Given
      Item book = new Book();
      book.setStockQuantity(0);

      //When
      book.AddStock(10);
      book.removeStock(11);

      //Then
      fail("재고 수량 부족 예외가 발생한다.");
  }
}