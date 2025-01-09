// src/main/resources/static/admin/js/navbar.js

document.addEventListener('DOMContentLoaded', () => {
    const header = document.querySelector('.header');
    if (!header) {
        console.error('Header element not found');
        return;
    }

    // 헤더를 고정시키기 위한 클래스 추가 (이미 CSS에서 fixed 처리)
    header.style.position = 'fixed';
    header.style.top = '0';
    header.style.left = '0';
    header.style.zIndex = '1000';

    let lastScrollTop = 0;

    window.addEventListener('scroll', () => {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

        if (scrollTop > lastScrollTop) {
            // 스크롤을 내릴 때
            header.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)';
        } else {
            // 스크롤을 올릴 때
            header.style.boxShadow = 'none';
        }

        lastScrollTop = scrollTop;
    });

    // 모바일 드롭다운 토글
    const dropdownToggles = document.querySelectorAll('.nav-item.dropdown > .nav-link');
    dropdownToggles.forEach(toggle => {
        toggle.addEventListener('click', (e) => {
            if (window.innerWidth < 768) { // 모바일 기준 (반응형 디자인에 맞게 조정 가능)
                e.preventDefault();
                const dropdownMenu = toggle.nextElementSibling;
                if (dropdownMenu.style.display === 'block') {
                    dropdownMenu.style.display = 'none';
                } else {
                    dropdownMenu.style.display = 'block';
                }
            }
        });
    });
});
