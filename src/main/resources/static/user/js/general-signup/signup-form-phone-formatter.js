/**
 * 휴대전화 번호 입력 시 자동으로 '-'를 추가하는 로직
 * @param {string} inputId - 휴대전화 번호 입력 필드의 ID
 */
export const initPhoneFormatter = (inputId) => {
    const inputElement = document.getElementById(inputId);

    if (!inputElement) {
        console.error(`Element with id '${inputId}' not found.`);
        return;
    }

    inputElement.addEventListener('input', function (event) {
        const input = event.target;
        let value = input.value.replace(/[^0-9]/g, ''); // 숫자만 남기기

        // 포맷팅 로직
        if (value.length > 3 && value.length <= 7) {
            // 010-xxxx
            value = value.replace(/(\d{3})(\d{1,4})/, '$1-$2');
        } else if (value.length > 7) {
            // 010-xxxx-xxxx
            value = value.replace(/(\d{3})(\d{4})(\d{1,4})/, '$1-$2-$3');
        }

        input.value = value; // 포맷팅된 값을 입력 필드에 다시 설정
    });
};
