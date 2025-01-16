function analyzeSalesSummary() {
    const startDate = document.getElementById("salesModalStart").value;
    const endDate = document.getElementById("salesModalEnd").value;
    const chartType = document.getElementById("salesChartType").value;

    if (!startDate || !endDate) {
        alert("시작일과 종료일을 선택해주세요.");
        return;
    }

    const chartContainer = document.getElementById("salesChartContainer");
    if (!chartContainer) {
        console.error("차트 컨테이너를 찾을 수 없습니다: #salesChartContainer");
        return;
    }

    // 차트 초기화
    const salesChart = echarts.init(chartContainer);
    salesChart.clear(); // 기존 차트 데이터 초기화
    salesChart.showLoading();

    fetch(`/admin/orders/analyzeSalesSummary?startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}&chartType=${encodeURIComponent(chartType)}`)
        .then(response => response.ok ? response.json() : response.json().then(error => { throw new Error(error.error); }))
        .then(data => {
            if (chartType === "pie") {
                // 파이 차트 처리
                const mainCategoryData = data.mainCategories.map(c => ({ name: c.name, value: c.value }));
                const subCategoryData = data.subCategories.map(c => ({ name: c.name, value: c.value }));

                const mainCategoryOption = {
                    title: { text: "큰 카테고리별 매출", left: "center" },
                    tooltip: { trigger: "item" },
                    series: [{
                        name: "매출",
                        type: "pie",
                        data: mainCategoryData,
                        radius: "50%",
                    }]
                };

                // 메인 카테고리 차트 렌더링
                salesChart.setOption(mainCategoryOption);

                // 서브 카테고리 차트 처리
                const subChartContainer = document.getElementById("subCategoryChartContainer");
                subChartContainer.style.display = "block"; // 서브 차트 표시
                const subCategoryChart = echarts.init(subChartContainer);
                const subCategoryOption = {
                    title: { text: "서브 카테고리별 매출", left: "center" },
                    tooltip: { trigger: "item" },
                    series: [{
                        name: "매출",
                        type: "pie",
                        data: subCategoryData,
                        radius: "50%",
                    }]
                };
                subCategoryChart.setOption(subCategoryOption);
            } else {
                // 선/막대 그래프 처리
                const option = {
                    title: { text: data.title, left: "center" },
                    tooltip: { trigger: "axis" },
                    xAxis: { type: "category", data: data.dates },
                    yAxis: { type: "value" },
                    series: [
                        { name: "총 매출", type: chartType, data: data.totalSales },
                        { name: "평균 매출", type: chartType, data: data.averageSales }
                    ]
                };

                // 선/막대 차트 렌더링
                salesChart.setOption(option);

                // 서브 카테고리 차트를 숨김
                document.getElementById("subCategoryChartContainer").style.display = "none";
            }

            salesChart.hideLoading();
        })
        .catch(error => {
            console.error("분석 요청 실패:", error.message);
            alert("분석 중 문제가 발생했습니다: " + error.message);
            salesChart.hideLoading();
        });
}


// 모달 열릴 때 시작일과 종료일 초기화
document.getElementById("salesChartModal").addEventListener("shown.bs.modal", () => {
    const searchStartDate = document.getElementById("startDate").value;
    const searchEndDate = document.getElementById("endDate").value;

    document.getElementById("salesModalStart").value = searchStartDate || "";
    document.getElementById("salesModalEnd").value = searchEndDate || "";
});

// 분석 버튼 클릭 시 이벤트 바인딩
document.getElementById("salesAnalyzeButton").addEventListener("click", analyzeSalesSummary);
