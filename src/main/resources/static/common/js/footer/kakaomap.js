// map.js

// 카카오 맵을 초기화하고 마커를 설정하는 함수
function initializeKakaoMap() {
    var container = document.getElementById('map');
    if (!container) {
        console.error("지도를 표시할 'map' ID를 가진 요소가 존재하지 않습니다.");
        return;
    }

    var options = {
        center: new kakao.maps.LatLng(37.20764562785, 127.0348918013), // 지도의 중심좌표
        level: 3 // 지도의 확대 레벨
    };

    var map = new kakao.maps.Map(container, options); // 지도 생성 및 객체 리턴

    var markerPosition = new kakao.maps.LatLng(37.20764562785, 127.0348918013); // 마커가 표시될 위치

    var marker = new kakao.maps.Marker({
        position: markerPosition
    });

    marker.setMap(map); // 지도에 마커 표시
}

// DOM이 모두 로드된 후 지도 초기화 함수 호출
document.addEventListener('DOMContentLoaded', function() {
    // 카카오 맵 API가 로드된 후 초기화
    if (typeof kakao !== 'undefined' && kakao.maps) {
        initializeKakaoMap();
    } else {
        console.error("카카오 맵 API가 로드되지 않았습니다.");
    }
});
