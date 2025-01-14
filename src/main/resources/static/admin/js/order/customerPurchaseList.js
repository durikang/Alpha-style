// 이벤트 핸들러 초기화
function initializeCustomerChartHandlers() {
    const chartModalEl = document.getElementById("customerChartModal");
    const analyzeBtn = document.getElementById("customerAnalyzeButton");

    // 모달 열릴 때 드롭다운 데이터 갱신
    if (chartModalEl) {
        chartModalEl.addEventListener("shown.bs.modal", function () {
            const formData = new FormData(document.querySelector("form"));
            fetch("/admin/orders/list/ajax?" + new URLSearchParams(formData), {
                method: "GET",
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("고객 데이터를 불러오는 중 문제가 발생했습니다.");
                    }
                    return response.json();
                })
                .then(data => {
                    console.log("모달 열릴 때 받은 고객 데이터:", data);
                    updateCustomerDropdown(data.customers); // 고객 드롭다운 갱신
                })
                .catch(error => {
                    console.error("모달 데이터 로드 오류:", error);
                    alert("고객 데이터를 불러오지 못했습니다.");
                });
        });
    }

    // 분석 버튼 클릭 시
    if (analyzeBtn) {
        analyzeBtn.addEventListener("click", analyzeCustomerChart);
    }
}

// 차트 초기화
let customerChart = null;
function initializeCustomerChart() {
    if (!customerChart) {
        const chartDom = document.getElementById("customerChartContainer");
        if (chartDom) {
            customerChart = echarts.init(chartDom);
        }
    }
}

// 차트 분석하기
function analyzeCustomerChart() {
    const customerId = document.getElementById("customerSelect").value;
    const startDate = document.getElementById("customerModalStart").value;
    const endDate = document.getElementById("customerModalEnd").value;
    const chartType = document.getElementById("customerChartType").value;

    if (!customerId) {
        alert("고객을 선택하세요.");
        return;
    }

    if (!startDate || !endDate) {
        alert("시작일과 종료일을 선택하세요.");
        return;
    }

    console.log(`분석 요청: customerId=${customerId}, startDate=${startDate}, endDate=${endDate}, chartType=${chartType}`);
    fetch(`/admin/orders/analyze?customerId=${encodeURIComponent(customerId)}&startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}&chartType=${encodeURIComponent(chartType)}`)
        .then((response) => {
            if (!response.ok) {
                return response.json().then((error) => {
                    throw new Error(error.error || "HTTP Error " + response.status);
                });
            }
            return response.json();
        })
        .then((data) => {
            console.log("분석 결과 데이터:", data);
            updateCustomerChart(data, chartType);
        })
        .catch((error) => {
            console.error("차트 분석 오류:", error.message);
            alert(error.message || "분석 중 문제가 발생했습니다.");
        });
}

// 차트 업데이트
function updateCustomerChart(data, chartType) {
    if (!customerChart) return;

    const colors = {
        bar: ['#36a2eb', '#ff6384', '#4bc0c0', '#9966ff', '#ff9f40'],
        pie: ['#ff6384', '#36a2eb', '#cc65fe', '#ffce56', '#4bc0c0'],
    };

    const option = {
        title: {
            text: data.title || "고객 분석 결과",
            left: "center",
        },
        tooltip: {
            trigger: chartType === "pie" ? "item" : "axis",
        },
        xAxis: chartType === "pie" ? null : {
            type: "category",
            data: data.labels || [],
        },
        yAxis: chartType === "pie" ? null : { type: "value" },
        series: [],
    };

    if (chartType === "bar" || chartType === "line") {
        option.series.push({
            name: "구매량",
            type: chartType,
            data: data.values || [],
            itemStyle: {
                color: colors.bar[0],
            },
        });
    } else if (chartType === "pie") {
        option.series.push({
            name: "카테고리별 분포",
            type: "pie",
            data: (data.labels || []).map((label, idx) => ({
                name: label,
                value: data.values[idx],
            })),
            radius: "50%",
            itemStyle: {
                color: (params) => colors.pie[params.dataIndex % colors.pie.length],
            },
        });
    }

    customerChart.setOption(option, true);
}

// 페이지 로드 시 이벤트 초기화
document.addEventListener("DOMContentLoaded", initializeCustomerChartHandlers);
