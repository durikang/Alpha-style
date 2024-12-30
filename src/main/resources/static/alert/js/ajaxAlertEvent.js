// src/main/resources/static/alert/js/ajaxAlertEvent.js
document.addEventListener("DOMContentLoaded", function () {
    const ajaxAlertBar = document.querySelector('.ajax-alert-bar');

    if (ajaxAlertBar) {
        // 닫기 버튼 클릭 시 알림 바 숨김
        const closeBtn = ajaxAlertBar.querySelector('.close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => {
                hideAjaxAlertBar(ajaxAlertBar);
            });
        }
    }

    /**
     * 알림 바 표시 함수
     * @param {string} message - 표시할 메시지
     * @param {string} type - 'SUCCESS' 또는 'ERROR'
     */
    window.showAjaxAlert = function (message, type) {
        if (!ajaxAlertBar) return;

        // 메시지 설정
        const messageElement = ajaxAlertBar.querySelector('p');
        messageElement.innerText = message;

        // 클래스 초기화
        ajaxAlertBar.classList.remove('ajax-alert-success', 'ajax-alert-error', 'hide');

        // 타입에 따라 클래스 추가
        if (type === 'SUCCESS') {
            ajaxAlertBar.classList.add('ajax-alert-success');
        } else if (type === 'ERROR') {
            ajaxAlertBar.classList.add('ajax-alert-error');
        }

        // 알림 바 표시
        ajaxAlertBar.classList.add('show');

        // 3초 후 자동으로 숨김
        setTimeout(() => {
            hideAjaxAlertBar(ajaxAlertBar);
        }, 3000);
    };

    /**
     * 알림 바 숨김 처리 함수
     * @param {HTMLElement} alertElement - 알림 바 요소
     */
    function hideAjaxAlertBar(alertElement) {
        if (!alertElement) return;
        alertElement.classList.remove('show');
        alertElement.classList.add('hide');
    }
});
