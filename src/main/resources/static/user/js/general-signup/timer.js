export const Timer = (() => {
    const startTimer = (duration, timerMessage, onExpire) => {
        let remainingTime = duration;

        const updateTimer = () => {
            const minutes = Math.floor(remainingTime / 60);
            const seconds = remainingTime % 60;

            timerMessage.textContent = `남은 시간: ${minutes}분 ${seconds}초`;
            remainingTime -= 1;

            if (remainingTime < 0) {
                clearInterval(timerInterval);
                timerMessage.textContent = '인증 시간이 만료되었습니다.';
                timerMessage.style.color = 'red';
                if (typeof onExpire === 'function') onExpire();
            }
        };

        const timerInterval = setInterval(updateTimer, 1000);
        updateTimer();
        return timerInterval;
    };

    return { startTimer };
})();
