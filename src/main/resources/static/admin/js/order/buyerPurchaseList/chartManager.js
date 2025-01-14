let customerChart = null;

export function initializeCustomerChart() {
    if (!customerChart) {
        const chartDom = document.getElementById("customerChartContainer");
        if (chartDom) {
            customerChart = echarts.init(chartDom);
        } else {
            console.error("차트를 표시할 컨테이너(#customerChartContainer)를 찾을 수 없습니다!");
        }
    }
}

export function updateCustomerChart(data, chartType) {
    if (!customerChart) initializeCustomerChart();

    const colors = {
        bar: ["#36a2eb", "#ff6384", "#4bc0c0", "#9966ff", "#ff9f40"],
        pie: ["#ff6384", "#36a2eb", "#cc65fe", "#ffce56", "#4bc0c0"],
    };

    const option = {
        title: {
            text: data.title || "고객 분석 결과",
            left: "center"
        },
        tooltip: {
            trigger: chartType === "pie" ? "item" : "axis"
        },
        xAxis: chartType === "pie" ? null : {
            type: "category",
            data: data.labels || []
        },
        yAxis: chartType === "pie" ? null : {
            type: "value"
        },
        series: [],
    };

    if (chartType === "bar" || chartType === "line") {
        option.series.push({
            name: "구매량",
            type: chartType,
            data: data.values || [],
            itemStyle: { color: colors.bar[0] },
        });
    } else if (chartType === "pie") {
        option.series.push({
            name: "카테고리별 분포",
            type: "pie",
            data: (data.labels || []).map((label, idx) => ({
                name: label,
                value: data.values[idx] || 0,
            })),
            radius: "50%",
            itemStyle: { color: (params) => colors.pie[params.dataIndex % colors.pie.length] },
        });
    }

    customerChart.setOption(option, true);
    customerChart.hideLoading(); // 로딩 숨김
}
