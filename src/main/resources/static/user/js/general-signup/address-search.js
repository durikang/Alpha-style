export const AddressSearch = (() => {
    const initialize = (config) => {
        const {
            searchAddressBtn,
            zipCodeInput,
            basicAddressInput,
            secondaryAddressInput,
        } = config;

        // 방어 코드
        if (
            !searchAddressBtn ||
            !zipCodeInput ||
            !basicAddressInput ||
            !secondaryAddressInput
        ) {
            console.error('주소 찾기 구성 요소가 누락되었습니다:', config);
            return;
        }

        // "주소 찾기" 버튼 클릭 이벤트
        searchAddressBtn.addEventListener('click', () => {
            new daum.Postcode({
                oncomplete: function (data) {
                    const fullAddr = data.address; // 도로명 주소
                    const extraAddr =
                        data.bname || data.buildingName
                            ? ` (${data.bname || ''}${data.buildingName ? ', ' + data.buildingName : ''})`
                            : '';

                    // 우편번호와 주소를 입력 필드에 설정
                    zipCodeInput.value = data.zonecode;
                    basicAddressInput.value = fullAddr + extraAddr;

                    // 추가 주소 입력 필드로 포커스 이동
                    secondaryAddressInput.focus();
                },
            }).open();
        });
    };

    return { initialize };
})();
