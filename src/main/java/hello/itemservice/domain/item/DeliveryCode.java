package hello.itemservice.domain.item;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 집하		    DROPOFF
 배송중(집고)	    PICKUP
 배송중(출고)	    SHEIPPED
 배달지도착	    ARRIVED
 배달중		    SHIPPING
 배달완료		COMPLETE
 */
@Data
@AllArgsConstructor
public class DeliveryCode {

    private String code;
    private String displayName;

    // 정적 상수 선언
    public static final DeliveryCode DROPOFF = new DeliveryCode("DROPOFF", "집하");
    public static final DeliveryCode PICKUP = new DeliveryCode("PICKUP", "배송중(집고)");
    public static final DeliveryCode SHIPPED = new DeliveryCode("SHIPPED", "배송중(출고)");
    public static final DeliveryCode ARRIVED = new DeliveryCode("ARRIVED", "배달지도착");
    public static final DeliveryCode SHIPPING = new DeliveryCode("SHIPPING", "배달중");
    public static final DeliveryCode COMPLETE = new DeliveryCode("COMPLETE", "배달완료");
}
