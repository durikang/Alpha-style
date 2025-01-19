/* productSalesPagination.js */

/**
 * 페이지 이동 처리 함수
 * @param {number} pageIndex - 이동할 페이지 번호 (0-based index)
 */
function goPage(pageIndex) {
    const page = parseInt(pageIndex, 10); // 문자열을 숫자로 변환
    if (isNaN(page) || page < 0) {
        console.error("Invalid page index:", pageIndex);
        return;
    }

    const form = document.getElementById('searchForm');
    if (!form) {
        console.error("searchForm not found!");
        return;
    }

    // 기존 page input 제거
    const oldPageInput = form.querySelector('input[name="page"]');
    if (oldPageInput) {
        oldPageInput.remove();
    }

    // 새로운 hidden input 추가
    const pageInput = document.createElement('input');
    pageInput.type = 'hidden';
    pageInput.name = 'page';
    pageInput.value = page;
    form.appendChild(pageInput);

    // 폼 제출
    form.submit();
}
