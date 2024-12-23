package page.admin.financial.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FilterValue {
    private int value;
    private String label;
    private boolean selected;
}