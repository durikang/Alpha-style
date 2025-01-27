document.addEventListener("DOMContentLoaded", () => {
    const images = [
        "/admin/img/background1.jpg",
        "/admin/img/background2.jpg",
        "/admin/img/background3.jpg",
        "/admin/img/background4.jpg",
        "/admin/img/background5.jpg"
    ];
    let index = 0;

    const body = document.body;

    body.style.backgroundImage = `url(${images[0]})`; // 초기 배경 설정
    body.style.transition = "background-image 1s ease-in-out, opacity 0.5s ease-in-out";

    function changeBackground() {
        body.style.opacity = "0"; // 페이드 아웃
        setTimeout(() => {
            body.style.backgroundImage = `url(${images[index]})`;
            body.style.opacity = "1"; // 페이드 인
            index = (index + 1) % images.length;
        }, 500);
    }

    setInterval(changeBackground, 5000);
    changeBackground();
});
