document.addEventListener("DOMContentLoaded", function () {
    const alertBar = document.querySelector('.alert-bar');

    if (alertBar) {
        // 초기 상태에서 숨김 처리된 알림 바를 보이게 함
        alertBar.style.display = "block"; // display 초기화
        alertBar.style.opacity = "1";    // opacity 초기화
        alertBar.style.transform = "translateY(-100%)"; // 화면 위로 시작

        // 알림 바 내려오는 애니메이션
        setTimeout(() => {
            alertBar.style.transform = "translateY(0)";
        }, 50); // 약간의 지연을 주어 애니메이션 적용

        // 5초 후 알림 바 사라지는 애니메이션 (기존 2초에서 연장)
        const autoHideDelay = 5000; // 자동 사라짐 지연 시간 (5초)
        setTimeout(() => {
            hideAlertBar(alertBar);
        }, autoHideDelay);

        // 닫기 버튼 클릭 시 알림 바 제거
        const closeBtn = alertBar.querySelector('.close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                hideAlertBar(alertBar);
            });
        }
    }

    /**
     * 알림 바 숨김 처리 함수
     * @param {HTMLElement} alertElement - 알림 바 요소
     */
    function hideAlertBar(alertElement) {
        alertElement.style.transform = "translateY(-100%)"; // 화면 위로 이동
        alertElement.style.opacity = "0"; // 점차 사라짐
        setTimeout(() => {
            alertElement.style.display = "none"; // DOM에서 제거
        }, 500); // CSS transition 시간과 동기화
    }
});
