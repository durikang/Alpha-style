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

        // 2초 후 알림 바 사라지는 애니메이션
        setTimeout(() => {
            alertBar.style.transform = "translateY(-100%)";
            alertBar.style.opacity = "0"; // 점차 사라짐
        }, 2000);

        // 닫기 버튼 클릭 시 알림 바 제거
        const closeBtn = alertBar.querySelector('.close-btn');
        closeBtn.addEventListener('click', () => {
            alertBar.style.transform = "translateY(-100%)";
            alertBar.style.opacity = "0";
        });
    }
});
