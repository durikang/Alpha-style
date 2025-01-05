/**
 * 입력 필드에 Debounce 적용
 * @param {HTMLInputElement} inputElement - 대상 input
 * @param {Function} callback - 실제 검사(또는 원하는 로직)를 호출할 함수
 * @param {number} delay - 지연 시간(ms)
 */
export function attachDebouncedInputListener(inputElement, callback, delay = 300) {
    let timer;
    inputElement.addEventListener('input', () => {
        clearTimeout(timer);
        timer = setTimeout(() => {
            // 입력이 300ms동안 멈추면 callback 실행
            callback(inputElement.value.trim());
        }, delay);
    });
}
