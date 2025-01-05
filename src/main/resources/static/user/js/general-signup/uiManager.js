// uiManager.js

export const UIManager = (() => {

    // 요소를 표시하는 메서드 (페이드 인 제거)
    const showElementAsync = (element) => {
        return new Promise((resolve) => {
            if (!element) {
                console.warn('UIManager.showElementAsync: Element is null or undefined.');
                resolve();
                return;
            }
            console.log(`UIManager.showElementAsync: Showing #${element.id}`);
            element.style.display = 'block';
            resolve();
        });
    };

    // 요소를 숨기는 메서드 (페이드 아웃 제거)
    const hideElementAsync = (element) => {
        return new Promise((resolve) => {
            if (!element) {
                console.warn('UIManager.hideElementAsync: Element is null or undefined.');
                resolve();
                return;
            }
            console.log(`UIManager.hideElementAsync: Hiding #${element.id}`);
            element.style.display = 'none';
            resolve();
        });
    };

    return {
        showElementAsync,
        hideElementAsync
    };
})();
