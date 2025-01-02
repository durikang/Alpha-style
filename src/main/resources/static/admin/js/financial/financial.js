// JavaScript로 텝 전환 처리
document.addEventListener('DOMContentLoaded', () => {
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');

    tabButtons.forEach(button => {
        button.addEventListener('click', () => {
            // 모든 버튼의 active 제거
            tabButtons.forEach(btn => btn.classList.remove('active'));
            // 클릭한 버튼에 active 추가
            button.classList.add('active');

            // 모든 콘텐츠 숨기기
            tabContents.forEach(content => content.classList.remove('active'));
            // 선택한 콘텐츠 보여주기
            const target = document.getElementById(button.dataset.tab);
            target.classList.add('active');
        });
    });
});
