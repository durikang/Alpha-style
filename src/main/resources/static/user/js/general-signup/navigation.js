export const Navigation = (() => {
    const init = (steps, buttonSelector) => {
        const buttons = document.querySelectorAll(buttonSelector);

        if (!buttons.length) {
            console.warn('No navigation buttons found with the selector:', buttonSelector);
            return; // 버튼이 없으면 종료
        }

        buttons.forEach((button) => {
            button.addEventListener('click', (event) => {
                const button = event.target;
                const targetStep = parseInt(button.dataset.targetStep, 10);

                if (!isNaN(targetStep)) {
                    showStep(steps, targetStep);
                } else {
                    console.error('Invalid target step for button:', button);
                }
            });
        });
    };

    const showStep = (steps, index) => {
        steps.forEach((step, i) => {
            step.style.display = i === index ? 'block' : 'none';
        });
    };

    return { init };
})();
