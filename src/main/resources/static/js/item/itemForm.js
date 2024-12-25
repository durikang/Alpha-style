document.addEventListener('DOMContentLoaded', () => {
    const setupImageUpload = (inputId, previewId, placeholder) => {
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

        // File input change event
        input.addEventListener('change', function () {
            const file = this.files[0];
            if (file) setImage(file);
        });

        // Drag-and-drop events
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

                // Sync with input field
                const dataTransfer = new DataTransfer();
                dataTransfer.items.add(file);
                input.files = dataTransfer.files;
            }
        });

        // Set placeholder image initially
        const img = preview.querySelector('img');
        if (img && placeholder) {
            img.src = placeholder;
        }
    };

    // Main image setup
    setupImageUpload(
        'mainImageInput',
        'mainImagePreview',
        'https://via.placeholder.com/400x500.png?text=Main+Image'
    );

    // Thumbnail image setup
    ['1', '2', '3', '4'].forEach((num) => {
        setupImageUpload(
            `thumbnailInput${num}`,
            `thumb${num}Preview`,
            `https://via.placeholder.com/80x100.png?text=Thumbnail+${num}`
        );
    });

    // Add thumbnail upload button
    const createUploadButton = () => {
        const uploadButton = document.createElement('button');
        uploadButton.textContent = '썸네일 업로드';
        uploadButton.className = 'upload-btn';

        // 버튼 타입을 명시적으로 "button"으로 설정
        uploadButton.type = 'button';

        uploadButton.addEventListener('click', () => {
            const input = document.createElement('input');
            input.type = 'file';
            input.accept = 'image/*';
            input.multiple = true;

            input.addEventListener('change', () => {
                const files = Array.from(input.files).slice(0, 4); // Limit to 4 files
                files.forEach((file, i) => {
                    const inputField = document.getElementById(`thumbnailInput${i + 1}`);
                    const preview = document.getElementById(`thumb${i + 1}Preview`);

                    if (file && inputField && preview) {
                        const reader = new FileReader();
                        reader.onload = (e) => {
                            preview.querySelector('img').src = e.target.result;
                        };
                        reader.readAsDataURL(file);

                        // Sync with hidden file input
                        const dataTransfer = new DataTransfer();
                        dataTransfer.items.add(file);
                        inputField.files = dataTransfer.files;
                    }
                });
            });

            input.click();
        });

        return uploadButton;
    };


    // Append upload button near gallery
    const gallery = document.querySelector('.thumbnail-gallery');
    const submitButton = document.querySelector('.btn-lg');
    if (gallery && submitButton) {
        const uploadButton = createUploadButton();
        submitButton.parentElement.insertBefore(uploadButton, submitButton); // Add button above submit
    }
});
