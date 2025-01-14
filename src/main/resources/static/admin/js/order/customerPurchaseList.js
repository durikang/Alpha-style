// 이벤트 핸들러 초기화
function initializeCustomerHandlers() {
    const chartModalEl = document.getElementById('chartModal');
    const analyzeBtn = document.getElementById('analyzeBtn');

    // 모달 열릴 때 차트 초기화
    if (chartModalEl) {
        chartModalEl.addEventListener('shown.bs.modal', initializeChart);
    }

    // 분석 버튼 클릭 시
    if (analyzeBtn) {
        analyzeBtn.addEventListener('click', analyzeChart);
    }
}

// 차트 초기화
let customerChart = null;
function initializeChart() {
    if (!customerChart) {
        const chartDom = document.getElementById('chartContainer');
        if (chartDom) {
            customerChart = echarts.init(chartDom);
        }
    }
}

// 차트 분석하기
function analyzeChart() {
    const start = document.getElementById('modalStart').value;
    const end = document.getElementById('modalEnd').value;
    const chartType = document.getElementById('chartType').value;
    const customerId = document.body.dataset.customerId; // body의 dataset 사용

    if (!customerId) {
        alert('고객 ID를 찾을 수 없습니다.');
        return;
    }

    // 매개변수 인코딩
    const encodedStart = encodeURIComponent(start);
    const encodedEnd = encodeURIComponent(end);
    const encodedChartType = encodeURIComponent(chartType);

    // AJAX 요청
    fetch(`/admin/customers/analyze?customerId=${encodeURIComponent(customerId)}&startDate=${encodedStart}&endDate=${encodedEnd}&chartType=${encodedChartType}`)
        .then((response) => {
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            return response.json();
        })
        .then((data) => {
            updateChart(data, chartType);
        })
        .catch((error) => {
            console.error('차트 분석 오류:', error);
            alert('차트 데이터를 불러오지 못했습니다. 서버 상태를 확인하세요.');
        });
}

// 차트 업데이트
function updateChart(data, chartType) {
    if (!customerChart) return;

    const colors = {
        bar: ['#36a2eb', '#ff6384', '#4bc0c0', '#9966ff', '#ff9f40'],
        pie: ['#ff6384', '#36a2eb', '#cc65fe', '#ffce56', '#4bc0c0'],
    };

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
                name: '구매량',
                type: chartType, // 'bar', 'line', 'pie'
                data: data.values || [],
                itemStyle: {
                    color: (params) => {
                        const colorSet = chartType === 'pie' ? colors.pie : colors.bar;
                        return colorSet[params.dataIndex % colorSet.length];
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
    }

    customerChart.setOption(option, true);
}

// 페이지 로드 시 이벤트 초기화
document.addEventListener('DOMContentLoaded', initializeCustomerHandlers);
