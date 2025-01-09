package page.admin.admin.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import page.admin.admin.item.domain.Item;
import page.admin.admin.item.domain.UploadFile;
import page.admin.admin.item.domain.Region;
import page.admin.admin.item.domain.ItemType;
import page.admin.admin.item.domain.DeliveryCode;
import page.admin.admin.item.domain.MainCategory;
import page.admin.admin.item.domain.SubCategory;
import page.admin.admin.member.domain.Member;
import page.admin.admin.item.repository.ItemRepository;
import page.admin.admin.member.repository.MemberRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    // 필요한 다른 리포지토리들 주입
    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private ItemTypeRepository itemTypeRepository;

    @Autowired
    private DeliveryCodeRepository deliveryCodeRepository;

    @Autowired
    private MainCategoryRepository mainCategoryRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Long testItemId;

    @BeforeEach
    public void setUp() {
        // 지역 설정
        Region region = new Region();
        region.setCode("SEOUL");
        region.setDisplayName("서울");
        region.setActive(true);
        regionRepository.save(region);
        Set<Region> regions = new HashSet<>();
        regions.add(region);

        // 아이템 타입 설정
        ItemType itemType = new ItemType();
        itemType.setCode("ELEC");
        itemType.setDescription("전자제품");
        itemTypeRepository.save(itemType);

        // 배송 코드 설정
        DeliveryCode deliveryCode = new DeliveryCode();
        deliveryCode.setCode("STANDARD");
        deliveryCode.setDisplayName("일반 배송");
        deliveryCodeRepository.save(deliveryCode);

        // 메인 카테고리 설정
        MainCategory mainCategory = new MainCategory();
        mainCategory.setMainCategoryName("가전");
        mainCategoryRepository.save(mainCategory);

        // 서브 카테고리 설정
        SubCategory subCategory = new SubCategory();
        subCategory.setSubCategoryName("노트북");
        subCategory.setMainCategory(mainCategory);
        subCategoryRepository.save(subCategory);

        // 판매자 설정
        Member seller = new Member();
        seller.setUserId("seller123");
        seller.setUsername("판매자");
        seller.setEmail("seller@example.com");
        seller.setPassword("password");
        seller.setRole("SELLER");
        seller.setGender("M"); // 또는 "F" 등 적절한 값 설정
        seller.setAddress("서울특별시 강남구"); // address 필드 설정 추가
        memberRepository.save(seller);

        // 메인 이미지 설정
        UploadFile mainImage = new UploadFile("main.jpg", "main_store.jpg");

        // 썸네일 설정
        UploadFile thumbnail1 = new UploadFile("thumb1.jpg", "thumb1_store.jpg");
        UploadFile thumbnail2 = new UploadFile("thumb2.jpg", "thumb2_store.jpg");
        Set<UploadFile> thumbnails = new HashSet<>();
        thumbnails.add(thumbnail1);
        thumbnails.add(thumbnail2);

        // 아이템 생성 및 저장
        Item item = new Item(
                "테스트 상품",
                100000,
                150000,
                50,
                true,
                regions,
                itemType,
                deliveryCode,
                mainImage,
                thumbnails,
                mainCategory,
                subCategory,
                seller
        );

        Item savedItem = itemRepository.save(item);
        testItemId = savedItem.getItemId();
    }


    @Test
    public void testFindByIdWithDetails() {
        Optional<Item> optionalItem = itemRepository.findByIdWithDetails(testItemId);
        assertTrue(optionalItem.isPresent(), "Item should be present");
        Item item = optionalItem.get();
        assertNotNull(item.getThumbnails(), "Thumbnails should not be null");
        assertFalse(item.getThumbnails().isEmpty(), "Thumbnails should not be empty");
        item.getThumbnails().forEach(thumbnail -> {
            assertNotNull(thumbnail.getStoreFileName(), "StoreFileName should not be null");
            assertNotNull(thumbnail.getItem(), "Thumbnail's item should not be null");
        });
    }
}
