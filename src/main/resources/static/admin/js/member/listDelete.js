// src/main/resources/static/user/js/listDelete.js
document.addEventListener("DOMContentLoaded", () => {
    const deleteButton = document.getElementById("deleteSelected");
    const selectAllCheckbox = document.getElementById("selectAll");

    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", () => {
            const checkboxes = document.querySelectorAll(".select-checkbox");
            checkboxes.forEach(checkbox => {
                checkbox.checked = selectAllCheckbox.checked;
            });
        });
    }

    if (deleteButton) {
        deleteButton.addEventListener("click", () => {
            const checkboxes = document.querySelectorAll(".select-checkbox");
            const selectedIds = Array.from(checkboxes)
                .filter(checkbox => checkbox.checked)
                .map(checkbox => checkbox.value);

            if (selectedIds.length === 0) {
                // AJAX 전용 알림 바 사용
                showAjaxAlert("삭제할 항목을 선택해주세요.", "ERROR");
                return;
            }

            if (confirm("선택한 회원을 삭제하시겠습니까?")) {
                deleteButton.disabled = true; // 버튼 비활성화
                deleteButton.innerText = "처리 중..."; // 로딩 표시

                fetch(deleteButton.dataset.deleteUrl, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        // CSRF 토큰이 필요한 경우 추가
                        // "X-CSRF-TOKEN": getCsrfToken(),
                    },
                    body: JSON.stringify(selectedIds),
                })
                    .then(response => response.json().then(data => ({ status: response.status, body: data })))
                    .then(({ status, body }) => {
                        if (status >= 200 && status < 300) {
                            // 성공 메시지 표시
                            showAjaxAlert(body.title, body.type);

                            // 2초 후 페이지 리로드
                            setTimeout(() => {
                                window.location.reload();
                            }, 2000);
                        } else {
                            // 에러 메시지 표시
                            showAjaxAlert(body.title, body.type);
                        }
                    })
                    .catch(error => {
                        console.error("삭제 중 오류 발생:", error);
                        showAjaxAlert("네트워크 오류가 발생했습니다. 다시 시도해주세요.", "ERROR");
                    })
                    .finally(() => {
                        deleteButton.disabled = false; // 버튼 재활성화
                        deleteButton.innerText = "삭제"; // 원래 텍스트로 복원
                    });
            }
        });
    } else {
        console.error("Delete button not found.");
    }

    /**
     * AJAX 전용 알림 바 표시 함수
     * @param {string} message - 알림 메시지
     * @param {string} type - 'SUCCESS' 또는 'ERROR'
     */
    function showAjaxAlert(message, type) {
        if (typeof showAjaxAlert !== 'undefined') {
            // 글로벌 함수인 경우
            window.showAjaxAlert(message, type);
        } else {
            console.error("showAjaxAlert 함수가 정의되지 않았습니다.");
        }
    }
});
