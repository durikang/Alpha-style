document.addEventListener("DOMContentLoaded", () => {
    const clickableRows = document.querySelectorAll(".clickable-row");
    const checkboxes = document.querySelectorAll(".select-checkbox");

    clickableRows.forEach(row => {
        row.addEventListener("click", (event) => {
            // 클릭 이벤트가 발생한 대상이 체크박스 또는 체크박스가 포함된 td인지 확인
            const target = event.target;

            // 체크박스 또는 체크박스를 감싸고 있는 td인 경우 이벤트 실행 방지
            if (
                (target.tagName.toLowerCase() === "input" && target.type === "checkbox") ||
                target.tagName.toLowerCase() === "td" && target.querySelector("input[type='checkbox']")
            ) {
                return;
            }

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

    // "전체 선택" 체크박스 기능 추가
    const selectAllCheckbox = document.getElementById("selectAll");
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", () => {
            checkboxes.forEach(checkbox => {
                checkbox.checked = selectAllCheckbox.checked;
            });
        });
    }
});
