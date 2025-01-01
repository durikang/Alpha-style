// 비밀번호 확인 모듈
export const initPasswordValidation = (passwordFieldId, passwordConfirmFieldId) => {
    // 비밀번호와 비밀번호 확인 필드 가져오기
    const passwordInput = document.getElementById(passwordFieldId);
    const passwordConfirmInput = document.getElementById(passwordConfirmFieldId);

    // 메시지를 표시할 div 생성
    const passwordMessage = document.createElement('div');
    passwordMessage.style.marginTop = '5px';
    passwordConfirmInput.parentNode.appendChild(passwordMessage);

    // 비밀번호 확인 함수
    const validatePassword = () => {
        const password = passwordInput.value;
        const passwordConfirm = passwordConfirmInput.value;

        if (passwordConfirm === '') {
            passwordMessage.textContent = '';
            return;
        }

        if (password !== passwordConfirm) {
            passwordMessage.textContent = '비밀번호가 일치하지 않습니다.';
            passwordMessage.style.color = 'red';
        } else {
            passwordMessage.textContent = '비밀번호가 일치합니다.';
            passwordMessage.style.color = 'green';
        }
    };

    // 입력 이벤트 리스너 추가
    passwordInput.addEventListener('input', validatePassword);
    passwordConfirmInput.addEventListener('input', validatePassword);
};
