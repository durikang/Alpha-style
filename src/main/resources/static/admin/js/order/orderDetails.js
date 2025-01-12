document.addEventListener('DOMContentLoaded', () => {
    const cancelOrderButton = document.querySelector('.btn.btn-cancel');
    const saveChangesButton = document.querySelector('.btn.btn-save');

    if (cancelOrderButton) {
        cancelOrderButton.addEventListener('click', cancelOrder);
    }

    if (saveChangesButton) {
        saveChangesButton.addEventListener('click', saveChanges);
    }

    function cancelOrder() {
        if (confirm('주문을 취소하시겠습니까?')) {
            // 주문 취소 API 호출 로직
            alert('주문이 취소되었습니다.');
        }
    }

    function saveChanges() {
        const orderNoInput = document.querySelector('#orderNo');
        const statusSelect = document.querySelector('#status-select');

        if (!orderNoInput || !statusSelect) {
            alert('필수 데이터가 누락되었습니다.');
            return;
        }

        const orderNo = orderNoInput.value;
        const status = statusSelect.value;

        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/admin/orders/update';

        const orderNoHidden = document.createElement('input');
        orderNoHidden.type = 'hidden';
        orderNoHidden.name = 'orderNo';
        orderNoHidden.value = orderNo;
        form.appendChild(orderNoHidden);

        const statusHidden = document.createElement('input');
        statusHidden.type = 'hidden';
        statusHidden.name = 'status';
        statusHidden.value = status;
        form.appendChild(statusHidden);

        document.body.appendChild(form);
        form.submit();
    }
});
