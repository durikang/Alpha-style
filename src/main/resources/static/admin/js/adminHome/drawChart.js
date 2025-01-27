document.addEventListener("DOMContentLoaded", () => {
    console.log("HTML 로드 완료!");

    // HTML에 서버에서 주입한 초기 데이터가 있어야 작동
    let visitData = window.visitData || [];
    console.log("초기 visitData =", visitData);

    // 2) 차트 생성 함수
    function drawChart(visitData) {
        let dates = visitData.map(item => item.date);
        let counts = visitData.map(item => item.count);

        let traceBar = {
            x: dates,
            y: counts,
            type: 'bar',
            name: '막대그래프',
            marker: { color: 'rgba(54, 162, 235, 0.7)' }
        };

        let traceLine = {
            x: dates,
            y: counts,
            type: 'scatter',
            mode: 'lines+markers',
            name: '선그래프',
            line: { color: 'rgb(255, 99, 132)', width: 2 },
            marker: { size: 6 }
        };

        let layout = {
            title: '최근 7일 방문자 추이',
            xaxis: { title: '날짜' },
            yaxis: { title: '방문 수' },
            font: { family: 'Noto Sans KR', size: 14 }
        };

        Plotly.newPlot('weekly-visit-chart', [traceBar, traceLine], layout);
    }

    // 3) 페이지 최초 로드 시 차트 그리기
    drawChart(visitData);

    // 4) 1분마다 차트 데이터 갱신
    setInterval(() => {
        fetch('/admin/adminHome/visit-stats')
            .then(res => res.json())
            .then(data => {
                console.log("실시간 visitData =", data);
                drawChart(data); // 갱신된 데이터로 차트 다시 그리기
            })
            .catch(err => console.error("차트 갱신 에러:", err));
    }, 60000); // 60초(1분)
});
