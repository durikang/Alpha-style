document.addEventListener("DOMContentLoaded", () => {
    const clickableRows = document.querySelectorAll(".clickable-row");

    clickableRows.forEach(row => {
        row.addEventListener("click", (event) => {
            const target = event.target;

            // 클릭이 체크박스나 체크박스를 감싸는 `<td>`인 경우 상세 페이지 이동 방지
            if (
                (target.tagName.toLowerCase() === "input" && target.type === "checkbox") ||
                (target.tagName.toLowerCase() === "td" && target.querySelector("input[type='checkbox']"))
            ) {
                return;
            }

            const detailsUrl = row.dataset.detailsUrl || "";

            if (detailsUrl) {
                console.log(`Navigating to: ${detailsUrl}`);
                window.location.href = detailsUrl; // `itemId`를 다시 붙이지 않음
            } else {
                console.warn("detailsUrl is missing for this row.");
            }
        });
    });
});