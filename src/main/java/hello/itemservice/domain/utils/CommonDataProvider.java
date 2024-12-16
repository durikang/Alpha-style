package hello.itemservice.domain.utils;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.item.ItemType;
import hello.itemservice.domain.item.PurchaseStatus;
import hello.itemservice.domain.item.Region;

import java.util.ArrayList;
import java.util.List;

public class CommonDataProvider {

    // 배송 상태 데이터 제공
    // 정적 메서드로 상태 목록 제공
    public static List<DeliveryCode> getDeliveryStatuses() {
        return List.of(
                DeliveryCode.DROPOFF,
                DeliveryCode.PICKUP,
                DeliveryCode.SHIPPED,
                DeliveryCode.ARRIVED,
                DeliveryCode.SHIPPING,
                DeliveryCode.COMPLETE
        );
    }

    // 구매 상태 데이터 제공 (List 반환)
    public static List<PurchaseStatus> getPurchaseStatuses() {
        return List.of(
                PurchaseStatus.ORDER,
                PurchaseStatus.COMPLETED,
                PurchaseStatus.CANCELED,
                PurchaseStatus.PURCHASED,
                PurchaseStatus.EXCHANGED,
                PurchaseStatus.RETURNED
        );
    }

    // 지역 데이터 제공
    public static List<Region> getRegions() {
        List<Region> regions = new ArrayList<>();
        regions.add(new Region("SEOUL", "서울", true));
        regions.add(new Region("BUSAN", "부산", true));
        regions.add(new Region("DAEGU", "대구", true));
        regions.add(new Region("GWANGJU", "광주", false)); // 비활성화 예시
        return regions;
    }

    // 아이템 종류 데이터 제공
    public static List<ItemType> getItemTypes() {
        List<ItemType> itemTypes = new ArrayList<>();
        itemTypes.add(new ItemType("BOOK", "도서"));
        itemTypes.add(new ItemType("FOOD", "음식"));
        itemTypes.add(new ItemType("ETC", "기타"));
        return itemTypes;
    }
}
