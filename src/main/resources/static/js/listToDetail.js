document.addEventListener("DOMContentLoaded", () => {
    const clickableRows = document.querySelectorAll(".clickable-row");
    const checkboxes = document.querySelectorAll(".select-checkbox");
    const selectAllCheckbox = document.getElementById("selectAll");
    const deleteButton = document.getElementById("deleteSelected");

    /**
     * 행 클릭 시 상세 페이지로 이동
     */
    clickableRows.forEach(row => {
        row.addEventListener("click", (event) => {
            const target = event.target;

            // 클릭 이벤트가 발생한 대상이 체크박스 또는 체크박스를 감싸는 td인지 확인
            if (
                (target.tagName.toLowerCase() === "input" && target.type === "checkbox") ||
                (target.tagName.toLowerCase() === "td" && target.querySelector("input[type='checkbox']"))
            ) {
                return; // 상세 페이지 이동 방지
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

    /**
     * "전체 선택" 체크박스 기능
     */
    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", () => {
            checkboxes.forEach(checkbox => {
                checkbox.checked = selectAllCheckbox.checked;
            });
        });
    }

    /**
     * 선택된 상품 삭제 기능
     */
    if (deleteButton) {
        deleteButton.addEventListener("click", () => {
            // 선택된 체크박스의 ID 수집
            const selectedIds = Array.from(checkboxes)
                .filter(checkbox => checkbox.checked)
                .map(checkbox => checkbox.value);

            if (selectedIds.length === 0) {
                alert("삭제할 항목을 선택해주세요.");
                return;
            }

            if (confirm("선택한 상품을 삭제하시겠습니까?")) {
                console.log("Deleting items:", selectedIds); // 디버깅용 로그

                fetch("/product/items/batch-delete", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(selectedIds),
                })
                    .then(response => {
                        if (response.ok) {
                            alert("선택한 상품이 삭제되었습니다.");
                            window.location.reload(); // 페이지 새로고침
                        } else {
                            console.error("Response error:", response.status);
                            return response.json().then(data => {
                                alert("오류 발생: " + data.message);
                            });
                        }
                    })
                    .catch(error => {
                        console.error("삭제 중 오류 발생:", error);
                        alert("삭제 중 오류가 발생했습니다.");
                    });
            }
        });
    } else {
        console.error("Delete button not found.");
    }
});
