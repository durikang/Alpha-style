// src/main/resources/static/js/star-rating.js

document.addEventListener("DOMContentLoaded", function () {
    const stars = document.querySelectorAll("#star-rating .star");
    const ratingInput = document.getElementById("rating");

    stars.forEach(star => {
        star.addEventListener("click", function () {
            const rating = this.getAttribute("data-value");
            ratingInput.value = rating;
            stars.forEach(s => {
                if (s.getAttribute("data-value") <= rating) {
                    s.style.color = "#ffc107";
                } else {
                    s.style.color = "#ddd";
                }
            });
        });

        star.addEventListener("mouseover", function () {
            const hoverValue = this.getAttribute("data-value");
            stars.forEach(s => {
                if (s.getAttribute("data-value") <= hoverValue) {
                    s.style.color = "#ffc107";
                } else {
                    s.style.color = "#ddd";
                }
            });
        });

        star.addEventListener("mouseout", function () {
            const currentRating = ratingInput.value;
            stars.forEach(s => {
                if (s.getAttribute("data-value") <= currentRating) {
                    s.style.color = "#ffc107";
                } else {
                    s.style.color = "#ddd";
                }
            });
        });
    });
});
