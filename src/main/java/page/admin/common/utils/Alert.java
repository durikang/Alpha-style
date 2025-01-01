package page.admin.common.utils;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Alert {
    private String title;
    private AlertType type;
    private boolean active;

    // 기본 생성자
    public Alert(String title, AlertType type) {
        this.title = title;
        this.type = type;
        this.active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    // Enum
    public enum AlertType {
        SUCCESS("alert-success"),
        ERROR("alert-error"),
        WARNING("alert-warning");

        private final String cssClass;

        AlertType(String cssClass) {
            this.cssClass = cssClass;
        }

        public String getCssClass() {
            return cssClass;
        }
    }

    // 유효성 검사 메서드 추가
    public static boolean isValid(Alert alert) {
        return alert != null && alert.getTitle() != null && !alert.getTitle().isBlank();
    }
}
