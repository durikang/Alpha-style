document.addEventListener('DOMContentLoaded', () => {
    const authButton = document.getElementById('authButton');
    const authModal = document.getElementById('authModal');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const emailAuthBtn = document.getElementById('emailAuthBtn');
    const kakaoAuthBtn = document.getElementById('kakaoAuthBtn');
    const emailAuthDiv = document.getElementById('emailAuthDiv');
    const kakaoAuthDiv = document.getElementById('kakaoAuthDiv');

    authButton.addEventListener('click', () => { authModal.style.display = 'block'; });
    closeModalBtn.addEventListener('click', () => { authModal.style.display = 'none'; });
    emailAuthBtn.addEventListener('click', () => {
        authModal.style.display = 'none';
        emailAuthDiv.style.display = 'block';
        kakaoAuthDiv.style.display = 'none';
    });
    kakaoAuthBtn.addEventListener('click', () => {
        authModal.style.display = 'none';
        emailAuthDiv.style.display = 'none';
        kakaoAuthDiv.style.display = 'block';
    });
});
