document.addEventListener('DOMContentLoaded', () => {
    const progressText = document.getElementById('progressText');
    const progressBar = document.getElementById('uploadProgress');
    const cancelButton = document.getElementById('cancelButton');
    let eventSource;

    function startProgress() {
        eventSource = new EventSource('/insert');

        eventSource.addEventListener('progress', (event) => {
            const data = JSON.parse(event.data);
            progressBar.value = data.progress;
            progressText.textContent = `${data.progress}% - ${data.message}`;
        });

        eventSource.addEventListener('complete', (event) => {
            const data = JSON.parse(event.data);
            progressBar.value = data.progress;
            progressText.textContent = data.message;
            eventSource.close();
        });

        eventSource.addEventListener('cancel', (event) => {
            const data = JSON.parse(event.data);
            progressText.textContent = data.message;
            eventSource.close();
        });

        eventSource.addEventListener('error', (event) => {
            progressText.textContent = '오류 발생!';
            eventSource.close();
        });
    }

    cancelButton.addEventListener('click', () => {
        fetch('/cancel', { method: 'POST' })
            .then(response => response.json())
            .then(data => {
                progressText.textContent = data.message;
            });
    });

    // 작업 시작 버튼을 클릭하면 진행 시작
    document.getElementById('startButton').addEventListener('click', startProgress);
});
