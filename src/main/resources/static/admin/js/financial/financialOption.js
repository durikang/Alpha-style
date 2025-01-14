document.addEventListener('DOMContentLoaded', () => {
    const progressModal = document.getElementById('progressModal');
    const uploadProgress = document.getElementById('uploadProgress');
    const progressText = document.getElementById('progressText');
    const cancelButton = document.getElementById('cancelButton');
    const insertForm = document.getElementById('insertForm');

    let eventSource;

    // 서버 기본 URL 설정
    const BASE_URL = 'http://localhost:5000';

    /**
     * 오류 로그 및 모달 처리
     * @param {string} message - 오류 메시지
     */
    function logError(message) {
        console.error(message);
        progressText.textContent = '작업 중 문제가 발생했습니다: ' + message;
        if (eventSource) {
            eventSource.close();
            progressModal.classList.remove('active'); // 모달 닫기
        }
    }

    /**
     * 작업 취소 처리
     */
    cancelButton.addEventListener('click', () => {
        if (eventSource) {
            fetch(`${BASE_URL}/cancel`, { method: 'POST' })
                .then(() => {
                    progressText.textContent = '작업이 취소되었습니다.';
                    uploadProgress.value = 0;
                    eventSource.close();
                    progressModal.classList.remove('active'); // 모달 닫기
                })
                .catch((error) => logError('취소 요청 중 에러 발생: ' + error));
        }
    });

    /**
     * 데이터 삽입 작업 시작 처리
     */
    insertForm.addEventListener('submit', (e) => {
        e.preventDefault(); // 폼 기본 동작 방지
        progressModal.classList.add('active'); // 모달 열기
        resetUI();

        try {
            // SSE 연결 시작
            eventSource = new EventSource(`${BASE_URL}/insert`);

            // SSE 메시지 수신 처리
            eventSource.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data);
                    uploadProgress.value = data.progress;
                    progressText.textContent = `${data.message} (${data.progress}%)`;

                    // 작업 완료 또는 취소 시 SSE 연결 종료
                    if (data.progress === 100 || data.status === 'cancelled') {
                        eventSource.close();
                        setTimeout(() => {
                            progressModal.classList.remove('active'); // 모달 닫기
                        }, 2000); // 2초 후 모달 닫기
                    }
                } catch (error) {
                    logError('SSE 데이터 처리 중 에러 발생: ' + error);
                }
            };

            // SSE 연결 오류 처리
            eventSource.onerror = () => {
                logError('SSE 연결 에러 발생');
            };
        } catch (error) {
            logError('EventSource 초기화 중 에러 발생: ' + error);
        }
    });

    /**
     * UI 초기화
     */
    function resetUI() {
        uploadProgress.value = 0;
        progressText.textContent = '0%';
    }
});
