package page.admin.admin.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import page.admin.admin.item.domain.*;
import page.admin.admin.item.domain.dto.ItemSaveForm;
import page.admin.admin.item.repository.*;
import page.admin.admin.member.domain.Member;
import page.admin.admin.member.repository.MemberRepository;
import page.admin.common.utils.exception.DataNotFoundException;
import page.admin.common.utils.file.FileStore;
import page.admin.user.member.domain.dto.LoginSessionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewService reviewService;
    @Mock
    private RegionRepository regionRepository;
    @Mock
    private ItemTypeRepository itemTypeRepository;
    @Mock
    private DeliveryCodeRepository deliveryCodeRepository;
    @Mock
    private FileStore fileStore;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private MainCategoryRepository mainCategoryRepository;
    @Mock
    private SubCategoryRepository subCategoryRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private ItemSaveForm itemSaveForm;
    private LoginSessionInfo sellerInfo;

    @BeforeEach
    void setUp() {
        itemSaveForm = new ItemSaveForm();
        itemSaveForm.setItemName("Test Item");
        itemSaveForm.setPurchasePrice(1000);
        itemSaveForm.setSalePrice(1500);
        itemSaveForm.setQuantity(10);
        itemSaveForm.setOpen(true);
        itemSaveForm.setRegionCodes(Set.of("REGION-001"));
        itemSaveForm.setItemType(100L);
        itemSaveForm.setDeliveryCode("DELIVERY-FAST");
        itemSaveForm.setMainCategory(1L);
        itemSaveForm.setSubCategory(2L);
        // **여기가 포인트**: 썸네일 리스트를 비어있더라도 초기화
        itemSaveForm.setThumbnails(new ArrayList<>());

        sellerInfo = new LoginSessionInfo();
        sellerInfo.setUserNo(9999L);
        sellerInfo.setRole("ADMIN");
    }

    @Test
    void saveItem_Success() {
        // given
        // (1) RegionRepository Mock
        Region mockRegion = new Region();
        mockRegion.setCode("REGION-001");
        given(regionRepository.findByCodeIn(anySet()))
                .willReturn(Set.of(mockRegion));

        // (2) ItemTypeRepository Mock
        ItemType mockItemType = new ItemType();
        mockItemType.setId(100L);
        given(itemTypeRepository.findById(100L))
                .willReturn(Optional.of(mockItemType));

        // (3) DeliveryCodeRepository Mock
        DeliveryCode mockDeliveryCode = new DeliveryCode();
        mockDeliveryCode.setCode("DELIVERY-FAST");
        given(deliveryCodeRepository.findByCode("DELIVERY-FAST"))
                .willReturn(Optional.of(mockDeliveryCode));

        // (4) MainCategoryRepository Mock
        MainCategory mockMainCat = new MainCategory();
        mockMainCat.setId(1L);
        given(mainCategoryRepository.findById(1L))
                .willReturn(Optional.of(mockMainCat));

        // (5) SubCategoryRepository Mock
        SubCategory mockSubCat = new SubCategory();
        mockSubCat.setId(2L);
        given(subCategoryRepository.findById(2L))
                .willReturn(Optional.of(mockSubCat));

        // (6) MemberRepository Mock
        Member mockMember = new Member();
        mockMember.setUserNo(9999L);
        given(memberRepository.findById(9999L))
                .willReturn(Optional.of(mockMember));

        // (7) fileStore Mock
        // -> 실제 파일 업로드/삭제는 제외. storeFile() 호출 시 가짜 UploadFile 리턴
        given(fileStore.storeFile(any()))
                .willAnswer(invocation -> {
                    UploadFile uf = new UploadFile();
                    uf.setUploadFileName("test.jpg");
                    uf.setStoreFileName("stored-test.jpg");
                    return uf;
                });

        // (8) itemRepository Mock
        given(itemRepository.save(any(Item.class)))
                .willAnswer(invocation -> {
                    Item item = invocation.getArgument(0);
                    item.setItemId(1234L); // DB에 저장된 것처럼 ID 부여
                    return item;
                });

        // when
        Item savedItem = itemService.saveItem(itemSaveForm, sellerInfo);

        // then
        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getItemId()).isEqualTo(1234L);
        assertThat(savedItem.getItemName()).isEqualTo("Test Item");
        assertThat(savedItem.getMainImage()).isNotNull();
        assertThat(savedItem.getRegions()).hasSize(1);
        // 추가 검증들...

        // verify: repository.save가 1번 호출되었는지
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void saveItem_Fail_InvalidRegion() {
        // given
        given(regionRepository.findByCodeIn(anySet()))
                .willReturn(Set.of()); // 빈 Set 반환

        // when & then
        assertThatThrownBy(() -> itemService.saveItem(itemSaveForm, sellerInfo))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessageContaining("유효한 지역 정보가 없습니다.");
    }
}
