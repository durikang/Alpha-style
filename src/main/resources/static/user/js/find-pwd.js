document.addEventListener('DOMContentLoaded', function () {
    const findPwdForm = document.getElementById('findPwdForm');
    const authCodeSection = document.getElementById('authCodeSection');
    const resetPwdSection = document.getElementById('resetPwdSection');
    const verifyAuthCodeBtn = document.getElementById('verifyAuthCodeBtn');
    const authMessage = document.getElementById('authMessage');

    // 인증 코드 전송 폼 제출
    findPwdForm.addEventListener('submit', function (e) {
        e.preventDefault(); // 기본 폼 제출 방지

        const email = document.getElementById('email').value;

        fetch('/user/member/find/send-email', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ email: email }),
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(data => {
                        throw new Error(data.message || '이메일 전송 실패');
                    });
                }
                return response.json();
            })
            .then(data => {
                alert(data.message);
                document.getElementById('authCodeSection').classList.remove('hidden');
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message || '알 수 없는 오류가 발생했습니다.');
            });
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
