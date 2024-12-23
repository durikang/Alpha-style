package page.admin.financial.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FilterOption {
    private String label;
    private String name;
    private List<FilterValue> options;
}


