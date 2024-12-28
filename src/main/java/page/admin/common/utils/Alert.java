package page.admin.common.utils;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Alert {
    private String title;
    private AlertType type; // Enum을 사용하여 타입 제한
    private boolean active; // 알림 활성화 여부

    public Alert(String title, AlertType type) {
        this.title = title;
        this.type = type;
        this.active = true; // 기본값 활성화
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    // Alert의 유형을 제한하는 Enum
    public enum AlertType {
        SUCCESS, ERROR,WARNING
    }
}
