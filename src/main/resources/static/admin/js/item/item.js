document.addEventListener("DOMContentLoaded", function () {
    const mainImage = document.querySelector(".main-image");
    const thumbnails = document.querySelectorAll(".thumbnail");

    let currentIndex = 0;

    function updateMainImage() {
        if (thumbnails.length === 0) return; // 썸네일이 없는 경우 바로 종료
        thumbnails.forEach(thumbnail => thumbnail.classList.remove("active"));
        const currentThumbnail = thumbnails[currentIndex];
        if (!currentThumbnail) return; // currentThumbnail이 undefined일 경우 종료
        mainImage.src = currentThumbnail.src;
        currentThumbnail.classList.add("active");
        currentIndex = (currentIndex + 1) % thumbnails.length;
    }

    setInterval(updateMainImage, 3000);

    thumbnails.forEach((thumbnail, index) => {
        thumbnail.addEventListener("click", () => {
            currentIndex = index;
            updateMainImage();
        });
    });
});