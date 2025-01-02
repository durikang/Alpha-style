document.addEventListener('DOMContentLoaded', () => {
    // DOM 요소 참조
    const sendCodeBtn = document.getElementById('sendCodeBtn');
    const emailInput = document.getElementById('email');
    const emailMessage = document.getElementById('emailMessage');
    const verifyCodeBtn = document.getElementById('verifyCodeBtn');
    const verificationCodeInput = document.getElementById('verificationCode');
    const codeMessage = document.getElementById('codeMessage');
    const timerMessage = document.getElementById('timerMessage');
    const authCompleteMessage = document.getElementById('authCompleteMessage');

    let isEmailVerified = false; // 이메일 인증 상태 플래그
    let timerInterval;

    // 이메일 인증 요청
    sendCodeBtn.addEventListener('click', () => {
        const email = emailInput.value;

        if (!email || !validateEmail(email)) {
            emailMessage.textContent = '유효한 이메일을 입력하세요.';
            emailMessage.style.color = 'red';
            return;
        }

        fetch('/auth/send-code?email=' + encodeURIComponent(email), {
            method: 'POST',
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('서버 오류');
                }
                return response.text();
            })
            .then((data) => {
                emailMessage.textContent = data;
                emailMessage.style.color = 'green';
                startTimer(300); // 5분 타이머 시작
            })
            .catch((error) => {
                emailMessage.textContent = error.message || '서버 오류가 발생했습니다. 잠시 후 다시 시도하세요.';
                emailMessage.style.color = 'red';
                console.error('오류 내용:', error);
            });
    });

    // 인증 코드 확인
    verifyCodeBtn.addEventListener('click', () => {
        const email = emailInput.value;
        const code = verificationCodeInput.value;

        if (!code) {
            codeMessage.textContent = '인증 코드를 입력해주세요.';
            codeMessage.style.color = 'red';
            return;
        }

        fetch('/auth/verify-code?email=' + encodeURIComponent(email) + '&code=' + encodeURIComponent(code), {
            method: 'POST',
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('인증 실패');
                }
                return response.text();
            })
            .then((data) => {
                codeMessage.textContent = data; // 인증 성공 메시지
                codeMessage.style.color = 'green';
                authCompleteMessage.style.display = 'block'; // 인증 완료 메시지 표시
                emailInput.disabled = true; // 이메일 입력 비활성화
                verificationCodeInput.disabled = true; // 인증 코드 입력 비활성화
                sendCodeBtn.disabled = true;
                verifyCodeBtn.disabled = true;
                clearInterval(timerInterval); // 타이머 종료
            })
            .catch((error) => {
                codeMessage.textContent = error.message || '인증 실패! 올바른 코드를 입력해주세요.';
                codeMessage.style.color = 'red';
                console.error('오류 내용:', error);
            });
    });

    // 타이머 시작 함수
    function startTimer(duration) {
        let remainingTime = duration;

        const updateTimer = () => {
            const minutes = Math.floor(remainingTime / 60);
            const seconds = remainingTime % 60;

            timerMessage.textContent = `남은 시간: ${minutes}분 ${seconds}초`;
            remainingTime -= 1;

            if (remainingTime < 0) {
                clearInterval(timerInterval);
                timerMessage.textContent = '인증 시간이 만료되었습니다. 다시 요청해주세요.';
                emailMessage.textContent = '';
            }
        };

        updateTimer();
        timerInterval = setInterval(updateTimer, 1000);
    }

    // 이메일 유효성 검사 함수
    function validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
});
