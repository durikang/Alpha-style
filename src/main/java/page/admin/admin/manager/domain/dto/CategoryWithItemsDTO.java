package page.admin.admin.manager.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import page.admin.admin.item.domain.Item;
import page.admin.admin.item.domain.MainCategory;
import page.admin.admin.item.domain.dto.ItemViewForm;

import java.util.List;

@Data
@AllArgsConstructor
public class CategoryWithItemsDTO {
    private MainCategory mainCategory;
    private List<Item> items;


}