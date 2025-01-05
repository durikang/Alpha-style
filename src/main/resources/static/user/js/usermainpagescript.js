document.addEventListener("DOMContentLoaded", function () {
    const slides = document.querySelectorAll(".slider .slide");
    const prevButton = document.querySelector(".slider .prev");
    const nextButton = document.querySelector(".slider .next");
    let currentSlide = 0;
    const totalSlides = slides.length;

    function showSlide(index) {
        slides.forEach((slide, i) => {
            if (i === index) {
                slide.classList.add("active");
            } else {
                slide.classList.remove("active");
            }
        });
    }

    function nextSlide() {
        currentSlide = (currentSlide + 1) % totalSlides;
        showSlide(currentSlide);
    }

    function prevSlide() {
        currentSlide = (currentSlide - 1 + totalSlides) % totalSlides;
        showSlide(currentSlide);
    }

    // 초기 슬라이드 표시
    if (totalSlides > 0) {
        showSlide(currentSlide);
        // 자동 슬라이드
        setInterval(nextSlide, 5000); // 5초마다 슬라이드 변경
    }

    // 버튼 클릭 이벤트
    if (nextButton) {
        nextButton.addEventListener("click", nextSlide);
    }

    if (prevButton) {
        prevButton.addEventListener("click", prevSlide);
    }
});
