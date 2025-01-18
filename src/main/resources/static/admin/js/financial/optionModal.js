document.addEventListener('DOMContentLoaded', () => {
    const progressModal = new bootstrap.Modal(document.getElementById('optionModal'), { backdrop: 'static' });

    // 폼들
    const insertForm = document.getElementById('insertForm');
    const analysisForm = document.getElementById('analysisForm');

    // 버튼/바
    const optionCloseButton = document.getElementById('optionCloseButton');   // 헤더 X 버튼
    const optionCloseButton2 = document.getElementById('optionCloseButton2'); // 바디의 닫기 버튼
    const optionCancelButton = document.getElementById('optionCancelButton');

    // Bootstrap progress bar & 텍스트
    const optionProgressBar = document.getElementById('optionProgressBar');
    const optionProgressText = document.getElementById('optionProgressText');

    const BASE_URL = 'http://localhost:5000';
    let eventSource = null;
    let isProcessing = false;

    // 에러 처리
    function logError(msg) {
        console.error("optionModal logError:", msg);
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

    // 초기화
    function resetUI() {
        isProcessing = false;
        optionProgressText.classList.remove('text-danger', 'text-success');
        optionProgressText.textContent = '0%';

        optionProgressBar.style.width = '0%';
        optionProgressBar.textContent = '0%';
    }

    // progress bar 업데이트
    function updateOptionBar(percentage, text = '') {
        optionProgressBar.style.width = percentage + '%';
        optionProgressBar.setAttribute('aria-valuenow', percentage);
        optionProgressBar.textContent = text || `${percentage}%`;
    }

    // SSE 연결
    function startProgressSSE(url) {
        if (eventSource) {
            eventSource.close();
        }
        eventSource = new EventSource(url);
        isProcessing = true;

        eventSource.onmessage = (evt) => {
            const msg = evt.data;
            console.log("Option SSE msg:", msg);

            let parsed = null;
            try {
                parsed = JSON.parse(msg); // {progress, message}
            } catch(e) {
                // JSON 아니면 그냥 문자열
            }

            if (parsed && parsed.progress !== undefined) {
                // JSON 구조
                const {progress, message} = parsed;
                if (progress < 100) {
                    optionProgressText.classList.remove('text-danger', 'text-success');
                    optionProgressText.textContent = `${message} (${progress}%)`;
                    updateOptionBar(progress, `${message} (${progress}%)`);
                } else {
                    // 100
                    optionProgressText.classList.remove('text-danger');
                    optionProgressText.classList.add('text-success');
                    optionProgressText.textContent = `${message} (100%)`;
                    updateOptionBar(100, `${message} (100%)`);
                    alert("작업이 완료되었습니다!");

                    eventSource.close();
                    isProcessing = false;
                }
            } else {
                // 일반 문자열
                if (msg.includes("오류") || msg.includes("에러")) {
                    optionProgressText.classList.remove('text-success');
                    optionProgressText.classList.add('text-danger');
                    optionProgressText.textContent = msg;
                    updateOptionBar(0, "오류 발생");
                    eventSource.close();
                    isProcessing = false;

                } else if (msg.includes("완료")) {
                    optionProgressText.classList.remove('text-danger');
                    optionProgressText.classList.add('text-success');
                    optionProgressText.textContent = msg + " (100%)";
                    updateOptionBar(100, msg + " (100%)");
                    alert("작업이 완료되었습니다!");

                    eventSource.close();
                    isProcessing = false;

                } else if (msg.includes("취소")) {
                    optionProgressText.textContent = "작업이 취소되었습니다.";
                    updateOptionBar(0, "취소됨");
                    eventSource.close();
                    isProcessing = false;

                } else {
                    // "XX%"
                    const match = msg.match(/(\d+)%/);
                    if (match) {
                        const pct = parseInt(match[1], 10);
                        optionProgressText.textContent = `진행 중... (${pct}%)`;
                        updateOptionBar(pct, `진행 ${pct}%`);
                    } else {
                        // 일반 메시지
                        optionProgressText.textContent = msg;
                    }
                }
            }
        };

        eventSource.onerror = (err) => {
            console.error("Option SSE Error:", err);
            if (isProcessing) {
                logError("SSE 연결 에러");
            }
        };
    }

    // ========== DB 삽입 ==========
    insertForm.addEventListener('submit', (e) => {
        e.preventDefault();

        // 1) POST /insert
        fetch(`${BASE_URL}/insert`, {method:'POST'})
            .then(res => res.json())
            .then(data => {
                console.log("POST /insert 응답:", data);
                // { message: "데이터 삽입 작업이 시작되었습니다." }

                if (data.message === "데이터 삽입 작업이 시작되었습니다.") {
                    // 모달 오픈 + UI 초기화
                    progressModal.show();
                    resetUI();

                    // 2) SSE GET /insert
                    startProgressSSE(`${BASE_URL}/insert`);
                } else {
                    logError(data.message);
                }
            })
            .catch(err => logError("DB 삽입 POST 중 에러: " + err));
    });

    // ========== 분석하기 ==========
    analysisForm.addEventListener('submit', (e) => {
        e.preventDefault();

        // 1) POST /analysis
        fetch(`${BASE_URL}/analysis`, {method:'POST'})
            .then(res => res.json())
            .then(data => {
                console.log("POST /analysis 응답:", data);
                // { message: "분석 작업이 시작되었습니다." } or { error: "..."}
                if (data.message === "분석 작업이 시작되었습니다.") {
                    progressModal.show();
                    resetUI();

                    // 2) SSE GET /analysis
                    startProgressSSE(`${BASE_URL}/analysis`);
                } else {
                    // 에러
                    logError(data.error || data.message);
                }
            })
            .catch(err => logError("분석 POST 에러: " + err));
    });

    // 취소 버튼
    optionCancelButton.addEventListener('click', () => {
        if (eventSource) {
            // 서버 /cancel
            fetch(`${BASE_URL}/cancel`, {method:'POST'}).then(() => {
                optionProgressText.textContent = '작업이 취소되었습니다.';
                updateOptionBar(0, "취소됨");
                eventSource.close();
                isProcessing = false;
                progressModal.hide();
            }).catch(err => {
                logError("취소 요청 중 에러: " + err);
            });
        } else {
            progressModal.hide();
        }
    });

    // 닫기(X)
    optionCloseButton.addEventListener('click', () => {
        if (isProcessing) {
            alert("진행 중인 작업이 있어 모달을 닫을 수 없습니다.");
        } else {
            progressModal.hide();
        }
    });

    // 닫기 버튼(아래)
    optionCloseButton2.addEventListener('click', () => {
        if (isProcessing) {
            alert("진행 중인 작업이 있어 모달을 닫을 수 없습니다.");
        } else {
            progressModal.hide();
        }
    });
});
