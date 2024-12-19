package page.admin.item.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import page.admin.item.service.ItemService;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class) // ItemController만 테스트
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ItemService itemService; // Mock 객체 직접 선언

    @BeforeEach
    void setup(WebApplicationContext context) {
        // Mock 객체 생성 및 주입
        this.itemService = Mockito.mock(ItemService.class);
        Mockito.when(itemService.getAllItems()).thenReturn(List.of()); // Mock 동작 설정
    }

    @Test
    void testUpdateItemWithThumbnail() throws Exception {
        // MockMultipartFile로 파일 생성
        MockMultipartFile mainImage = new MockMultipartFile("mainImage", "main.jpg", "image/jpeg", "dummy data".getBytes());
        MockMultipartFile thumbnail = new MockMultipartFile("thumbnails", "thumb.jpg", "image/jpeg", "dummy data".getBytes());

        // When
        mockMvc.perform(MockMvcRequestBuilders.multipart("/product/items/1/edit")
                        .file(mainImage)
                        .file(thumbnail)
                        .param("itemName", "Test Item")
                        .param("price", "1000")
                        .param("quantity", "10")
                        .param("regionCodes", "region1")
                        .param("itemType", "type1")
                        .param("deliveryCode", "delivery1")
                        .param("open", "true")
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().is3xxRedirection()) // 성공 시 리다이렉트
                .andDo(print());
    }
}
