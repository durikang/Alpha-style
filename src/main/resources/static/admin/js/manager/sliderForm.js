document.addEventListener("DOMContentLoaded", function() {
    const imageInput = document.getElementById("image");
    const maxFileSizeMB = imageInput.getAttribute("data-max-size");
    const maxFileSizeBytes = maxFileSizeMB * 1024 * 1024;

    imageInput.addEventListener("change", function() {
        const file = this.files[0];
        if (file && file.size > maxFileSizeBytes) {
            alert(`파일 크기가 너무 큽니다. 업로드 가능한 최대 크기는 ${maxFileSizeMB}MB 입니다.`);
            this.value = ""; // 파일 선택 초기화
        }
    });
});
