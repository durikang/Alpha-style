document.addEventListener('DOMContentLoaded', () => {
    // DOM 요소 참조
    const sendCodeBtn = document.getElementById('sendCodeBtn');
    const emailInput = document.getElementById('email');
    const emailLabel = document.querySelector('label[for="email"]');
    const emailMessage = document.getElementById('emailMessage');
    const verifyCodeBtn = document.getElementById('verifyCodeBtn');
    const verificationCodeInput = document.getElementById('verificationCode');
    const codeLabel = document.querySelector('label[for="verificationCode"]');
    const codeMessage = document.getElementById('codeMessage');
    const timerMessage = document.getElementById('timerMessage');
    const authCompleteMessage = document.getElementById('authCompleteMessage');
    const mobilePhoneInput = document.getElementById('mobilePhone');
    const authButton = document.getElementById('authButton'); // 본인 인증 버튼

    let timerInterval;

    // 이메일 인증 요청
    sendCodeBtn.addEventListener('click', () => {
        const email = emailInput.value;

        if (!email || !validateEmail(email)) {
            emailMessage.textContent = '유효한 이메일을 입력하세요.';
            emailMessage.style.color = 'red';
            return;
        }

        // 회원 가입용 인증 요청 경로로 수정
        fetch('/auth/signup/send-code?email=' + encodeURIComponent(email), {
            method: 'POST',
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error('서버 오류');
                }
                return response.json();
            })
            .then(() => {
                emailMessage.textContent = '이메일이 발송되었습니다. 이메일을 확인하세요!';
                emailMessage.style.color = 'green';

                // 숨김 처리
                emailInput.style.display = 'none';
                emailLabel.style.display = 'none';
                sendCodeBtn.style.display = 'none';
                authButton.style.display = 'none'; // 본인 인증 버튼 숨김
                emailMessage.style.display = 'none'; // 이메일 발송 메시지 숨김

                // 인증 코드 관련 요소 표시
                verificationCodeInput.style.display = 'block';
                codeLabel.style.display = 'block';
                verifyCodeBtn.style.display = 'block';

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
            .then(() => {
                // 폭죽 아이콘과 인증 완료 메시지 표시
                authCompleteMessage.style.display = 'block';
                authCompleteMessage.style.textAlign = 'center';
                authCompleteMessage.innerHTML = '🎉 인증이 완료되었습니다!';

                // 숨김 처리
                clearInterval(timerInterval);
                timerMessage.style.display = 'none';
                verificationCodeInput.style.display = 'none';
                codeLabel.style.display = 'none';
                verifyCodeBtn.style.display = 'none';

                // 다음 커서 이동
                mobilePhoneInput.focus();
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
                timerMessage.style.color = 'red';

                // 숨김 해제
                emailInput.style.display = 'block';
                emailLabel.style.display = 'block';
                sendCodeBtn.style.display = 'block';
                authButton.style.display = 'block'; // 본인 인증 버튼 표시

                verificationCodeInput.style.display = 'none';
                codeLabel.style.display = 'none';
                verifyCodeBtn.style.display = 'none';

                emailMessage.style.display = 'block'; // 이메일 메시지 재표시
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
