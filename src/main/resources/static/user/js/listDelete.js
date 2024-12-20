document.addEventListener("DOMContentLoaded", () => {
    const deleteButton = document.getElementById("deleteSelected");
    const selectAllCheckbox = document.getElementById("selectAll");
    const checkboxes = document.querySelectorAll(".select-checkbox");

    // "전체 선택" 체크박스 클릭 이벤트
    selectAllCheckbox.addEventListener("change", () => {
        const isChecked = selectAllCheckbox.checked;
        checkboxes.forEach(checkbox => {
            checkbox.checked = isChecked;
        });
    });

    // 삭제 버튼 클릭 이벤트
    deleteButton.addEventListener("click", () => {
        // 선택된 체크박스들 가져오기
        const selectedIds = Array.from(checkboxes)
            .filter(checkbox => checkbox.checked)
            .map(checkbox => checkbox.value);

        // 선택된 회원이 없을 경우 경고 메시지
        if (selectedIds.length === 0) {
            alertUser("삭제할 회원을 선택해주세요.", "error");
            return;
        }

        // 삭제 확인 메시지
        const confirmation = confirm("선택한 회원들을 삭제하시겠습니까?");
        if (!confirmation) return;

        // AJAX 요청으로 삭제 API 호출
        fetch("/user/users/batch-delete", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(selectedIds), // 수정된 부분
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(data => {
                        throw new Error(data.title || "회원 삭제 중 오류가 발생했습니다.");
                    });
                }
                return response.json(); // 성공 시 Alert 객체 반환
            })
            .then(alert => {
                // Alert 메시지를 표시
                alertUser(alert.title, alert.type === "SUCCESS" ? "success" : "error");
                location.reload(); // 성공 후 새로고침
            })
            .catch(error => {
                console.error("Error deleting members:", error);
                alertUser(error.message, "error");
            });
    });

    // Alert 메시지 표시 함수
    function alertUser(message, type) {
        const alertDiv = document.createElement("div");
        alertDiv.className = `alert-bar ${type === "success" ? "success" : "error"}`;
        alertDiv.textContent = message;

        document.body.appendChild(alertDiv);

        // 3초 후 Alert 메시지 제거
        setTimeout(() => {
            alertDiv.remove();
        }, 3000);
    }
});
