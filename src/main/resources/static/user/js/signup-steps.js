document.addEventListener('DOMContentLoaded', () => {
    const steps = document.querySelectorAll('.step');
    let currentStep = 0;

    // 버튼 참조
    const toStepButtons = {
        step1Next: document.getElementById('toStep2'),
        step2Prev: document.getElementById('toStep1'),
        step2Next: document.getElementById('toStep3'),
        step3Prev: document.getElementById('toStep2'),
        step3Next: document.getElementById('toStep4'),
        step4Prev: document.getElementById('toStep3'),
    };

    function showStep(index) {
        steps.forEach((step, i) => {
            step.style.display = i === index ? 'block' : 'none';
        });
        currentStep = index;
    }

    // 다음 단계 이동
    toStepButtons.step1Next.addEventListener('click', () => showStep(1));
    toStepButtons.step2Prev.addEventListener('click', () => showStep(0));
    toStepButtons.step2Next.addEventListener('click', () => showStep(2));
    toStepButtons.step3Prev.addEventListener('click', () => showStep(1));
    toStepButtons.step3Next.addEventListener('click', () => showStep(3));
    toStepButtons.step4Prev.addEventListener('click', () => showStep(2));
});
