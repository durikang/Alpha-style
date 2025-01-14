document.addEventListener('DOMContentLoaded', () => {
    const body = document.querySelector('body');

    // 페이지 로드 시 페이드인 효과
    body.classList.add('fade-in');

    // 리다이렉트 시 페이드아웃 효과
    document.querySelectorAll('a').forEach(link => {
        link.addEventListener('click', (e) => {
            // 'no-transition' 클래스가 있을 경우 무시
            if (link.classList.contains('no-transition')) {
                return;
            }

            if (link.href) {
                e.preventDefault();
                const href = link.href;

                body.classList.add('fade-out');

                setTimeout(() => {
                    window.location.href = href; // 0.5초 후 리다이렉트
                }, 500);
            }
        });
    });

    // 특정 예외 처리: form 태그는 별도로 관리
    document.querySelectorAll('form').forEach(form => {
        form.addEventListener('submit', (e) => {
            // 'no-transition' 클래스가 있을 경우 무시
            if (form.classList.contains('no-transition')) {
                return;
            }

            e.preventDefault();

            body.classList.add('fade-out');

            setTimeout(() => {
                form.submit();
            }, 500); // 0.5초 후 폼 제출
        });
    });
});
