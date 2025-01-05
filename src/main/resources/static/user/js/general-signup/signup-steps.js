document.addEventListener('DOMContentLoaded', () => {
    const steps = document.querySelectorAll('.step');
    let currentStep = 0;

    // 초기화: 모든 step 숨기기
    steps.forEach(step => step.classList.remove('active'));
    // 첫 단계 보여주기
    steps[0].classList.add('active');

    function showStep(index) {
        steps.forEach((step, i) => {
            if (i === index) {
                step.classList.add('active');
            } else {
                step.classList.remove('active');
            }
        });
        currentStep = index;
    }

    const stepButtons = document.querySelectorAll('.to-step');
    stepButtons.forEach(button => {
        button.addEventListener('click', (event) => {
            const targetStep = parseInt(event.target.dataset.targetStep, 10);
            if (!isNaN(targetStep) && targetStep >= 0 && targetStep < steps.length) {
                showStep(targetStep);
            }
        });
    });
});
