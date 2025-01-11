document.addEventListener('DOMContentLoaded', () => {
    const mainCategoryElement = document.querySelector("#mainCategory");
    const baseUrl = window.SUBCATEGORIES_URL; // 글로벌 변수 사용

    if (mainCategoryElement) {
        mainCategoryElement.addEventListener('change', function () {
            const mainCategoryId = this.value;
            if (!mainCategoryId) return;

            const url = `${baseUrl}${mainCategoryId}`; // URL 생성

            fetch(url)
                .then(response => {
                    if (!response.ok) throw new Error('Failed to load subcategories');
                    return response.json();
                })
                .then(subCategories => {
                    const subCategorySelect = document.getElementById('subCategory');
                    if (!subCategorySelect) {
                        console.warn('Subcategory select element not found.');
                        return;
                    }

                    // 기존 옵션 초기화
                    subCategorySelect.innerHTML = '<option value="">== 서브 카테고리 선택 ==</option>';

                    // 새로운 옵션 추가
                    subCategories.forEach(subCategory => {
                        const option = document.createElement('option');
                        option.value = subCategory.id;
                        option.textContent = subCategory.name;
                        subCategorySelect.appendChild(option);
                    });
                })
                .catch(error => {
                    console.error('Error loading subcategories:', error);
                    alert('서브 카테고리를 불러오는 중 오류가 발생했습니다. 다시 시도해주세요.');
                });
        });
    } else {
        console.warn('Main category element not found.');
    }
});
