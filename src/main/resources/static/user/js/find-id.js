document.addEventListener('DOMContentLoaded', () => {
    const sendCodeButton = document.getElementById('sendCodeButton');
    const verifyCodeButton = document.getElementById('verifyCodeButton');
    const emailField = document.getElementById('email');
    const verificationCodeField = document.getElementById('verificationCode');
    const emailDiv = document.getElementById('emailDiv');
    const verificationDiv = document.getElementById('verificationDiv');
    const codeMessage = document.getElementById('codeMessage');
    const timerMessage = document.getElementById('timerMessage');
    const userIdElement = document.getElementById('userId');
    const resultMessage = document.getElementById('resultMessage');
    let timerInterval;

    /**
     * 인증 코드 요청
     */
    sendCodeButton.addEventListener('click', () => {
        const email = emailField.value;

        // 이메일 유효성 검사
        if (!email || !validateEmail(email)) {
            emailMessage.textContent = '유효한 이메일을 입력하세요.';
            emailMessage.style.color = 'red';
            return;
        }

        fetch(`/auth/send-code?email=${encodeURIComponent(email)}`, {
            method: 'POST',
        })
            .then((response) => response.json()
                .then((data) => {
                    if (!response.ok) {
                        // 에러 메시지 처리
                        emailMessage.textContent = data.message || '오류가 발생했습니다.';
                        emailMessage.style.color = 'red';
                    } else {
                        // 성공 메시지 처리
                        emailMessage.textContent = data.message || '인증 코드가 발송되었습니다.';
                        emailMessage.style.color = 'green';
                        emailDiv.style.display = 'none';
                        verificationDiv.style.display = 'block';
                        startTimer(300); // 5분 타이머 시작
                    }
                }))
            .catch(() => {
                emailMessage.textContent = '알 수 없는 오류가 발생했습니다. 다시 시도해주세요.';
                emailMessage.style.color = 'red';
            });
    });


    /**
     * 인증 코드 검증
     */
    verifyCodeButton.addEventListener('click', () => {
        const email = emailField.value;
        const code = verificationCodeField.value;

        if (!code) {
            codeMessage.textContent = '인증 코드를 입력하세요.';
            codeMessage.style.color = 'red';
            return;
        }

        fetch(`/auth/find-id/verify-code`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ email, code }),
        })
            .then((response) => response.json())
            .then((data) => {
                if (data.success) {
                    clearInterval(timerInterval); // 타이머 종료
                    emailDiv.style.display = 'none';
                    verificationDiv.style.display = 'none';
                    resultMessage.style.display = 'block';
                    userIdElement.textContent = data.userId;
                    showAlert(`축하합니다! 아이디 "${data.userId}"를 찾았습니다.`, 'alert-success');
                } else {
                    codeMessage.textContent = data.message || '인증 실패!';
                    codeMessage.style.color = 'red';
                }
            })
            .catch(() => {
                showAlert('알 수 없는 오류가 발생했습니다. 다시 시도해주세요.', 'alert-error');
            });
    });

    /**
     * 타이머 시작 함수
     */
    function startTimer(duration) {
        let remainingTime = duration;

        timerInterval = setInterval(() => {
            const minutes = Math.floor(remainingTime / 60);
            const seconds = remainingTime % 60;

            timerMessage.textContent = `남은 시간: ${minutes}분 ${seconds}초`;
            remainingTime--;

            if (remainingTime < 0) {
                clearInterval(timerInterval);
                timerMessage.textContent = '인증 시간이 만료되었습니다. 다시 요청해주세요.';
                verificationDiv.style.display = 'none';
                emailDiv.style.display = 'block';
            }
        }, 1000);
    }

    /**
     * 이메일 유효성 검사 함수
     */
    function validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    /**
     * 알림 표시 함수
     */
    function showAlert(message, type) {
        const alertBar = document.querySelector('.alert-bar');
        if (!alertBar) return;

        alertBar.querySelector('p').textContent = message;
        alertBar.className = `alert-bar ${type}`;
        alertBar.style.display = 'block';

        // 일정 시간 후 알림 자동 닫기
        setTimeout(() => {
            alertBar.style.display = 'none';
        }, 5000);
    }
});
