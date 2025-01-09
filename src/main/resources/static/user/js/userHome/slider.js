document.addEventListener("DOMContentLoaded", function () {
    console.log("DOM fully loaded and parsed");

    const slides = document.querySelectorAll(".slider .slide");
    const prevButton = document.querySelector(".slider .prev");
    const nextButton = document.querySelector(".slider .next");
    let currentSlide = 0;
    const totalSlides = slides.length;

    console.log("Total slides found:", totalSlides);
    console.log("Prev button found:", !!prevButton);
    console.log("Next button found:", !!nextButton);

    function showSlide(index) {
        console.log(`Switching to slide ${index}`);
        slides.forEach((slide, i) => {
            if (i === index) {
                slide.classList.add("active");
                console.log(`Slide ${i} is now active`);
            } else {
                slide.classList.remove("active");
                console.log(`Slide ${i} is now inactive`);
            }
        });
    }

    function nextSlide() {
        currentSlide = (currentSlide + 1) % totalSlides;
        console.log(`Next button clicked. Moving to slide ${currentSlide}`);
        showSlide(currentSlide);
    }

    function prevSlide() {
        currentSlide = (currentSlide - 1 + totalSlides) % totalSlides;
        console.log(`Prev button clicked. Moving to slide ${currentSlide}`);
        showSlide(currentSlide);
    }

    // 초기 슬라이드 표시
    if (totalSlides > 0) {
        console.log("Initializing slider with first slide");
        showSlide(currentSlide);

        // 자동 슬라이드
        setInterval(() => {
            console.log("Auto slide triggered");
            nextSlide();
        }, 5000);
    } else {
        console.log("No slides found, slider not initialized");
    }

    // 버튼 클릭 이벤트
    if (nextButton) {
        nextButton.addEventListener("click", () => {
            console.log("Next button clicked");
            nextSlide();
        });
    } else {
        console.log("Next button not found");
    }

    if (prevButton) {
        prevButton.addEventListener("click", () => {
            console.log("Prev button clicked");
            prevSlide();
        });
    } else {
        console.log("Prev button not found");
    }
});
