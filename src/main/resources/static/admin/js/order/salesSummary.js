function analyzeSalesSummary() {
    const startDate = document.getElementById("modalStart").value;
    const endDate = document.getElementById("modalEnd").value;
    const chartType = document.getElementById("chartType").value;

    if (!startDate || !endDate) {
        alert("시작일과 종료일을 선택해주세요.");
        return;
    }

    fetch(`/admin/orders/analyzeSalesSummary?startDate=${startDate}&endDate=${endDate}&chartType=${chartType}`)
        .then((response) => {
            if (!response.ok) {
                throw new Error("분석 요청 중 오류 발생");
            }
            return response.json();
        })
        .then((data) => {
            const chartContainer = document.getElementById("chartContainer");
            const chart = echarts.init(chartContainer);

            const option = {
                title: {
                    text: data.title,
                    left: 'center',
                },
                tooltip: {
                    trigger: 'axis',
                },
                xAxis: {
                    type: 'category',
                    data: data.labels,
                },
                yAxis: {
                    type: 'value',
                },
                series: [
                    {
                        name: '매출 금액',
                        type: chartType,
                        data: data.values,
                    },
                ],
            };

            chart.setOption(option);
        })
        .catch((error) => {
            console.error("분석 요청 실패:", error);
            alert("분석 중 문제가 발생했습니다.");
        });
}
