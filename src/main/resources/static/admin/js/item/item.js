document.addEventListener("DOMContentLoaded", function () {
    const mainImage = document.querySelector(".main-image");
    const thumbnails = document.querySelectorAll(".thumbnail");

    let currentIndex = 0;

    function updateMainImage() {
        thumbnails.forEach(thumbnail => thumbnail.classList.remove("active"));
        const currentThumbnail = thumbnails[currentIndex];
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