document.addEventListener('DOMContentLoaded', () => {
    // Bootstrap 모달
    const uploadModal = new bootstrap.Modal(document.getElementById('uploadModal'), { backdrop: 'static' });

    // 모달 버튼 / 폼 요소
    const uploadButton = document.getElementById('uploadModalButton');   // 업로드 모달 오픈용 버튼(페이지 어딘가에 있다고 가정)
    const closeModalButton = document.getElementById('closeModalButton'); // 모달 헤더의 X 버튼
    const cancelButton = document.getElementById('cancelButton');
    const uploadForm = document.getElementById('uploadForm');

    // 진행 표시 텍스트 & Bootstrap progress bar
    const progressText = document.getElementById('progressText');
    const bootstrapProgressBar = document.getElementById('bootstrapProgressBar');

    // 서버 URL
    const BASE_URL = 'http://localhost:5000';

    let eventSource;   // SSE EventSource 객체
    let isUploading = false;  // 업로드 진행 중 여부

    // 에러 출력 + progress bar 초기화
    function logError(message) {
        console.error(message);

        // 텍스트 표시 (빨간색)
        progressText.classList.remove('text-success');
        progressText.classList.add('text-danger');
        progressText.textContent = `업로드 중 문제가 발생했습니다: ${message}`;

        // progress bar 문구
        bootstrapProgressBar.style.width = '0%';
        bootstrapProgressBar.textContent = '오류 발생';

        // SSE 닫기
        if (eventSource) {
            eventSource.close();
        }
        // 업로드 종료로 간주
        isUploading = false;
    }

    // 업로드 UI 초기화
    function resetUI() {
        // 업로드 진행중 플래그 OFF
        isUploading = false;

        // 텍스트 색상 초기화
        progressText.classList.remove('text-danger', 'text-success');
        progressText.textContent = '0%';

        // progress bar 초기화
        bootstrapProgressBar.style.width = '0%';
        bootstrapProgressBar.textContent = '0%';
    }

    // progress bar 업데이트
    function updateBootstrapBar(percentage, displayText = '') {
        bootstrapProgressBar.style.width = percentage + '%';
        bootstrapProgressBar.setAttribute('aria-valuenow', percentage);
        bootstrapProgressBar.textContent = displayText || `${percentage}%`;
    }

    // SSE 시작
    function startProgressStream() {
        // 이미 연결된 SSE가 있다면 닫기
        if (eventSource) {
            eventSource.close();
        }
        eventSource = new EventSource(`${BASE_URL}/progress`);
        isUploading = true;  // SSE 연결 시 업로드 진행중 플래그 ON

        eventSource.onmessage = (event) => {
            const msg = event.data;
            console.log("SSE:", msg);

            // 정규식 "파일 x/y 업로드"
            const match = msg.match(/파일\s+(\d+)\/(\d+)\s+업로드/);
            if (match) {
                const current = parseInt(match[1], 10);
                const total = parseInt(match[2], 10);
                const percentage = Math.round((current / total) * 100);

                progressText.classList.remove('text-danger');
                progressText.classList.remove('text-success');
                progressText.textContent = `파일 ${current}/${total} 업로드 중... (${percentage}%)`;
                updateBootstrapBar(percentage, `파일 ${current}/${total} 업로드 중... (${percentage}%)`);

            } else if (msg === "데이터 병합 중...") {
                progressText.classList.remove('text-danger', 'text-success');
                progressText.textContent = "데이터 병합 중...(80%)";
                updateBootstrapBar(80, "데이터 병합 중... (80%)");

            } else if (msg === "파일 병합 완료.") {
                // 업로드 완료
                progressText.classList.remove('text-danger');
                progressText.classList.add('text-success');
                progressText.textContent = "파일 병합 완료! (100%)";
                updateBootstrapBar(100, "파일 병합 완료! (100%)");

                // 업로드 완료 -> alert
                alert("업로드가 완료되었습니다!");

                // SSE 닫기
                if (eventSource) {
                    eventSource.close();
                }
                isUploading = false;

            } else if (msg.includes("취소")) {
                // 취소됨
                progressText.textContent = "업로드가 취소되었습니다.";
                updateBootstrapBar(0, "업로드 취소됨.");

                if (eventSource) {
                    eventSource.close();
                }
                isUploading = false;

            } else if (msg.includes("에러 발생") || msg.includes("오류")) {
                // 오류
                progressText.classList.remove('text-success');
                progressText.classList.add('text-danger');
                progressText.textContent = msg;
                updateBootstrapBar(0, "오류 발생.");

                if (eventSource) {
                    eventSource.close();
                }
                isUploading = false;
            }
        };

        eventSource.onerror = (err) => {
            console.error("SSE Error:", err);
            logError("SSE 연결 에러");
        };
    }

    // 업로드 모달 열기 (버튼 클릭)
    uploadButton.addEventListener('click', () => {
        resetUI();
        uploadModal.show();
    });

    // 업로드 폼 제출
    uploadForm.addEventListener('submit', (event) => {
        event.preventDefault();
        resetUI();

        // 업로드 시작
        const formData = new FormData(uploadForm);
        startProgressStream();

        fetch(uploadForm.action, { method: 'POST', body: formData })
            .then(response => response.json())
            .then(data => {
                if (data.status === "started") {
                    // 초기 메시지 (ex: "파일 업로드가 완료되었습니다. 백그라운드에서 병합을 진행합니다...")
                    progressText.classList.remove('text-danger');
                    progressText.classList.remove('text-success');
                    progressText.textContent = data.message;
                    updateBootstrapBar(0, data.message);
                } else {
                    progressText.textContent = data.message;
                    logError(data.message);
                }
            })
            .catch(error => {
                logError('업로드 중 오류가 발생했습니다: ' + error);
            });
    });

    // 취소 버튼
    cancelButton.addEventListener('click', () => {
        if (eventSource) {
            fetch(`${BASE_URL}/cancel`, { method: 'POST' })
                .then(() => {
                    progressText.textContent = '업로드가 취소 요청되었습니다.';
                    updateBootstrapBar(0, "취소 요청됨.");
                    eventSource.close();
                    isUploading = false;
                    uploadModal.hide();
                })
                .catch(error => logError('취소 요청 중 에러 발생: ' + error));
        }
    });

    // 닫기(X) 버튼
    closeModalButton.addEventListener('click', () => {
        // 업로드 중이라면 닫기 불가
        if (isUploading) {
            alert("파일 업로드 중에는 닫을 수 없습니다.");
        } else {
            // 업로드가 끝났으면 닫기 허용
            uploadModal.hide();
        }
    });
});
