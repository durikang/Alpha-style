document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById("file-settings-form");
    const maxFileSizeInput = document.getElementById("maxFileSizeMB");
    const MAX_ALLOWED_SIZE_MB = 200; // 최대 설정 가능 크기

    form.addEventListener("submit", function(event) {
        const maxFileSize = parseInt(maxFileSizeInput.value, 10);

        if (isNaN(maxFileSize) || maxFileSize < 1) {
            alert("최대 파일 크기는 최소 1MB 이상이어야 합니다.");
            event.preventDefault();
            return;
        }

        if (maxFileSize > MAX_ALLOWED_SIZE_MB) {
            alert(`최대 파일 크기는 ${MAX_ALLOWED_SIZE_MB}MB를 초과할 수 없습니다.`);
            event.preventDefault();
        }
    });
});
