document.addEventListener('DOMContentLoaded', function () {
    const emailForm = document.getElementById('findPwdForm');
    const authCodeSection = document.getElementById('authCodeSection');
    const resetPwdSection = document.getElementById('resetPwdSection');
    const verifyAuthCodeBtn = document.getElementById('verifyAuthCodeBtn');
    const authMessage = document.getElementById('authMessage');

    // Step 1: 이메일 입력 후 검증 및 인증 코드 요청
    emailForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const email = document.getElementById('email').value;

        // 이메일 검증 AJAX 요청
        fetch('/user/member/find/send-email', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('이메일로 인증 코드가 전송되었습니다.');

                    // Step 2: 인증 코드 입력 섹션 표시
                    emailForm.classList.add('fade-out');
                    setTimeout(() => {
                        emailForm.classList.add('hidden');
                        authCodeSection.classList.remove('hidden');
                        authCodeSection.classList.add('fade-in');
                    }, 500);
                } else {
                    alert('이메일 검증 실패: ' + data.message);
                }
            })
            .catch(error => console.error('Error:', error));
    });

    // Step 2: 인증 코드 확인
    verifyAuthCodeBtn.addEventListener('click', function () {
        const authCode = document.getElementById('authCode').value;

        // 인증 코드 확인 요청
        fetch('/user/member/find/verify-code', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ authCode })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('인증 코드가 확인되었습니다.');

                    // Step 3: 새 비밀번호 설정 섹션 표시
                    authCodeSection.classList.add('fade-out');
                    setTimeout(() => {
                        authCodeSection.classList.add('hidden');
                        resetPwdSection.classList.remove('hidden');
                        resetPwdSection.classList.add('fade-in');
                    }, 500);
                } else {
                    authMessage.innerText = '인증 코드가 유효하지 않습니다.';
                    authMessage.style.color = 'red';
                }
            })
            .catch(error => console.error('Error:', error));
    });
});
