document.addEventListener('DOMContentLoaded', () => {
    const progressModalElement = document.getElementById('optionModal');
    const progressModal = bootstrap.Modal.getInstance(progressModalElement) || new bootstrap.Modal(progressModalElement, { backdrop: 'static' });

    // 폼들
    const insertForm = document.getElementById('insertForm');
    const analysisForm = document.getElementById('analysisForm');

    // 버튼/바
    const optionCloseButton = document.getElementById('optionCloseButton'); // 헤더 X 버튼
    const optionCloseButton2 = document.getElementById('optionCloseButton2'); // 바디 닫기 버튼
    const optionCancelButton = document.getElementById('optionCancelButton');

    // Progress bar & 텍스트
    const optionProgressBar = document.getElementById('optionProgressBar');
    const optionProgressText = document.getElementById('optionProgressText');

    const BASE_URL = 'http://localhost:5000';
    let eventSource = null;
    let isProcessing = false;

    // 공통: 에러 처리
    function logError(msg) {
        console.error("OptionModal Error:", msg);
        optionProgressText.classList.remove('text-success');
        optionProgressText.classList.add('text-danger');
        optionProgressText.textContent = `오류 발생: ${msg}`;

        optionProgressBar.style.width = '0%';
        optionProgressBar.textContent = '오류 발생';

        if (eventSource) {
            eventSource.close();
        }
        isProcessing = false;
    }

    // 공통: UI 초기화
    function resetUI() {
        isProcessing = false;
        optionProgressText.classList.remove('text-danger', 'text-success');
        optionProgressText.textContent = '0%';

        optionProgressBar.style.width = '0%';
        optionProgressBar.textContent = '0%';
    }

    // 공통: Progress bar 업데이트
    function updateOptionBar(percentage, text = '') {
        optionProgressBar.style.width = percentage + '%';
        optionProgressBar.setAttribute('aria-valuenow', percentage);
        optionProgressBar.textContent = text || `${percentage}%`;
    }

    // 공통: SSE 시작
    function startProgressSSE(url) {
        if (eventSource) {
            console.warn("이전 SSE 연결 종료");
            eventSource.close();
        }
        eventSource = new EventSource(url);
        isProcessing = true;

        eventSource.onmessage = (evt) => {
            const msg = evt.data;
            console.log("SSE Message:", msg);

            let parsed = null;
            try {
                parsed = JSON.parse(msg); // {progress, message}
            } catch (e) {
                // JSON 파싱 실패 시 문자열로 처리
            }

            if (parsed && parsed.progress !== undefined) {
                const { progress, message } = parsed;
                optionProgressText.textContent = `${message} (${progress}%)`;
                updateOptionBar(progress, `${message} (${progress}%)`);

                if (progress === 100) {
                    optionProgressText.classList.remove('text-danger');
                    optionProgressText.classList.add('text-success');
                    alert("작업이 완료되었습니다!");
                    eventSource.close();
                    isProcessing = false;
                }
            } else {
                // 기타 메시지 처리
                if (msg.includes("오류") || msg.includes("에러")) {
                    logError(msg);
                } else if (msg.includes("완료")) {
                    optionProgressText.textContent = msg + " (100%)";
                    updateOptionBar(100, msg + " (100%)");
                    alert("작업이 완료되었습니다!");
                    eventSource.close();
                    isProcessing = false;
                } else {
                    optionProgressText.textContent = msg;
                }
            }
        };

        eventSource.onerror = (err) => {
            console.error("SSE Error:", err);
            if (isProcessing) logError("SSE 연결 에러");
        };
    }

    // 공통: 닫기 버튼 로직
    function closeModalIfNotProcessing(modal) {
        if (isProcessing) {
            alert("진행 중인 작업이 있어 모달을 닫을 수 없습니다.");
        } else if (modal) {
            modal.hide();
        }
    }

    // ========== DB 삽입 ==========
    insertForm.addEventListener('submit', (e) => {
        e.preventDefault();

        resetUI();
        fetch(`${BASE_URL}/insert`, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.message === "데이터 삽입 작업이 시작되었습니다.") {
                    progressModal.show();
                    startProgressSSE(`${BASE_URL}/insert`);
                } else {
                    logError(data.message);
                }
            })
            .catch(err => logError("DB 삽입 요청 중 오류: " + err));
    });

    // ========== 분석하기 ==========
    analysisForm.addEventListener('submit', (e) => {
        e.preventDefault();

        resetUI();
        fetch(`${BASE_URL}/analysis`, { method: 'POST' })
            .then(res => res.json())
            .then(data => {
                if (data.message === "분석 작업이 시작되었습니다.") {
                    progressModal.show();
                    startProgressSSE(`${BASE_URL}/analysis`);
                } else {
                    logError(data.message || data.error);
                }
            })
            .catch(err => logError("분석 요청 중 오류: " + err));
    });

    // 취소 버튼
    optionCancelButton.addEventListener('click', () => {
        if (eventSource) {
            fetch(`${BASE_URL}/cancel`, { method: 'POST' })
                .then(() => {
                    optionProgressText.textContent = '작업이 취소되었습니다.';
                    updateOptionBar(0, "취소됨");
                    eventSource.close();
                    isProcessing = false;
                    progressModal.hide();
                })
                .catch(err => logError("취소 요청 중 오류: " + err));
        } else {
            progressModal.hide();
        }
    });

    // 닫기(X) 버튼
    optionCloseButton.addEventListener('click', () => closeModalIfNotProcessing(progressModal));

    // 닫기 버튼(아래)
    optionCloseButton2.addEventListener('click', () => closeModalIfNotProcessing(progressModal));
});
