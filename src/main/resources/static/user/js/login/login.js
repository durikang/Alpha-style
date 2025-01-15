document.addEventListener('DOMContentLoaded', function () {
    // 아이디 유효성 검사
    document.getElementById('userId').addEventListener('input', function () {
        const userId = this.value;
        const errorMessage = document.querySelector('#userIdError');
        if (userId.length < 4 || userId.length > 16) {
            errorMessage.textContent = '아이디는 4~16자 사이여야 합니다.';
        } else {
            errorMessage.textContent = '';
        }
    });

    // 비밀번호 보기/숨기기 토글
    window.togglePassword = function () {
        const passwordField = document.getElementById('password');
        if (passwordField.type === 'password') {
            passwordField.type = 'text';
        } else {
            passwordField.type = 'password';
        }
    };

    // 로딩 스피너 표시
    window.showLoadingSpinner = function () {
        const spinner = document.getElementById('loadingSpinner');
        spinner.style.display = 'block';

        // 로딩 스피너는 비동기 작업과 별도로 동작
        setTimeout(() => {
            spinner.style.display = 'none';
        }, 2000); // 2초 후 숨김
    };

    // 아이디 저장 로직
    const saveIdCheckbox = document.getElementById('save-id');
    saveIdCheckbox.addEventListener('change', function () {
        const userId = document.getElementById('userId').value;
        if (this.checked) {
            localStorage.setItem('savedUserId', userId);
        } else {
            localStorage.removeItem('savedUserId');
        }
    });

    // 저장된 아이디 불러오기
    const savedUserId = localStorage.getItem('savedUserId');
    if (savedUserId) {
        document.getElementById('userId').value = savedUserId;
        document.getElementById('save-id').checked = true;
    }
});
