class FinancialDownloadHandler {
    constructor(config) {
        this.downloadButton = document.getElementById(config.downloadButtonId);
        this.startDateInput = document.getElementById(config.startDateInputId);
        this.endDateInput = document.getElementById(config.endDateInputId);
        this.transactionTypeSelect = document.getElementById(config.transactionTypeSelectId);
        this.progressModal = new bootstrap.Modal(document.getElementById(config.progressModalId));
        this.progressBar = document.getElementById(config.progressBarId);
        this.cancelTaskButton = document.getElementById(config.cancelTaskButtonId);
        this.apiEndpoint = config.apiEndpoint;
        this.cancelEndpoint = config.cancelEndpoint;
        this.isCancelled = false;

        this.initialize();
    }

    initialize() {
        // 다운로드 버튼 클릭 이벤트
        this.downloadButton.addEventListener('click', async () => {
            if (!this.validateInputs()) return;

            const startDate = this.startDateInput.value;
            const endDate = this.endDateInput.value;
            const transactionType = this.transactionTypeSelect.value;

            const params = new URLSearchParams({
                startDate: startDate || '',
                endDate: endDate || '',
                transactionType: transactionType || '',
            });

            this.isCancelled = false;
            this.startDownload(params);
        });

        // 작업 취소 버튼 클릭 이벤트
        this.cancelTaskButton.addEventListener('click', async () => {
            this.isCancelled = true;
            await this.cancelTask();
            this.progressModal.hide();
        });
    }

    validateInputs() {
        const startDate = this.startDateInput.value;
        const endDate = this.endDateInput.value;
        const transactionType = this.transactionTypeSelect.value;

        if (!startDate || !endDate || !transactionType) {
            alert('시작일, 종료일, 거래 유형을 모두 선택해야 다운로드할 수 있습니다.');
            return false;
        }

        if (new Date(startDate) > new Date(endDate)) {
            alert('시작일은 종료일보다 클 수 없습니다.');
            return false;
        }

        return true;
    }

    async startDownload(params) {
        try {
            this.resetProgressBar();
            this.progressModal.show();

            const response = await fetch(`${this.apiEndpoint}?${params.toString()}`, { method: 'GET' });

            if (!response.ok) throw new Error('다운로드 실패');

            const reader = response.body.getReader();
            const contentLength = +response.headers.get('Content-Length');
            let receivedLength = 0;
            const chunks = [];

            while (true) {
                if (this.isCancelled) break;

                const { done, value } = await reader.read();
                if (done) break;

                chunks.push(value);
                receivedLength += value.length;

                this.updateProgressBar(receivedLength, contentLength);
            }

            if (!this.isCancelled) {
                this.completeDownload(chunks);
            }
        } catch (error) {
            alert('다운로드 중 오류가 발생했습니다.');
        } finally {
            this.progressModal.hide();
        }
    }

    resetProgressBar() {
        this.progressBar.style.width = '0%';
        this.progressBar.innerText = '0%';
    }

    updateProgressBar(receivedLength, contentLength) {
        const progress = Math.round((receivedLength / contentLength) * 100);
        this.progressBar.style.width = `${progress}%`;
        this.progressBar.innerText = `${progress}%`;
    }

    completeDownload(chunks) {
        const blob = new Blob(chunks);
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'financial_records.xlsx';
        a.click();
    }

    async cancelTask() {
        try {
            await fetch(this.cancelEndpoint, { method: 'POST' });
            alert('작업이 취소되었습니다.');
        } catch (error) {
            console.error('작업 취소 중 오류 발생:', error);
        }
    }
}

// 독립적인 초기화 로직
document.addEventListener('DOMContentLoaded', () => {
    new FinancialDownloadHandler({
        downloadButtonId: 'downloadExcel',
        startDateInputId: 'startDate',
        endDateInputId: 'endDate',
        transactionTypeSelectId: 'transactionType',
        progressModalId: 'progressModal',
        progressBarId: 'progressBar',
        cancelTaskButtonId: 'cancelTaskButton',
        apiEndpoint: 'http://127.0.0.1:5000/financial/download',
        cancelEndpoint: 'http://127.0.0.1:5000/cancel',
    });
});
