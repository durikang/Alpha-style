const slides = document.querySelectorAll('.slide');
const prevButton = document.querySelector('.prev');
const nextButton = document.querySelector('.next');

let currentIndex = 0;

// 슬라이드를 활성화하는 함수
function showSlide(index) {
    slides.forEach((slide, i) => {
        if (i === index) {
            slide.classList.add('active');
        } else {
            slide.classList.remove('active');
        }
    });
}

// 다음 슬라이드로 이동
function nextSlide() {
    currentIndex = (currentIndex + 1) % slides.length;
    showSlide(currentIndex);
}

// 이전 슬라이드로 이동
function prevSlide() {
    currentIndex = (currentIndex - 1 + slides.length) % slides.length;
    showSlide(currentIndex);
}

// 버튼 이벤트
nextButton.addEventListener('click', nextSlide);
prevButton.addEventListener('click', prevSlide);

// 자동 슬라이드 이동
setInterval(nextSlide, 5000);

// 초기 슬라이드 표시
showSlide(currentIndex);
