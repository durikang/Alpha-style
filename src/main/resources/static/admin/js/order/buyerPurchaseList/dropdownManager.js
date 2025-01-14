export function updateCustomerDropdown(customers) {
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
    customers.forEach((customer) => {
        const option = document.createElement("option");
        option.value = customer.id; // customer ID
        option.textContent = customer.name; // customer 이름
        customerSelect.appendChild(option);
    });
}
