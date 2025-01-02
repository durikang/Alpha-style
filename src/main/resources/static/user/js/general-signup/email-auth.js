import { Timer } from './timer.js';

export const EmailAuth = (() => {
    const init = (config) => {
        const {
            sendCodeBtn,
            emailInput,
            emailMessage,
            verifyCodeBtn,
            verificationCodeInput,
            verificationCodeContainer,
            timerMessage, // 타이머 메시지
            codeMessage,
            authCompleteMessage,
            toStep2Button,
        } = config;

        if (!sendCodeBtn || !emailInput || !timerMessage) {
            console.error('필수 요소 중 일부가 DOM에 존재하지 않습니다.');
            return;
        }

        let timerInterval;

        // 인증 코드 발송
        sendCodeBtn.addEventListener('click', () => {
            const email = emailInput.value;

            if (!validateEmail(email)) {
                emailMessage.textContent = '유효한 이메일을 입력하세요.';
                emailMessage.style.color = 'red';
                return;
            }

            fetch(`/auth/signup/send-code?email=${encodeURIComponent(email)}`, {
                method: 'POST',
            })
                .then((response) => {
                    if (!response.ok) throw new Error('서버 오류');
                    return response.json();
                })
                .then(() => {
                    emailMessage.textContent = '이메일이 발송되었습니다. 이메일을 확인하세요!';
                    emailMessage.style.color = 'green';

                    // 인증 코드 입력 UI 활성화
                    verificationCodeContainer.style.display = 'block';
                    verifyCodeBtn.style.display = 'block';

                    // 타이머 시작
                    timerMessage.style.display = 'block';
                    timerMessage.style.color = 'black'; // 타이머 초기화
                    timerInterval = Timer.startTimer(300, timerMessage, () => {
                        timerMessage.textContent = '인증 시간이 만료되었습니다.';
                        timerMessage.style.color = 'red';
                    });
                })
                .catch((error) => {
                    emailMessage.textContent = error.message || '서버 오류가 발생했습니다. 잠시 후 다시 시도하세요.';
                    emailMessage.style.color = 'red';
                    console.error('오류 내용:', error);
                });
        });

        // 인증 코드 확인
        verifyCodeBtn.addEventListener('click', () => {
            const code = verificationCodeInput.value;

            if (!code) {
                codeMessage.textContent = '인증 코드를 입력해주세요.';
                codeMessage.style.color = 'red';
                return;
            }

            fetch(`/auth/verify-code?email=${encodeURIComponent(emailInput.value)}&code=${encodeURIComponent(code)}`, {
                method: 'POST',
            })
                .then((response) => {
                    if (!response.ok) throw new Error('인증 실패');
                    return response.text();
                })
                .then(() => {
                    authCompleteMessage.style.display = 'block';
                    authCompleteMessage.innerHTML = '🎉 인증이 완료되었습니다!';

                    clearInterval(timerInterval);
                    timerMessage.style.display = 'none';
                    verificationCodeContainer.style.display = 'none';
                    verifyCodeBtn.style.display = 'none';
                    toStep2Button.disabled = false;
                })
                .catch((error) => {
                    codeMessage.textContent = error.message || '인증 실패! 올바른 코드를 입력해주세요.';
                    codeMessage.style.color = 'red';
                    console.error('오류 내용:', error);
                });
        });
    };

    const validateEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

    return { init };
})();
