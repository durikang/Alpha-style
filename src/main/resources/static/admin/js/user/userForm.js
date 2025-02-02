function openPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            document.getElementById('address').value = data.address; // 기본 주소
            document.getElementById('zipCode').value = data.zonecode; // 우편번호
        }
    }).open();
}

// 주소 검색 이벤트 연결
document.addEventListener("DOMContentLoaded", function () {
    // 주소 입력 필드 및 관련 필드 클릭 시 이벤트 연결
    const addressFields = [
        document.getElementById("address"),
        document.getElementById("zipCode")
    ];

    addressFields.forEach(field => {
        field.addEventListener("click", openPostcode);
    });
});
