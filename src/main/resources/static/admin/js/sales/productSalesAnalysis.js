// static/js/sales/productSalesAnalysis.js

document.addEventListener('DOMContentLoaded', function() {
    const analyzeBtn = document.getElementById("salesAnalyzeButton");
    if (analyzeBtn) {
        analyzeBtn.addEventListener("click", analyzeSalesSummary);
    }
});

function analyzeSalesSummary() {
    const startDate = document.getElementById("salesModalStart").value;
    const endDate   = document.getElementById("salesModalEnd").value;
    const chartType = document.getElementById("salesChartType").value;

    if (!startDate || !endDate) {
        alert("시작일과 종료일을 선택하세요.");
        return;
    }

    // 분석 모달 내의 컨테이너는 로딩 스피너로 대체하지 않고, 새로운 모달을 사용
    // 기존의 스피너는 필요 없으므로 제거하거나 주석 처리

    // Flask 서버 URL로 변경
    const url = `http://127.0.0.1:5000/salesAnalysis?startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}&chartType=${encodeURIComponent(chartType)}`;

    // 분석 결과 모달 열기 전에 로딩 스피너를 보여주기 위해 새로운 모달에 스피너 추가
    const resultContainer = document.getElementById("salesResultChartContainer");
    resultContainer.innerHTML = `
        <div class="d-flex justify-content-center align-items-center" style="height: 100%;">
            <div class="spinner-border text-primary" role="status" aria-hidden="true"></div>
            <span class="ms-2">분석 중입니다. 잠시만 기다려주세요...</span>
        </div>
    `;

    // 새로운 모달 열기
    const salesResultModal = new bootstrap.Modal(document.getElementById('salesResultModal'), {
        keyboard: false
    });
    salesResultModal.show();

    fetch(url)
        .then(res => {
            if (!res.ok) {
                // 에러 응답을 JSON으로 파싱 시도
                return res.json().then(errData => {
                    throw new Error(errData.error || '요청 실패');
                }).catch(() => {
                    // JSON 파싱 실패 시 텍스트로 에러 처리
                    throw new Error('요청 실패');
                });
            }
            return res.json();
        })
        .then(data => {
            if (data.success) {
                // Plotly 그래프 렌더링
                resultContainer.innerHTML = ""; // 기존의 스피너 제거
                Plotly.newPlot(resultContainer, data.plotly_data, data.plotly_layout);
            } else {
                resultContainer.innerHTML = `<p style="color:red;">오류: ${data.error}</p>`;
            }
        })
        .catch(err => {
            resultContainer.innerHTML = `<p style="color:red;">분석 실패: ${err.message}</p>`;
        });
}
