package hello.itemservice;

import hello.itemservice.domain.item.*;
import hello.itemservice.domain.order.Order;
import hello.itemservice.domain.order.OrderDetail;
import hello.itemservice.domain.order.OrderRepository;
import hello.itemservice.domain.users.Member;
import hello.itemservice.domain.users.MemberRepository;
import hello.itemservice.domain.utils.CommonDataProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void init() {
        // 등록 지역 리스트 생성
        List<Region> regionsA = Arrays.asList(
                new Region("SEOUL", "서울", true),
                new Region("BUSAN", "부산", true)
        );

        List<Region> regionsB = Arrays.asList(
                new Region("DAEGU", "대구", true),
                new Region("GWANGJU", "광주", false)
        );

        ItemInit(regionsA, regionsB);
        memberInit(); // 사용자 초기 데이터 설정
        orderInit();  // 주문 초기 데이터 설정
    }

    private void ItemInit(List<Region> regionsA, List<Region> regionsB) {
        // 첫 번째 상품
        Item itemA = new Item("itemA", 10000L, 10);
        itemA.setItemType(new ItemType("BOOK", "도서")); // 클래스 기반 ItemType
        itemA.setDeliveryCode("DROPOFF"); // 배송 상태 설정
        itemA.setRegions(regionsA); // 등록 지역 설정
        itemA.setOpen(true); // 판매 여부 설정
        itemRepository.save(itemA);

        // 두 번째 상품
        Item itemB = new Item("itemB", 20000L, 20);
        itemB.setItemType(new ItemType("FOOD", "음식")); // 클래스 기반 ItemType
        itemB.setDeliveryCode("COMPLETE"); // 배송 상태 설정
        itemB.setRegions(regionsB); // 등록 지역 설정
        itemB.setOpen(false); // 판매 여부 설정
        itemRepository.save(itemB);
    }

    private void memberInit() {
        // 첫 번째 사용자
        Member memberA = new Member();
        memberA.setUserId("userA");
        memberA.setUsername("홍길동");
        memberA.setPassword("password123");
        memberA.setEmail("userA@example.com");
        memberA.setMobilePhone("010-1234-5678");
        memberA.setAddress("서울특별시 강남구");
        memberA.setZipCode("12345");
        memberA.setSecondaryAddress("아파트 101호");
        memberA.setBirthDate(LocalDate.of(1990, 1, 1));
        memberA.setSecurityQuestion("가장 좋아하는 색은?");
        memberA.setSecurityAnswer("파랑");
        memberA.setRole("customer");
        memberRepository.save(memberA);

        // 두 번째 사용자
        Member memberB = new Member();
        memberB.setUserId("system");
        memberB.setUsername("김철수");
        memberB.setPassword("1234");
        memberB.setEmail("userB@example.com");
        memberB.setMobilePhone("010-9876-5432");
        memberB.setAddress("부산광역시 해운대구");
        memberB.setZipCode("67890");
        memberB.setSecondaryAddress("오피스텔 202호");
        memberB.setBirthDate(LocalDate.of(1985, 5, 15));
        memberB.setSecurityQuestion("가장 좋아하는 음식은?");
        memberB.setSecurityAnswer("스테이크");
        memberB.setRole("admin");
        memberRepository.save(memberB);
    }

    private void orderInit() {
        // 첫 번째 사용자 (홍길동)
        Member userA = memberRepository.findById(1L).orElseThrow();

        // 첫 번째 주문 (userA)
        Order order1 = new Order();
        order1.setUserNo(userA.getUserNo());
        order1.setOrderDate(new Date());
        order1.setDelivaryStatus(DeliveryCode.SHIPPING.getCode()); // 코드로 저장
        order1.setDetails(new ArrayList<>());

        Item itemA = itemRepository.findById(1L);
        Item itemB = itemRepository.findById(2L);

        // 주문 상세 데이터 추가
        order1.getDetails().add(createOrderDetail(itemA, 2));
        order1.getDetails().add(createOrderDetail(itemB, 1));

        // 총 금액 계산
        calculateTotalAmount(order1);

        orderRepository.saveOrder(order1);

        // 두 번째 사용자 (김철수)
        Member userB = memberRepository.findById(2L).orElseThrow();

        // 두 번째 주문 (userB)
        Order order2 = new Order();
        order2.setUserNo(userB.getUserNo());
        order2.setOrderDate(new Date());
        order2.setDelivaryStatus(DeliveryCode.COMPLETE.getCode()); // 코드로 저장
        order2.setDetails(new ArrayList<>());

        // 주문 상세 데이터 추가
        order2.getDetails().add(createOrderDetail(itemA, 1));
        order2.getDetails().add(createOrderDetail(itemB, 2));

        // 총 금액 계산
        calculateTotalAmount(order2);

        orderRepository.saveOrder(order2);
    }

    // 주문 상세 생성 헬퍼 메서드
    private OrderDetail createOrderDetail(Item item, int quantity) {
        OrderDetail detail = new OrderDetail();
        detail.setProductNo(item.getId());
        detail.setItem(item);
        detail.setQuantity(quantity);
        detail.setSubtotal(item.getPrice() * quantity);
        return detail;
    }

    // 총 금액 계산 헬퍼 메서드
    private void calculateTotalAmount(Order order) {
        double totalAmount = order.getDetails().stream()
                .mapToDouble(OrderDetail::getSubtotal)
                .sum();
        order.setTotalAmount(totalAmount);
    }





}
