// timer.js
export const Timer = (() => {
    /**
     * 타이머 시작
     * @param {number} duration - 타이머 지속 시간 (초)
     * @param {HTMLElement} displayElement - 타이머를 표시할 요소
     * @param {Function} callback - 타이머 종료 시 실행할 콜백 함수
     * @returns {number} - setInterval ID
     */
    const startTimer = (duration, displayElement, callback) => {
        let timer = duration, minutes, seconds;
        const intervalId = setInterval(() => {
            minutes = Math.floor(timer / 60);
            seconds = timer % 60;

            minutes = minutes < 10 ? `0${minutes}` : minutes;
            seconds = seconds < 10 ? `0${seconds}` : seconds;

            displayElement.textContent = `남은 시간: ${minutes}분 ${seconds}초`;

            if (--timer < 0) {
                clearInterval(intervalId);
                callback();
            }
        }, 1000);
        return intervalId;
    };

    return { startTimer };
})();
