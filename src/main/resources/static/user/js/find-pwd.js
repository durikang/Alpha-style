document.addEventListener('DOMContentLoaded', function () {
    const findPwdForm = document.getElementById('findPwdForm');
    const authCodeSection = document.getElementById('authCodeSection');
    const resetPwdSection = document.getElementById('resetPwdSection');
    const verifyAuthCodeBtn = document.getElementById('verifyAuthCodeBtn');
    const authMessage = document.getElementById('authMessage');

    // 인증 코드 전송 폼 제출
    findPwdForm.addEventListener('submit', function (e) {
        e.preventDefault();
        const email = document.getElementById('email').value;

        // 이메일 전송 요청
        fetch('/user/member/find/send-email', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('이메일로 인증 코드가 전송되었습니다.');
                    authCodeSection.classList.remove('hidden');
                } else {
                    alert('이메일 전송 실패: ' + data.message);
                }
            })
            .catch(error => console.error('Error:', error));
    });

    // 인증 코드 확인
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
                    resetPwdSection.classList.remove('hidden');
                } else {
                    authMessage.innerText = '인증 코드가 유효하지 않습니다.';
                    authMessage.style.color = 'red';
                }
            })
            .catch(error => console.error('Error:', error));
    });
});
