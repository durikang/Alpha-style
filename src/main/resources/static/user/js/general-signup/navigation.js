// navigation.js
import { UIManager } from './uiManager.js';

export const Navigation = (() => {
    let currentStep = 0;
    let isTransitioning = false; // 전환 중 여부 플래그

    const init = (steps, buttonSelector) => {
        // 초기 설정: 첫 단계만 보이고 나머지는 숨김
        steps.forEach((step, index) => {
            if (index === 0) {
                UIManager.showElementAsync(step)
                    .then(() => console.log(`Navigation: Step ${index + 1} shown.`))
                    .catch(err => console.error(`Navigation: Error showing step ${index + 1}:`, err));
            } else {
                UIManager.hideElementAsync(step)
                    .then(() => console.log(`Navigation: Step ${index + 1} hidden.`))
                    .catch(err => console.error(`Navigation: Error hiding step ${index + 1}:`, err));
            }
        });

        // 'to-step' 버튼 이벤트 리스너 추가
        const stepButtons = document.querySelectorAll(buttonSelector);
        stepButtons.forEach(button => {
            button.addEventListener('click', async (event) => {
                if (isTransitioning) {
                    console.warn('Navigation: Transition in progress, click ignored.');
                    return;
                }

                const targetStep = parseInt(event.target.dataset.targetStep, 10);
                console.log(`Navigation: Button clicked. Current Step: ${currentStep + 1}, Target Step: ${targetStep + 1}`);

                if (!isNaN(targetStep) && targetStep >= 0 && targetStep < steps.length) {
                    try {
                        if (targetStep === currentStep) {
                            console.warn(`Navigation: Already on step ${currentStep + 1}.`);
                            return;
                        }

                        isTransitioning = true; // 전환 시작

                        // 현재 단계 숨기기
                        await UIManager.hideElementAsync(steps[currentStep]);
                        console.log(`Navigation: Step ${currentStep + 1} hidden.`);

                        // 대상 단계 보이기
                        await UIManager.showElementAsync(steps[targetStep]);
                        console.log(`Navigation: Step ${targetStep + 1} shown.`);

                        // 현재 단계 업데이트
                        currentStep = targetStep;
                        console.log(`Navigation: Current step updated to ${currentStep + 1}.`);

                        isTransitioning = false; // 전환 종료
                    } catch (error) {
                        console.error(`Navigation: Error transitioning from step ${currentStep + 1} to step ${targetStep + 1}:`, error);
                        isTransitioning = false; // 전환 종료
                    }
                } else {
                    console.warn(`Navigation: Invalid targetStep: ${targetStep}`);
                }
            });
        });
    };

    return { init };
})();
