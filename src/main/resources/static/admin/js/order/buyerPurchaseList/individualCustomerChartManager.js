let individualCustomerChart = null;

export function initializeIndividualCustomerChart(containerId) {
    const chartDom = document.getElementById(containerId);

    if (!chartDom) {
        console.error(`차트를 표시할 컨테이너(#${containerId})를 찾을 수 없습니다.`);
        return;
    }

    if (!individualCustomerChart) {
        individualCustomerChart = echarts.init(chartDom);
    }

    individualCustomerChart.showLoading();
}

export function updateIndividualCustomerChart(data, chartType) {
    if (!individualCustomerChart) {
        console.error("차트를 초기화해야 합니다.");
        return;
    }

    const colors = {
        bar: ["#36a2eb", "#ff6384"],
        pie: ["#ff6384", "#36a2eb", "#cc65fe", "#ffce56"],
    };

    const option = {
        title: {
            text: data.title || "개인 고객 분석 결과",
            left: "center",
        },
        tooltip: {
            trigger: chartType === "pie" ? "item" : "axis",
        },
        xAxis: chartType === "pie" ? null : {
            type: "category",
            data: data.labels || [],
        },
        yAxis: chartType === "pie" ? null : {
            type: "value",
        },
        series: [],
    };

    if (chartType === "bar" || chartType === "line") {
        option.series.push({
            type: chartType,
            data: data.values || [],
            itemStyle: { color: colors.bar[0] },
        });
    } else if (chartType === "pie") {
        option.series.push({
            type: "pie",
            data: (data.labels || []).map((label, idx) => ({
                name: label,
                value: data.values[idx] || 0,
            })),
            radius: "50%",
            itemStyle: {
                color: (params) => colors.pie[params.dataIndex % colors.pie.length],
            },
        });
    }

    individualCustomerChart.setOption(option, true);
    individualCustomerChart.hideLoading();
}
