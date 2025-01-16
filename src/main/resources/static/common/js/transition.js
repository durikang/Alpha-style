document.addEventListener('DOMContentLoaded', () => {
    const body = document.body;

    // 페이지 로드 시 페이드인 효과
    body.classList.add('fade-in');

    // 페이지 전환 처리 (로그인 폼, 모달 폼 제외)
    document.querySelectorAll('a[href]:not([data-bs-toggle="modal"]), form:not([data-login-form]):not([data-modal-form])').forEach(element => {
        element.addEventListener('click', (event) => handlePageTransition(event, body));
    });
});

// 뒤로가기 및 페이지 캐시 복원 시 페이드아웃 상태 초기화
window.addEventListener('pageshow', () => {
    const body = document.body;

    // 페이지가 캐시로부터 복원된 경우에도 fade-out 클래스 제거
    if (body.classList.contains('fade-out')) {
        body.classList.remove('fade-out');
    }
});

/**
 * 페이지 전환 처리 함수
 * @param {Event} event - 클릭 이벤트
 * @param {HTMLElement} body - body 엘리먼트
 */
function handlePageTransition(event, body) {
    const target = event.currentTarget;
    const isForm = target.tagName === 'FORM';
    const isAnchor = target.tagName === 'A' && target.href;

    // 로그인 폼, 모달 폼 및 모달 내부 이벤트 제외
    if (target.closest('[data-login-form]') || target.closest('[data-modal-form]') || target.dataset.bsToggle === 'modal') {
        return;
    }

    // 유효한 대상만 처리
    if (isForm || isAnchor) {
        event.preventDefault();

        const href = isForm ? target.action : target.href;

        // 페이드아웃 효과 추가
        body.classList.add('fade-out');

        // 0.5초 후 페이지 이동/폼 제출
        setTimeout(() => {
            if (isForm) {
                target.submit();
            } else {
                window.location.href = href;
            }
        }, 500);
    }
}
