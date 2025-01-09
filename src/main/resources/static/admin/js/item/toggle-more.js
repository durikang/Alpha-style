document.addEventListener("DOMContentLoaded", function () {
    const toggleBtn = document.querySelector(".toggle-more-btn");
    const additionalInfo = document.querySelector(".additional-info");

    toggleBtn.addEventListener("click", function () {
        additionalInfo.classList.toggle("hidden");
        toggleBtn.textContent = additionalInfo.classList.contains("hidden") ? "더보기" : "닫기";
    });
});
