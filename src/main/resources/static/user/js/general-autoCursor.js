document.addEventListener('DOMContentLoaded', () => {
    const formFields = document.querySelectorAll('input');

    formFields.forEach((field, index) => {
        field.addEventListener('keydown', (event) => {
            if (event.key === 'Enter') {
                event.preventDefault(); // 기본 Enter 동작 방지
                const value = field.value.trim();

                // 입력값 유효성 검증
                if (value) {
                    const nextField = formFields[index + 1];
                    if (nextField) {
                        nextField.focus(); // 다음 필드로 이동
                    }
                } else {
                    alert("현재 입력란을 먼저 채워주세요.");
                }
            }
        });
    });
});
