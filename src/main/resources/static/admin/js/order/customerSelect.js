// 고객 드롭다운 업데이트 함수
function updateCustomerDropdown(customers) {
    const customerSelect = document.getElementById("customerSelect");
    if (!customerSelect) {
        console.error("customerSelect 요소를 찾을 수 없습니다.");
        return;
    }

    customerSelect.innerHTML = ""; // 기존 옵션 제거

    // 기본 옵션 추가
    const defaultOption = document.createElement("option");
    defaultOption.value = "";
    defaultOption.textContent = "고객을 선택하세요";
    customerSelect.appendChild(defaultOption);

    // 새로운 고객 목록 추가
    customers.forEach(customer => {
        const option = document.createElement("option");
        option.value = customer.id; // customer ID
        option.textContent = customer.name; // customer 이름
        customerSelect.appendChild(option);
    });
}

// 검색 버튼 클릭 시 동작
document.getElementById("filterButton").addEventListener("click", function (e) {
    e.preventDefault(); // 기본 동작 방지
    const form = document.querySelector("form");
    const formData = new FormData(form);

    fetch("/admin/orders/list/ajax?" + new URLSearchParams(formData), {
        method: "GET",
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("검색 중 문제가 발생했습니다.");
            }
            return response.json();
        })
        .then(data => {
            console.log("검색 결과:", data);
            updateCustomerDropdown(data.customers); // 고객 드롭다운 갱신
        })
        .catch(error => {
            console.error("검색 오류:", error);
            alert("검색 중 문제가 발생했습니다. 다시 시도해주세요.");
        });
});
