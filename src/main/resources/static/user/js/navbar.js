document.addEventListener('DOMContentLoaded', () => {
    const header = document.querySelector('.header');
    if (!header) {
        console.error('Header element not found');
        return;
    }

    // 헤더를 고정시키기 위한 클래스 추가
    header.style.position = 'fixed';
    header.style.top = '0';
    header.style.left = '0';
    header.style.width = '100%';
    header.style.zIndex = '1000';

    let lastScrollTop = 0;

    window.addEventListener('scroll', () => {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop;

        if (scrollTop > lastScrollTop) {
            // 스크롤을 내릴 때 (추가적인 스타일이 필요하면 여기에 작성)
            header.style.boxShadow = '0 4px 6px rgba(0, 0, 0, 0.1)';
        } else {
            // 스크롤을 올릴 때 (추가적인 스타일이 필요하면 여기에 작성)
            header.style.boxShadow = 'none';
        }

        // 스크롤 위치 업데이트
        lastScrollTop = scrollTop;
    });
});
