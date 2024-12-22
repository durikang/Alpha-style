document.addEventListener("DOMContentLoaded", () => {
    const deleteButton = document.getElementById("deleteSelected");

    if (deleteButton) {
        deleteButton.addEventListener("click", () => {
            const checkboxes = document.querySelectorAll(".select-checkbox");
            const selectedIds = Array.from(checkboxes)
                .filter(checkbox => checkbox.checked)
                .map(checkbox => checkbox.value);

            if (selectedIds.length === 0) {
                alert("삭제할 항목을 선택해주세요.");
                return;
            }

            if (confirm(`${deleteButton.innerText}를 진행하시겠습니까?`)) {
                deleteButton.disabled = true; // 버튼 비활성화
                deleteButton.innerText = "처리 중..."; // 로딩 표시

                fetch(deleteButton.dataset.deleteUrl, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(selectedIds),
                })
                    .then(response => {
                        if (response.ok) {
                            alert(`${deleteButton.innerText}가 완료되었습니다.`);
                            window.location.reload();
                        } else {
                            return response.json()
                                .then(data => {
                                    alert("오류 발생: " + (data.message || "알 수 없는 오류"));
                                })
                                .catch(() => {
                                    // alert("서버에서 예상치 못한 오류가 발생했습니다.");
                                });
                        }
                    })
                    .catch(error => {
                        console.error("삭제 중 오류 발생:", error);
                        alert("네트워크 오류가 발생했습니다. 다시 시도해주세요.");
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
});
