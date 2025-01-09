// src/main/resources/static/js/star-rating.js

document.addEventListener("DOMContentLoaded", function () {
    // 별점 선택 기능
    const stars = document.querySelectorAll(".add-review #star-rating .star");
    const ratingInput = document.getElementById("rating");

    stars.forEach(star => {
        star.addEventListener("click", () => {
            const rating = star.getAttribute("data-value");
            ratingInput.value = rating;

            stars.forEach(s => {
                if (s.getAttribute("data-value") <= rating) {
                    s.classList.add("selected");
                } else {
                    s.classList.remove("selected");
                }
            });
        });

        star.addEventListener("mouseover", () => {
            const rating = star.getAttribute("data-value");
            stars.forEach(s => {
                if (s.getAttribute("data-value") <= rating) {
                    s.classList.add("hover");
                } else {
                    s.classList.remove("hover");
                }
            });
        });

        star.addEventListener("mouseout", () => {
            stars.forEach(s => {
                s.classList.remove("hover");
            });
        });
    });
});
