// email-auth.js
import { Timer } from './timer.js';

export const EmailAuth = (() => {
    const init = (config) => {
        const {
            sendCodeBtn,
            emailInput,
            emailLabel,
            emailMessage,
            verifyCodeBtn,
            verificationCodeInput,
            verificationCodeContainer,
            codeLabel,
            codeMessage,
            timerMessage,
            authCompleteMessage,
            toStep2Button,
        } = config;

        // 필수 요소 확인
        if (
            !sendCodeBtn ||
            !emailInput ||
            !verifyCodeBtn ||
            !verificationCodeInput ||
            !verificationCodeContainer ||
            !timerMessage ||
            !authCompleteMessage ||
            !toStep2Button
        ) {
            console.error('필수 요소 중 일부가 누락되었습니다:', config);
            return;
        }

        let timerInterval;       // setInterval ID
        let isVerified = false;  // 인증 성공 여부 (타이머 콜백 중복 실행 방지)

        // 간단 이메일 검사
        const validateSimpleEmail = (email) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

        // 1) "인증 코드 보내기" 클릭
        sendCodeBtn.addEventListener('click', async () => {
            const email = emailInput.value.trim();
            console.log('Send Code button clicked with email:', email);

            // 이메일 유효성 검사
            if (!validateSimpleEmail(email)) {
                console.log('Invalid email format.');
                emailMessage.textContent = '유효한 이메일을 입력하세요.';
                emailMessage.style.color = 'red';
                emailMessage.style.display = 'block'; // 즉시 표시
                return;
            }

            try {
                const response = await fetch('/auth/signup/send-code', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ email }),
                });
                if (!response.ok) {
                    const data = await response.json();
                    throw new Error(data.message || '서버 오류가 발생했습니다.');
                }

                // 발송 성공
                console.log('Email sent successfully.');
                emailMessage.textContent = '이메일이 발송되었습니다. 이메일을 확인하세요!';
                emailMessage.style.color = 'green';
                emailMessage.style.display = 'block'; // 즉시 표시

                // (A) 이메일 입력 필드 숨기기
                emailInput.style.display = 'none';
                console.log('Email input field hidden.');

                // (B) "인증 코드 보내기" 버튼 숨기기
                sendCodeBtn.style.display = 'none';
                console.log('"인증 코드 받기" 버튼 숨김.');

                // (C) 인증코드 입력 필드 보이기
                verificationCodeContainer.style.display = 'block';
                console.log('인증코드 입력 필드 표시.');

                // (D) 인증코드 확인 버튼 보이기
                verifyCodeBtn.style.display = 'block';
                console.log('"인증코드 확인" 버튼 표시.');

                // (E) 타이머 메시지 보이기
                timerMessage.style.display = 'block';
                console.log('타이머 메시지 표시.');

                // 타이머 시작 (300초 = 5분 예시)
                timerInterval = Timer.startTimer(300, timerMessage, () => {
                    console.log('Timer callback invoked.');
                    if (isVerified) {
                        console.log('Timer expired but already verified.');
                        return; // 인증 완료 후, 타이머 콜백 무시
                    }

                    console.log('Timer expired.');
                    // 만료 콜백
                    // 인증코드 필드/버튼 숨기기
                    verificationCodeContainer.style.display = 'none';
                    verifyCodeBtn.style.display = 'none';
                    timerMessage.style.display = 'none';

                    // emailMessage 숨김 + 초기화
                    emailMessage.textContent = '';
                    emailMessage.style.display = 'none';
                });

            } catch (error) {
                console.error('Error sending email:', error);
                emailMessage.textContent = error.message || '서버 오류가 발생했습니다. 잠시 후 다시 시도하세요.';
                emailMessage.style.color = 'red';
                emailMessage.style.display = 'block'; // 즉시 표시
            }
        });

        // 2) "인증 코드 확인" 클릭
        verifyCodeBtn.addEventListener('click', async () => {
            const code = verificationCodeInput.value.trim();
            console.log('Verify Code button clicked with code:', code);

            if (!code) {
                console.log('No code entered.');
                codeMessage.textContent = '인증 코드를 입력해주세요.';
                codeMessage.style.color = 'red';
                codeMessage.style.display = 'block'; // 즉시 표시
                return;
            }

            try {
                const response = await fetch(`/auth/verify-code?email=${encodeURIComponent(emailInput.value)}&code=${encodeURIComponent(code)}`, {
                    method: 'POST',
                });
                if (!response.ok) {
                    throw new Error('인증 실패');
                }

                // 인증 성공
                console.log('Email verified successfully.');
                isVerified = true;     // 이제부터 타이머 만료 콜백이 동작 못하도록
                clearInterval(timerInterval);
                console.log('timerInterval cleared. isVerified set to true.');

                // 인증코드 필드, 버튼, 타이머 숨기기
                verificationCodeContainer.style.display = 'none';
                verifyCodeBtn.style.display = 'none';
                timerMessage.style.display = 'none';
                console.log('인증코드 필드, 버튼, 타이머 숨김.');

                // emailMessage 숨김 + 초기화
                emailMessage.textContent = '';
                emailMessage.style.display = 'none';
                console.log('emailMessage 숨김 및 초기화.');

                // 인증 완료 메시지 표시
                authCompleteMessage.textContent = '🎉 인증이 완료되었습니다!';
                authCompleteMessage.style.display = 'block'; // 즉시 표시
                console.log('인증 완료 메시지 표시.');

                // "다음 단계" 버튼 활성화
                toStep2Button.disabled = false;
                console.log('"다음 단계" 버튼 활성화.');

            } catch (error) {
                console.error('Error verifying email:', error);
                codeMessage.textContent = error.message || '인증 실패! 올바른 코드를 입력해주세요.';
                codeMessage.style.color = 'red';
                codeMessage.style.display = 'block'; // 즉시 표시
            }
        });
    };

    return { init };
})();
