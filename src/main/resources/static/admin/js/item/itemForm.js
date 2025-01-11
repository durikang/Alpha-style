document.addEventListener('DOMContentLoaded', () => {
    const MAX_THUMBNAILS = 4;

    const setupImageUpload = (inputId, previewId) => {
        const input = document.getElementById(inputId);
        const preview = document.getElementById(previewId);

        if (!input || !preview) {
            console.warn(`Element not found: inputId=${inputId}, previewId=${previewId}`);
            return;
        }

        const setImage = (file) => {
            const reader = new FileReader();
            reader.onload = (e) => {
                preview.querySelector('img').src = e.target.result;
            };
            reader.readAsDataURL(file);
        };

        // 파일 선택 이벤트
        input.addEventListener('change', () => {
            const file = input.files[0];
            if (file) setImage(file);
        });

        // 이미지 클릭 시 파일 업로드 트리거
        preview.addEventListener('click', () => {
            input.click();
        });

        // 드래그 앤 드롭 이벤트
        preview.addEventListener('dragover', (e) => {
            e.preventDefault();
            preview.classList.add('dragover');
        });

        preview.addEventListener('dragleave', () => {
            preview.classList.remove('dragover');
        });

        preview.addEventListener('drop', (e) => {
            e.preventDefault();
            preview.classList.remove('dragover');
            const file = e.dataTransfer.files[0];
            if (file) {
                setImage(file);

                const dataTransfer = new DataTransfer();
                dataTransfer.items.add(file);
                input.files = dataTransfer.files;
            }
        });
    };

    // 메인 이미지 설정
    setupImageUpload('mainImageInput', 'mainImagePreview');

    // 썸네일 설정
    for (let i = 1; i <= MAX_THUMBNAILS; i++) {
        setupImageUpload(`thumbnailInput${i}`, `thumb${i}`);
    }

    // 썸네일 업로드 버튼
    const uploadButton = document.getElementById('thumbnailUploadButton');
    uploadButton.addEventListener('click', () => {
        const input = document.createElement('input');
        input.type = 'file';
        input.accept = 'image/*';
        input.multiple = true;

        input.addEventListener('change', () => {
            const files = Array.from(input.files).slice(0, MAX_THUMBNAILS);
            files.forEach((file, index) => {
                const preview = document.getElementById(`thumb${index + 1}`);
                const thumbnailInput = document.getElementById(`thumbnailInput${index + 1}`);

                if (file && preview && thumbnailInput) {
                    const reader = new FileReader();
                    reader.onload = (e) => {
                        preview.querySelector('img').src = e.target.result;
                    };
                    reader.readAsDataURL(file);

                    const dataTransfer = new DataTransfer();
                    dataTransfer.items.add(file);
                    thumbnailInput.files = dataTransfer.files;
                }
            });
        });

        input.click();
    });
});
