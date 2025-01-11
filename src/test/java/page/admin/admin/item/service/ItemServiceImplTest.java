package page.admin.admin.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import page.admin.admin.item.domain.*;
import page.admin.admin.item.domain.dto.ItemUpdateForm;
import page.admin.admin.item.repository.ItemRepository;
import page.admin.common.utils.file.FileStore;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private FileStore fileStore;

    private Item item;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 테스트용 Item 엔티티 초기화
        item = new Item();
        item.setItemId(1L);
        item.setItemName("테스트 상품");
        item.setPurchasePrice(1000);
        item.setSalePrice(2000);
        item.setQuantity(10);
        item.setOpen(true);

        UploadFile mainImage = new UploadFile("test_main.jpg", "stored_test_main.jpg");
        item.setMainImage(mainImage);

        // 썸네일을 가변 컬렉션으로 초기화
        Set<UploadFile> thumbnails = new HashSet<>(Set.of(
                new UploadFile("thumb1.jpg", "stored_thumb1.jpg"),
                new UploadFile("thumb2.jpg", "stored_thumb2.jpg")
        ));
        item.setThumbnails(thumbnails);

        // 파일 삭제 Mock 설정
        when(fileStore.deleteFile(anyString())).thenReturn(true);
    }



    @Test
    void testUpdateItemWithNewMainImage() {
        MultipartFile newMainImage = mock(MultipartFile.class);
        when(newMainImage.isEmpty()).thenReturn(false);
        when(newMainImage.getOriginalFilename()).thenReturn("new_main.jpg");

        when(fileStore.storeFile(newMainImage)).thenReturn(new UploadFile("new_main.jpg", "stored_new_main.jpg"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        when(fileStore.deleteFile("stored_test_main.jpg")).thenReturn(true);

        ItemUpdateForm form = new ItemUpdateForm();
        form.setItemId(1L);
        form.setItemName("업데이트된 상품");
        form.setPurchasePrice(1500);
        form.setSalePrice(2500);
        form.setQuantity(15);
        form.setOpen(true);
        form.setNewMainImage(newMainImage);

        itemService.updateItem(1L, form);

        assertEquals("업데이트된 상품", item.getItemName());
        assertEquals(1500, item.getPurchasePrice());
        assertEquals(2500, item.getSalePrice());
        assertEquals(15, item.getQuantity());
        assertTrue(item.getOpen());
        assertNotNull(item.getMainImage());
        assertEquals("stored_new_main.jpg", item.getMainImage().getStoreFileName());

        verify(fileStore, times(1)).storeFile(newMainImage);
        verify(fileStore, times(1)).deleteFile("stored_test_main.jpg");
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testUpdateItemWithNewThumbnails() {
        List<MultipartFile> newThumbnails = List.of(
                mock(MultipartFile.class),
                mock(MultipartFile.class)
        );

        when(newThumbnails.get(0).isEmpty()).thenReturn(false);
        when(newThumbnails.get(0).getOriginalFilename()).thenReturn("new_thumb1.jpg");
        when(newThumbnails.get(1).isEmpty()).thenReturn(false);
        when(newThumbnails.get(1).getOriginalFilename()).thenReturn("new_thumb2.jpg");

        when(fileStore.storeFile(newThumbnails.get(0))).thenReturn(new UploadFile("new_thumb1.jpg", "stored_new_thumb1.jpg"));
        when(fileStore.storeFile(newThumbnails.get(1))).thenReturn(new UploadFile("new_thumb2.jpg", "stored_new_thumb2.jpg"));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemUpdateForm form = new ItemUpdateForm();
        form.setItemId(1L);
        form.setItemName("업데이트된 상품");
        form.setPurchasePrice(1500);
        form.setSalePrice(2500);
        form.setQuantity(15);
        form.setOpen(true);
        form.setThumbnails(newThumbnails);

        itemService.updateItem(1L, form);

        assertEquals(2, item.getThumbnails().size());
        assertTrue(item.getThumbnails().stream()
                .anyMatch(t -> t.getStoreFileName().equals("stored_new_thumb1.jpg")));
        assertTrue(item.getThumbnails().stream()
                .anyMatch(t -> t.getStoreFileName().equals("stored_new_thumb2.jpg")));

        verify(fileStore, times(2)).storeFile(any(MultipartFile.class));
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testUpdateItemWithInvalidId() {
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        ItemUpdateForm form = new ItemUpdateForm();
        form.setItemId(999L);
        form.setItemName("업데이트된 상품");
        form.setPurchasePrice(1500);
        form.setSalePrice(2500);
        form.setQuantity(15);
        form.setOpen(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itemService.updateItem(999L, form);
        });

        assertEquals("해당 아이템이 없습니다. ID: 999", exception.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void testUpdateItemWithMissingRequiredField() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ItemUpdateForm form = new ItemUpdateForm();
        form.setItemId(1L);
        form.setPurchasePrice(1500);
        form.setSalePrice(2500);
        form.setQuantity(15);
        form.setOpen(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itemService.updateItem(1L, form);
        });

        assertEquals("필수 필드가 누락되었습니다.", exception.getMessage());
        verify(itemRepository, never()).save(any(Item.class));
    }
}
