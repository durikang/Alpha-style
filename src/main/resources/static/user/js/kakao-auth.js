document.getElementById('startKakaoAuthBtn').addEventListener('click', function () {
    const userId = localStorage.getItem('userId'); // 서버에서 받은 userId
    const accessToken = localStorage.getItem('accessToken'); // 서버에서 받은 accessToken

    fetch('/kakao/send-code', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: userId, accessToken: accessToken })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('카카오톡으로 인증 코드가 전송되었습니다.');
            } else {
                alert('카카오 인증 코드 전송 실패: ' + data.message);
            }
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('verifyKakaoAuthCodeBtn').addEventListener('click', function () {
    const code = document.getElementById('kakaoAuthCode').value;
    const userId = localStorage.getItem('userId'); // 저장된 userId 가져오기

    // 인증 코드 확인 요청 (백엔드 호출)
    fetch('/kakao/verify-code', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId: userId, authCode: code })
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                document.getElementById('kakaoAuthMessage').innerText = '카카오 인증 성공!';
                document.getElementById('kakaoAuthMessage').style.color = 'green';
            } else {
                document.getElementById('kakaoAuthMessage').innerText = '카카오 인증 실패: ' + data.message;
            }
        })
        .catch(error => console.error('Error:', error));
});

