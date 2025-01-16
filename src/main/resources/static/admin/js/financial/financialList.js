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
