// 이벤트 핸들러 초기화
function initializeHandlers() {
    const chartModalEl = document.getElementById('chartModal');
    const csvDownloadBtn = document.getElementById('csvDownloadBtn');
    const analyzeBtn = document.getElementById('analyzeBtn');

    // CSV 다운로드 버튼 이벤트
    if (csvDownloadBtn) {
        csvDownloadBtn.addEventListener('click', downloadCSV);
    }

    // 모달이 표시될 때 차트 초기화
    if (chartModalEl) {
        chartModalEl.addEventListener('shown.bs.modal', initializeChart);
    }

    // 분석하기 버튼 이벤트
    if (analyzeBtn) {
        analyzeBtn.addEventListener('click', analyzeChart);
    }
}

// CSV 다운로드
function downloadCSV() {
    const itemId = document.body.dataset.itemId; // body의 dataset을 사용

    if (!itemId) {
        alert('상품 ID를 찾을 수 없습니다.');
        return;
    }

    // 다운로드 요청
    window.location.href = `/admin/orders/download?keyword=${itemId}`;
}

// 차트 초기화
let myChart = null;
function initializeChart() {
    if (!myChart) {
        const chartDom = document.getElementById('chartContainer');
        if (chartDom) {
            myChart = echarts.init(chartDom);
        }
    }
}

// 분석하기
function analyzeChart() {
    const start = document.getElementById('modalStart').value;
    const end = document.getElementById('modalEnd').value;
    const chartType = document.getElementById('chartType').value;
    const itemId = document.body.dataset.itemId; // body의 dataset을 사용

    if (!itemId) {
        alert('상품 ID를 찾을 수 없습니다.');
        return;
    }

    // 매개변수를 안전하게 인코딩
    const encodedStart = encodeURIComponent(start);
    const encodedEnd = encodeURIComponent(end);
    const encodedChartType = encodeURIComponent(chartType);

    // AJAX 요청
    fetch(`/admin/orders/itemDetails/analyze?itemId=${encodeURIComponent(itemId)}&startDate=${encodedStart}&endDate=${encodedEnd}&chartType=${encodedChartType}`)
        .then((resp) => {
            if (!resp.ok) {
                throw new Error(`HTTP error! Status: ${resp.status}`);
            }
            return resp.json();
        })
        .then((data) => {
            updateChart(data, chartType);
        })
        .catch((err) => {
            console.error(err);
            alert('차트 데이터를 불러오지 못했습니다. 서버 오류를 확인하세요.');
        });
}

// 차트 업데이트
function updateChart(data, chartType) {
    if (!myChart) return;

    const barColors = ['#36a2eb', '#ff6384', '#4bc0c0', '#9966ff', '#ff9f40'];
    const pieColors = ['#ff6384', '#36a2eb', '#cc65fe', '#ffce56', '#4bc0c0'];

    // 선형 회귀 계산 함수
    function calculateLinearRegression(xData, yData) {
        const n = xData.length;
        const xSum = xData.reduce((sum, x) => sum + x, 0);
        const ySum = yData.reduce((sum, y) => sum + y, 0);
        const xySum = xData.reduce((sum, x, i) => sum + x * yData[i], 0);
        const xSquaredSum = xData.reduce((sum, x) => sum + x * x, 0);

        const slope = (n * xySum - xSum * ySum) / (n * xSquaredSum - xSum * xSum);
        const intercept = (ySum - slope * xSum) / n;

        return xData.map((x) => slope * x + intercept);
    }

    // X축 데이터를 숫자로 변환
    const numericXData = data.labels.map((label, idx) => idx);

    // 선형 회귀 데이터 계산
    const regressionLine = calculateLinearRegression(numericXData, data.values);

    const option = {
        title: {
            text: data.title || '분석 결과',
            left: 'center',
        },
        tooltip: { trigger: 'axis' },
        xAxis: chartType === 'pie' ? null : {
            type: 'category',
            data: data.labels || [],
        },
        yAxis: chartType === 'pie' ? null : { type: 'value' },
        series: [
            {
                name: '판매량',
                type: chartType, // 'bar', 'line', 'pie'
                data: data.values || [],
                itemStyle: {
                    color: (params) => {
                        if (chartType === 'pie') {
                            return pieColors[params.dataIndex % pieColors.length];
                        } else {
                            return barColors[params.dataIndex % barColors.length];
                        }
                    },
                },
            },
        ],
    };

    if (chartType === 'pie') {
        option.tooltip = { trigger: 'item' };
        option.series[0].data = (data.labels || []).map((label, idx) => {
            return { name: label, value: data.values[idx] };
        });
        option.series[0].radius = '50%';
        option.series[0].itemStyle = {
            color: (params) => pieColors[params.dataIndex % pieColors.length],
        };
    } else if (chartType === 'line' || chartType === 'bar') {
        // 회귀선 추가
        option.series.push({
            name: '회귀선',
            type: 'line',
            data: regressionLine,
            lineStyle: {
                type: 'dashed',
                color: '#ff6347',
            },
            tooltip: {
                show: false, // 회귀선은 툴팁 제외
            },
        });
    }

    myChart.setOption(option, true);
}



// DOMContentLoaded 이벤트에서 초기화
document.addEventListener('DOMContentLoaded', initializeHandlers);
