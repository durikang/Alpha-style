// orderView.js

document.addEventListener("DOMContentLoaded", function () {
    const searchForm = document.querySelector(".search-bar form");
    if (searchForm) {
        searchForm.addEventListener("submit", function (e) {
            e.preventDefault();
            const keyword = document.querySelector("input[name='keyword']").value;
            const sortField = document.querySelector("input[name='sortField']")?.value || 'orderDate';
            const sortDirection = document.querySelector("input[name='sortDirection']")?.value || 'desc';
            const url = `/user/order-check?keyword=${encodeURIComponent(keyword)}&sortField=${encodeURIComponent(sortField)}&sortDirection=${encodeURIComponent(sortDirection)}`;
            window.location.href = url;
        });
    }
});
