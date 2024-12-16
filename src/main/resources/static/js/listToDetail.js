// 공통적으로 사용하는 클릭 이벤트 스크립트
document.addEventListener("DOMContentLoaded", () => {
    const clickableRows = document.querySelectorAll(".clickable-row");
    clickableRows.forEach(row => {
        row.addEventListener("click", () => {
            const detailsUrl = row.dataset.detailsUrl || ""; // 상세 페이지 URL (data-details-url 속성에서 가져옴)
            const id = row.dataset.id; // 상세 페이지 ID
            if (detailsUrl && id) {
                console.log(`Navigating to: ${detailsUrl + id}`); // 디버깅용 로그
                window.location.href = detailsUrl + id; // 상세 페이지로 이동
            } else {
                console.warn("detailsUrl or id is missing for this row.");
            }
        });
    });
});
