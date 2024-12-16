package hello.itemservice.domain.item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Region {
    private String code; // 지역 코드
    private String displayName; // 화면 표시 이름
    private boolean active; // 활성 상태 여부

    // 예를 들어 지역의 활성 상태에 따라 메시지를 반환하는 메서드 추가
    public String getStatusMessage() {
        return active ? "활성화됨" : "비활성화됨";
    }
    public String getFullDisplayName() {
        return displayName + " (" + getStatusMessage() + ")";
    }
}
