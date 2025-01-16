document.addEventListener('DOMContentLoaded', () => {
    const body = document.body;

    // 페이지 로드 시 페이드인 효과 적용
    body.classList.add('fade-in');

    // 전역 이벤트 위임: a[href] 태그만 처리
    document.addEventListener('click', (event) => {
        const target = event.target.closest('a[href]');
        if (!target) return; // 클릭한 대상이 a[href]가 아니면 무시

        handlePageTransition(event, target, body);
    });
});

// 뒤로가기 및 페이지 캐시 복원 처리
window.addEventListener('pageshow', () => {
    const body = document.body;

    // 캐시 복원 시 페이드아웃 초기화
    body.classList.remove('fade-out');
});

/**
 * 페이지 전환 처리 함수
 * @param {Event} event - 클릭 이벤트
 * @param {HTMLElement} target - 클릭된 링크 (a 태그)
 * @param {HTMLElement} body - body 엘리먼트
 */
function handlePageTransition(event, target, body) {
    const href = target.href;

    // 유효한 링크만 처리
    if (href) {
        event.preventDefault();

        // 페이드아웃 효과 추가
        body.classList.add('fade-out');

        // 0.5초 후 페이지 이동
        setTimeout(() => {
            window.location.href = href;
        }, 500);
    }
}
