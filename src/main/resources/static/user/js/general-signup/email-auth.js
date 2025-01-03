import { Timer } from './timer.js';
import { UIManager } from './uiManager.js';

export const EmailAuth = (() => {
    const init = (config) => {
        const {
            sendCodeBtn,
            emailInput,
            emailMessage,
            verifyCodeBtn,
            verificationCodeInput,
            verificationCodeContainer,
            timerMessage,
            codeMessage,
            authCompleteMessage,
            toStep2Button,
        } = config;


        // 방어 코드 추가
        if (
            !sendCodeBtn ||
            !emailInput ||
            !verifyCodeBtn ||
            !verificationCodeInput ||
            !verificationCodeContainer ||
            !timerMessage ||
            !authCompleteMessage || // authCompleteMessage 확인 추가
            !toStep2Button
        ) {
            console.error('필수 요소 중 일부가 누락되었습니다:', config);
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

            fetch('/auth/signup/send-code', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ email }),
            })
                .then((response) => {
                    if (!response.ok) {
                        return response.json().then((data) => {
                            throw new Error(data.message || '서버 오류가 발생했습니다.');
                        });
                    }
                    return response.json();
                })
                .then(() => {
                    emailMessage.textContent = '이메일이 발송되었습니다. 이메일을 확인하세요!';
                    emailMessage.style.color = 'green';

                    UIManager.hideElement(emailInput);
                    UIManager.hideElement(sendCodeBtn);

                    UIManager.showElement(verificationCodeContainer);
                    UIManager.showElement(verifyCodeBtn);
                    UIManager.showElement(timerMessage);

                    timerInterval = Timer.startTimer(300, timerMessage, () => {
                        timerMessage.textContent = '인증 시간이 만료되었습니다.';
                        timerMessage.style.color = 'red';

                        UIManager.hideElement(verificationCodeContainer);
                        UIManager.hideElement(verifyCodeBtn);
                        UIManager.hideElement(timerMessage);
                        emailMessage.textContent = '';
                        UIManager.showElement(emailInput);
                        UIManager.showElement(sendCodeBtn);
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
                    // 인증 완료 메시지 표시
                    if (authCompleteMessage) {
                        authCompleteMessage.style.display = 'block';
                        authCompleteMessage.innerHTML = '🎉 인증이 완료되었습니다!';
                    } else {
                        console.warn('authCompleteMessage element is missing in the DOM.');
                    }

                    // 이메일 발송 메시지 숨김
                    if (emailMessage) {
                        UIManager.hideElement(emailMessage);
                    }

                    // 이메일 입력 필드와 플로팅 레이블 숨김
                    if (emailInput) {
                        UIManager.hideElement(emailInput);
                    }
                    const emailLabel = document.querySelector('label[for="email"]');
                    if (emailLabel) {
                        UIManager.hideElement(emailLabel);
                    }

                    clearInterval(timerInterval);
                    UIManager.hideElement(timerMessage);
                    UIManager.hideElement(verificationCodeContainer);
                    UIManager.hideElement(verifyCodeBtn);

                    // 다음 단계 버튼 활성화
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
