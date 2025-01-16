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

    // 에러 메시지가 있을 경우 toggle-password 버튼 위치 조정
    const errorMessageElement = document.querySelector('.errorMessage');
    const togglePasswordButton = document.querySelector('.toggle-password');

    if (errorMessageElement && errorMessageElement.innerText.trim() !== "") {
        // 에러 메시지가 있을 경우
        togglePasswordButton.style.top = '38%'; // 버튼 위치를 37%로 변경
    } else {
        // 에러 메시지가 없을 경우
        togglePasswordButton.style.top = '41%'; // 버튼 위치를 원래대로 설정
    }

    // MutationObserver로 에러 메시지가 동적으로 변경될 때 버튼 위치 조정
    const observer = new MutationObserver(function(mutations) {
        mutations.forEach(function(mutation) {
            if (mutation.target.classList.contains('errorMessage')) {
                if (mutation.target.innerText.trim() !== "") {
                    togglePasswordButton.style.top = '38%'; // 에러 메시지가 있을 때 버튼 위치 변경
                } else {
                    togglePasswordButton.style.top = '41%'; // 에러 메시지가 없을 때 원래 위치로 설정
                }
            }
        });
    });

    if (errorMessageElement) {
        observer.observe(errorMessageElement, { childList: true, subtree: true });
    }
});
