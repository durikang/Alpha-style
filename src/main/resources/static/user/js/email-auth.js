document.addEventListener('DOMContentLoaded', () => {
    // DOM 요소 참조
    const sendCodeBtn = document.getElementById('sendCodeBtn');
    const emailInput = document.getElementById('email');
    const emailMessage = document.getElementById('emailMessage');
    const emailConfirmModal = document.getElementById('emailConfirmModal');
    const emailPreview = document.getElementById('emailPreview');
    const confirmEmailBtn = document.getElementById('confirmEmailBtn');
    const cancelEmailBtn = document.getElementById('cancelEmailBtn');
    const emailLoading = document.getElementById('emailLoading');
    const verifyCodeBtn = document.getElementById('verifyCodeBtn');
    const verificationCodeInput = document.getElementById('verificationCode');
    const codeMessage = document.getElementById('codeMessage');

    let isEmailConfirmed = false;

    // 이메일 전송 버튼 클릭 이벤트
    sendCodeBtn.addEventListener('click', () => {
        const email = emailInput.value;

        if (!email || !validateEmail(email)) {
            emailMessage.textContent = '유효한 이메일을 입력하세요.';
            emailMessage.style.color = 'red';
            return;
        }

        // 이메일 확인 모달 표시
        emailPreview.textContent = email;
        emailConfirmModal.style.display = 'block';
    });

    // 이메일 확인 버튼
    confirmEmailBtn.addEventListener('click', () => {
        emailConfirmModal.style.display = 'none';
        sendVerificationCode(); // 이메일 인증 요청 진행
    });

    // 이메일 취소 버튼
    cancelEmailBtn.addEventListener('click', () => {
        emailConfirmModal.style.display = 'none';
        emailMessage.textContent = '이메일 인증이 취소되었습니다.';
        emailMessage.style.color = 'red';
    });

    // 이메일 인증 요청 함수
    function sendVerificationCode() {
        const email = emailInput.value;

        emailLoading.style.display = 'inline'; // 로딩 표시
        sendCodeBtn.disabled = true; // 버튼 비활성화

        // AJAX 요청으로 인증코드 전송
        fetch('/auth/send-code?email=' + encodeURIComponent(email), {
            method: 'POST'
        })
            .then(response => {
                if (!response.ok) {
                    // 응답 코드가 200~299 범위를 벗어날 경우
                    throw new Error('서버 오류');
                }
                return response.text();
            })
            .then(data => {
                emailMessage.textContent = data;
                emailMessage.style.color = 'green';
                startTimer(300); // 5분 타이머
            })
            .catch(error => {
                emailMessage.textContent = error.message || '서버 오류가 발생했습니다. 잠시 후 다시 시도하세요.';
                emailMessage.style.color = 'red';
                console.error('오류 내용:', error);
            })
            .finally(() => {
                emailLoading.style.display = 'none'; // 로딩 숨기기
                sendCodeBtn.disabled = false; // 버튼 활성화
            });
    }

    // 인증코드 확인 버튼 클릭 이벤트
    verifyCodeBtn.addEventListener('click', () => {
        const email = emailInput.value;
        const code = verificationCodeInput.value;

        if (!code) {
            codeMessage.textContent = '인증코드를 입력해주세요.';
            codeMessage.style.color = 'red';
            return;
        }

        // 인증코드 검증 요청
        fetch('/auth/verify-code?email=' + encodeURIComponent(email) + '&code=' + encodeURIComponent(code), {
            method: 'POST'
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('인증 실패');
                }
                return response.text();
            })
            .then(data => {
                codeMessage.textContent = data; // 인증 성공 메시지
                codeMessage.style.color = 'green';

                // 인증 완료 시 관련 UI 요소 숨기기
                emailAuthDiv.style.display = 'none';
                const authCompleteMessage = document.getElementById('authCompleteMessage');
                authCompleteMessage.style.display = 'block'; // 메시지 표시
                authCompleteMessage.style.opacity = '1'; // 애니메이션 효과 적용
            })
            .catch(error => {
                codeMessage.textContent = error.message || '인증 실패! 올바른 코드를 입력해주세요.';
                codeMessage.style.color = 'red';
                console.error('오류 내용:', error);
            });
    });


    // 타이머 시작 함수
    function startTimer(duration) {
        const timerMessage = document.getElementById('timerMessage');
        let remainingTime = duration;

        // 타이머 업데이트 함수
        const updateTimer = () => {
            const minutes = Math.floor(remainingTime / 60);
            const seconds = remainingTime % 60;

            timerMessage.textContent = `남은 시간: ${minutes}분 ${seconds}초`;
            remainingTime -= 1;

            if (remainingTime < 0) {
                clearInterval(timerInterval); // 타이머 종료
                timerMessage.textContent = '인증 시간이 만료되었습니다. 다시 요청해주세요.';
            }
        };

        // 1초 간격으로 타이머 업데이트
        updateTimer();
        const timerInterval = setInterval(updateTimer, 1000);
    }

    // 이메일 유효성 검사 함수
    function validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }
});
