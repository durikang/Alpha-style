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
            const id = row.dataset.id;

            if (detailsUrl && id) {
                console.log(`Navigating to: ${detailsUrl}${id}`);
                window.location.href = `${detailsUrl}${id}`;
            } else {
                console.warn("detailsUrl or id is missing for this row.");
            }
        });
    });
});