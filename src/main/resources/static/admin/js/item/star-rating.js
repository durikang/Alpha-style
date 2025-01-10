document.addEventListener("DOMContentLoaded", function () {
    const stars = document.querySelectorAll("#star-rating .star");
    const ratingInput = document.getElementById("rating");
    let currentRating = 0; // 현재 선택된 별점

    const commentInput = document.getElementById('comment');
    const charCount = document.getElementById('char-count');

    // 별점 채우기 (마우스 오버/클릭 공통)
    function fillStars(value) {
        stars.forEach((star, index) => {
            star.classList.remove("filled-left", "filled-right");
            if (index + 1 <= value) {
                star.classList.add("filled-right");
            } else if (index + 0.5 === value) {
                star.classList.add("filled-left");
            }
        });
    }

    // 마우스 오버 시 별점 반영
    stars.forEach((star, index) => {
        star.addEventListener("mousemove", (e) => {
            const rect = star.getBoundingClientRect();
            const offsetX = e.clientX - rect.left; // 마우스 위치
            const width = rect.width; // 별의 전체 너비

            // 반개/전체 계산
            const value = index + 1 + (offsetX < width / 2 ? -0.5 : 0);
            fillStars(value);
        });

        // 마우스 아웃 시 마지막 선택 값으로 복구
        star.addEventListener("mouseleave", () => {
            fillStars(currentRating);
        });

        // 클릭 시 별점 고정
        star.addEventListener("click", (e) => {
            const rect = star.getBoundingClientRect();
            const offsetX = e.clientX - rect.left;
            const width = rect.width;

            // 선택된 별점 저장
            currentRating = index + 1 + (offsetX < width / 2 ? -0.5 : 0);
            ratingInput.value = currentRating;

            fillStars(currentRating);
        });
    });

    // 초기 상태 유지 (별점 0점)
    fillStars(currentRating);


    commentInput.addEventListener('input', () => {
        charCount.textContent = `${commentInput.value.length}/3000`;
    });

});
