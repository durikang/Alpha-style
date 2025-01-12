// 모달 요소 가져오기
const modal = document.getElementById('guideModal');
const closeModalSpan = document.getElementById('closeModal');
const closeButton = document.getElementById('closeButton');
const helpButton = document.getElementById('helpButton');

// 로컬 스토리지 키
const hasSeenGuide = 'hasSeenProductSalesGuide';

// 모달 열기 함수
function openModal() {
    modal.style.display = 'block';
}

// 모달 닫기 함수
function closeModalFunc() {
    modal.style.display = 'none';
    // 사용자가 안내를 본 것으로 설정 (초기 모달에만 해당)
    localStorage.setItem(hasSeenGuide, 'true');
}

// 페이지 로드 시 실행
window.onload = function () {
    try {
        if (!localStorage.getItem(hasSeenGuide)) {
            openModal();
        }
    } catch (error) {
        console.error("로컬 스토리지 접근 오류:", error);
        openModal();
    }
};

// 모달 닫기 이벤트 리스너
closeModalSpan.onclick = closeModalFunc;
closeButton.onclick = closeModalFunc;

// Help 버튼 클릭 시 모달 열기
helpButton.onclick = function() {
    openModal();
};

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    if (event.target == modal) {
        closeModalFunc();
    }
};
