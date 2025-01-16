import { fetchIndividualCustomerData } from "./individualCustomerAjax.js";
import { initializeIndividualCustomerChart, updateIndividualCustomerChart } from "./individualCustomerChartManager.js";

document.addEventListener("DOMContentLoaded", () => {
    const modal = document.getElementById("individualCustomerChartModal");

    if (modal) {
        // 모달 열릴 때 이벤트 처리
        modal.addEventListener("show.bs.modal", (event) => {
            const button = event.relatedTarget;
            const customerId = button.getAttribute("data-customer-id");

            // 고객 ID를 모달 데이터 속성에 저장
            modal.dataset.customerId = customerId;

            // 상단 검색 필드에서 시작일과 종료일 값을 가져와 초기화
            const searchStartDate = document.getElementById("startDate").value;
            const searchEndDate = document.getElementById("endDate").value;

            document.getElementById("individualCustomerStartDate").value = searchStartDate || "";
            document.getElementById("individualCustomerEndDate").value = searchEndDate || "";
        });

        // "분석하기" 버튼 클릭 처리
        const analyzeButton = document.getElementById("individualCustomerAnalyzeButton");
        if (analyzeButton) {
            analyzeButton.addEventListener("click", () => {
                const customerId = modal.dataset.customerId;
                const startDate = document.getElementById("individualCustomerStartDate").value;
                const endDate = document.getElementById("individualCustomerEndDate").value;
                const chartType = document.getElementById("individualCustomerChartType").value;

                // 유효성 검사
                if (!customerId) {
                    alert("고객 ID가 유효하지 않습니다.");
                    return;
                }

                if (!startDate || !endDate) {
                    alert("시작일과 종료일을 선택하세요.");
                    return;
                }

                if (!["bar", "line", "pie"].includes(chartType)) {
                    alert("유효하지 않은 차트 유형입니다.");
                    return;
                }

                // 차트 초기화
                initializeIndividualCustomerChart("individualCustomerChartContainer");

                // 데이터 가져오기 및 차트 업데이트
                fetchIndividualCustomerData(customerId, startDate, endDate, chartType)
                    .then((data) => {
                        updateIndividualCustomerChart(data, chartType);
                    })
                    .catch((error) => {
                        console.error("개인 고객 데이터 로드 오류:", error.message);
                        alert("데이터를 가져오는 중 문제가 발생했습니다.");
                    });
            });
        }
    }
});
