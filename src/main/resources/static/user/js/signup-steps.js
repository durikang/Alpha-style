document.addEventListener('DOMContentLoaded', () => {
    const steps = document.querySelectorAll('.step');
    let currentStep = 0;

    // 추가 방어 코드: 단계 초기화
    if (!steps || steps.length === 0) {
        console.error('No steps found in the DOM. Ensure steps are correctly defined.');
        return;
    }

    function showStep(index) {
        steps.forEach((step, i) => {
            step.style.display = i === index ? 'block' : 'none';
        });
        currentStep = index;
    }

    // 페이지 로드 시 첫 번째 단계 표시
    showStep(0);

    // 이전/다음 버튼 로직 통합
    const stepButtons = document.querySelectorAll('.to-step');

    // 추가 방어 코드: 버튼 확인
    if (!stepButtons || stepButtons.length === 0) {
        console.error('No navigation buttons found. Ensure buttons have the class "to-step".');
        return;
    }

    stepButtons.forEach((button) => {
        button.addEventListener('click', (event) => {
            const targetStep = parseInt(event.target.dataset.targetStep, 10);

            if (!isNaN(targetStep) && targetStep >= 0 && targetStep < steps.length) {
                showStep(targetStep);
            } else {
                console.error('Invalid target step:', targetStep);
            }
        });
    });
});
