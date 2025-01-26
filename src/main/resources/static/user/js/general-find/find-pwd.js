document.addEventListener('DOMContentLoaded', function () {
    const findPwdForm = document.getElementById('findPwdForm');
    const authCodeSection = document.getElementById('authCodeSection');
    const resetPwdSection = document.getElementById('resetPwdSection');
    const verifyAuthCodeBtn = document.getElementById('verifyAuthCodeBtn');
    const authMessage = document.getElementById('authMessage');
    const resetPwdForm = document.getElementById('resetPwdForm');

    // 인증에 성공한 이메일을 저장해둘 변수
    let verifiedEmail = null;

    // [Step 1] 이메일 입력 후 인증 코드 전송
    findPwdForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const email = document.getElementById('email').value;

        fetch("/auth/reset-password/send-code?email=" + encodeURIComponent(email), {
            method: 'POST'
        })
            .then(response => response.json())
            .then(data => {
                // 서버 측에서 성공 시 { message: "...", ... } 형태를 반환
                if (data.message && !data.message.includes('등록되지 않은')) {
                    // 간단한 판단:
                    // data에 success 필드를 안 넣으셨다면,
                    // message로 구분하거나, 혹은 서버에서도 { success: true }를 넣어주도록 하면 더 명확
                    alert(data.message);

                    // 이메일 입력 섹션 페이드아웃
                    findPwdForm.classList.add('fade-out');
                    setTimeout(() => {
                        findPwdForm.classList.add('hidden');
                        // 인증코드 섹션 페이드인
                        authCodeSection.classList.remove('hidden');
                        authCodeSection.classList.add('fade-in');
                    }, 500);

                } else {
                    // 예외: 등록되지 않은 이메일 등
                    alert(data.message || '이메일 전송 실패');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('알 수 없는 오류가 발생했습니다.');
            });
    });

    // [Step 2] 인증 코드 확인
    verifyAuthCodeBtn.addEventListener('click', function () {
        const authCode = document.getElementById('authCode').value;
        const email = document.getElementById('email').value; // Step 1에서 입력된 이메일과 동일

        if (!authCode) {
            authMessage.innerText = '인증 코드를 입력하세요.';
            authMessage.style.color = 'red';
            return;
        }

        // email과 code를 같이 전달해야 서버가 verifyCode(email, code) 가능
        fetch("/auth/verify-code-json", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: email,
                code: authCode
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('인증 코드가 확인되었습니다!');
                    // 인증 성공: verifiedEmail에 저장
                    verifiedEmail = email;

                    // 인증코드 섹션 페이드아웃
                    authCodeSection.classList.add('fade-out');
                    setTimeout(() => {
                        authCodeSection.classList.add('hidden');
                        // 새 비밀번호 섹션 페이드인
                        resetPwdSection.classList.remove('hidden');
                        resetPwdSection.classList.add('fade-in');
                    }, 500);
                } else {
                    authMessage.innerText = data.message || '인증 코드가 유효하지 않습니다.';
                    authMessage.style.color = 'red';
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('알 수 없는 오류가 발생했습니다.');
            });
    });

    // [Step 3] 새 비밀번호 설정
    resetPwdForm.addEventListener('submit', function (e) {
        e.preventDefault();

        const newPassword = document.getElementById('newPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        // (클라이언트 측) 비밀번호 확인 로직
        if (!newPassword || !confirmPassword) {
            alert('비밀번호를 입력하세요.');
            return;
        }
        if (newPassword !== confirmPassword) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }
        if (!verifiedEmail) {
            // 혹시 모르니 이메일이 없는 경우 방어
            alert('이메일 정보가 없습니다. 인증 과정을 다시 진행해주세요.');
            return;
        }

        // 최종 비밀번호 재설정 요청
        fetch("/auth/reset-password", {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: verifiedEmail,
                newPassword: newPassword
            })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert(data.message || '비밀번호 재설정 성공!');
                    // 필요 시 로그인 페이지로 이동 or 안내
                    window.location.href = '/user/home';
                } else {
                    alert(data.message || '비밀번호 재설정 실패');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('알 수 없는 오류가 발생했습니다.');
            });
    });
});
