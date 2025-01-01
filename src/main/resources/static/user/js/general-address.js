document.addEventListener('DOMContentLoaded', () => {
    const searchAddressBtn = document.getElementById('searchAddressBtn');
    const zipCodeInput = document.getElementById('zipCode');
    const basicAddressInput = document.getElementById('basicAddress');
    const secondaryAddressInput = document.getElementById('secondaryAddress');

    // 주소찾기 버튼 클릭 이벤트
    searchAddressBtn.addEventListener('click', () => {
        new daum.Postcode({
            oncomplete: function (data) {
                // 우편번호와 기본 주소 설정
                zipCodeInput.value = data.zonecode;
                basicAddressInput.value = data.roadAddress || data.jibunAddress;

                // 상세주소 필드에 포커스
                secondaryAddressInput.focus();
            }
        }).open();
    });
});
