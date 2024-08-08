package jpabook.jpashop.service;

import static org.junit.Assert.*;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

  @Autowired
  MemberService memberService;
  @Autowired
  MemberRepository memberRepository;
  @Autowired
  EntityManager em;

  @Test
  public void 회원가입() throws Exception {
    //Given
    Member member = new Member();
    member.setName("kim");

    //When
    Long saveId = memberService.join(member);

    //Then
    assertEquals(member, memberService.findOne(saveId));
  }

  @Test(expected = IllegalStateException.class)
  public void 중복_회원_예외() throws Exception {
    //Given
    Member member1 = new Member();
    member1.setName("kim");

    Member member2 = new Member();
    member2.setName("kim");

    //When
    memberService.join(member1);
    memberService.join(member2);

    //Then
    fail("중복 회원이 존재한다.");
  }
}