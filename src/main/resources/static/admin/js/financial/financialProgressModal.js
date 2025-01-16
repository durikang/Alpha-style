document.addEventListener('DOMContentLoaded', function () {
    const downloadButton = document.getElementById('downloadExcel');
    const progressModal = new bootstrap.Modal(document.getElementById('progressModal'));
    const progressBar = document.getElementById('progressBar');

    downloadButton.addEventListener('click', async function () {
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;
        const transactionType = document.getElementById('transactionType').value;

        const params = new URLSearchParams({
            startDate: startDate || '',
            endDate: endDate || '',
            transactionType: transactionType || ''
        });

        // 모달 창 열기
        progressBar.style.width = '0%';
        progressBar.innerText = '0%';
        progressModal.show();

        try {
            const response = await fetch(`http://127.0.0.1:5000/financial/download?${params.toString()}`, {
                method: 'GET'
            });

            if (!response.ok) throw new Error('다운로드 실패');

            const reader = response.body.getReader();
            let receivedLength = 0;
            const chunks = [];

            while (true) {
                const { done, value } = await reader.read();
                if (done) break;

                chunks.push(value);
                receivedLength += value.length;

                // 진행바 업데이트
                const progress = Math.round((receivedLength / response.headers.get('Content-Length')) * 100);
                progressBar.style.width = `${progress}%`;
                progressBar.innerText = `${progress}%`;
            }

            const blob = new Blob(chunks);
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'financial_records.xlsx';
            a.click();

            progressModal.hide();
        } catch (error) {
            alert('다운로드 중 오류가 발생했습니다.');
            progressModal.hide();
        }
    });
});
