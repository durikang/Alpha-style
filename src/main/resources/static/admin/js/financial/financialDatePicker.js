$(function() {
    // Date Range Picker 초기화
    $('input[name="daterange"]').daterangepicker({
        opens: 'center', // 팝업이 중앙에 열림
        locale: {
            format: 'YYYY-MM-DD', // 한국어 형식 (예: 2020-01-01)
            separator: ' - ', // 시작 날짜와 종료 날짜 사이의 구분자
            applyLabel: '적용',
            cancelLabel: '취소',
            daysOfWeek: ['일', '월', '화', '수', '목', '금', '토'],
            monthNames: [
                '1월', '2월', '3월', '4월', '5월', '6월',
                '7월', '8월', '9월', '10월', '11월', '12월'
            ],
            firstDay: 0 // 일요일을 시작 요일로 설정
        },
        startDate: moment('2020-01-01', 'YYYY-MM-DD'), // 초기 시작 날짜 설정
        endDate: moment('2020-12-31', 'YYYY-MM-DD') // 초기 종료 날짜 설정 (원하는 날짜로 변경 가능)
    }, function(start, end, label) {
        console.log("새로운 날짜 선택: " +
            start.format('YYYY-MM-DD') + ' ~ ' + end.format('YYYY-MM-DD'));
    });
});
