// static/js/sales/productSalesAnalysis.js

// 분석 버튼 클릭 -> ECharts로 차트 표시
function analyzeSalesSummary() {
    const startDate = document.getElementById("salesModalStart").value;
    const endDate = document.getElementById("salesModalEnd").value;
    const chartType = document.getElementById("salesChartType").value;

    if (!startDate || !endDate) {
        alert("시작일과 종료일을 선택하세요.");
        return;
    }

    const chartContainer = document.getElementById("salesChartContainer");
    if (!chartContainer) return;

    const salesChart = echarts.init(chartContainer);
    salesChart.clear();
    salesChart.showLoading();

    // 예: /admin/salesAnalysis?startDate=..., etc
    fetch(`/admin/pd-manager/salesAnalysis?startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}&chartType=${encodeURIComponent(chartType)}`)
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => {throw new Error(err.error);});
            }
            return response.json();
        })
        .then(data => {
            salesChart.hideLoading();
            // data에 따라 chartOption 구성
            // e.g. if chartType == 'pie', do pie logic...
            // ...
        })
        .catch(err => {
            console.error("분석 중 에러:", err);
            salesChart.hideLoading();
            alert("분석 오류: " + err.message);
        });
}

// 이벤트 바인딩
document.addEventListener('DOMContentLoaded', () => {
    const analyzeBtn = document.getElementById("salesAnalyzeButton");
    if (analyzeBtn) {
        analyzeBtn.addEventListener("click", analyzeSalesSummary);
    }

    // 모달 show event -> 값 복사
    const salesModal = document.getElementById("salesChartModal");
    if (salesModal) {
        salesModal.addEventListener("shown.bs.modal", () => {
            const mainStart = document.getElementById("startDate").value;
            const mainEnd   = document.getElementById("endDate").value;
            document.getElementById("salesModalStart").value = mainStart || "";
            document.getElementById("salesModalEnd").value   = mainEnd || "";
        });
    }
});
