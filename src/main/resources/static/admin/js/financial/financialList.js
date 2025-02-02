// document.addEventListener('DOMContentLoaded', function () {
//     const filterButton = document.getElementById('filterButton');
//
//     // filterButton 존재 여부 확인
//     if (!filterButton) {
//         console.error('filterButton이 존재하지 않습니다.');
//         return;
//     }
//
//     filterButton.addEventListener('click', function () {
//         const startDate = document.getElementById('startDate').value;
//         const endDate = document.getElementById('endDate').value;
//         const keyword = document.getElementById('keyword').value;
//         const params = new URLSearchParams(window.location.search);
//
//         if (startDate) params.set('startDate', startDate);
//         if (endDate) params.set('endDate', endDate);
//         if (keyword) params.set('keyword', keyword);
//
//         window.location.search = params.toString();
//     });
// });
//
// function sortTable(field, direction) {
//     const params = new URLSearchParams(window.location.search);
//     params.set('sortField', field);
//     params.set('sortDirection', direction);
//     window.location.search = params.toString();
// }
function sortTable(field, direction) {
    const urlParams = new URLSearchParams(window.location.search);
    urlParams.set('sortField', field);
    urlParams.set('sortDirection', direction);
    // 페이지 번호를 0(첫 페이지)로 초기화하는 것이 좋음
    urlParams.set('page', '0');
    window.location.search = urlParams.toString();
}
