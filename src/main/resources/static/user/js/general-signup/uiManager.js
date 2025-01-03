export const UIManager = (() => {
    const showElement = (element) => {
        if (!element) {
            console.error('Element is null or undefined:', element);
            return;
        }
        element.classList.remove('hidden');
        element.style.display = 'block'; // 명시적으로 block 설정
        console.log('Removing hidden class from element:', element);
    };

    const hideElement = (element) => {
        if (!element) {
            console.error('Element is null or undefined:', element);
            return;
        }
        element.classList.add('hidden');
        element.style.display = 'none'; // 확실히 숨김 처리
        console.log('Adding hidden class to element:', element);
    };


    const disableElement = (element) => {
        if (element) {
            element.disabled = true;
        }
    };

    const enableElement = (element) => {
        if (element) {
            element.disabled = false;
        }
    };

    return {
        hideElement,
        showElement,
        disableElement,
        enableElement,
    };
})();
