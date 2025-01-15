import { fetchIndividualCustomerData } from "./individualCustomerAjax.js";
import { initializeIndividualCustomerChart, updateIndividualCustomerChart } from "./individualCustomerChartManager.js";
import { initializeCustomerChart, updateCustomerChart } from "./chartManager.js";
import { updateCustomerDropdown } from "./dropdownManager.js";

/**
 * 모달 초기화 함수
 * @param {string} modalId - 모달 ID
 * @param {Object} options - 모달 옵션 (분석 로직, 열릴 때 로직)
 */
function initializeModal(modalId, options) {
    const modal = document.getElementById(modalId);

    if (modal) {
        // 모달 열릴 때 실행
        modal.addEventListener("show.bs.modal", (event) => {
            if (typeof options.onModalShow === "function") {
                options.onModalShow(event, modal);
            }
        });

        // "분석하기" 버튼 클릭 처리
        const analyzeButton = modal.querySelector(".btn-analyze");
        if (analyzeButton) {
            analyzeButton.addEventListener("click", () => {
                if (typeof options.onAnalyzeClick === "function") {
                    options.onAnalyzeClick(modal);
                }
            });
        }
    }
}

// 개인 고객 분석 모달 초기화
initializeModal("individualCustomerChartModal", {
    onModalShow: (event, modal) => {
        const button = event.relatedTarget;
        const customerId = button.getAttribute("data-customer-id");

        // 고객 ID를 모달 데이터 속성에 저장
        modal.dataset.customerId = customerId;

        // 시작일과 종료일 초기화
        const searchStartDate = document.getElementById("startDate").value;
        const searchEndDate = document.getElementById("endDate").value;

        document.getElementById("individualCustomerStartDate").value = searchStartDate || "";
        document.getElementById("individualCustomerEndDate").value = searchEndDate || "";
    },
    onAnalyzeClick: (modal) => {
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
    },
});

// 고객 분석 모달 초기화
initializeModal("customerChartModal", {
    onModalShow: (event) => {
        const searchStartDate = document.getElementById("startDate").value;
        const searchEndDate = document.getElementById("endDate").value;

        document.getElementById("customerModalStart").value = searchStartDate || "";
        document.getElementById("customerModalEnd").value = searchEndDate || "";

        const formData = new FormData(document.querySelector("form"));
        fetch("/admin/orders/list/ajax?" + new URLSearchParams(formData), { method: "GET" })
            .then((response) => {
                if (!response.ok) throw new Error("고객 데이터를 불러오는 중 문제가 발생했습니다.");
                return response.json();
            })
            .then((data) => {
                updateCustomerDropdown(data.customers);
            })
            .catch((error) => {
                console.error("모달 데이터 로드 오류:", error);
                alert("고객 데이터를 불러오지 못했습니다.");
            });
    },
    onAnalyzeClick: () => {
        let customerId = document.getElementById("customerSelect").value;
        const startDate = document.getElementById("customerModalStart").value;
        const endDate = document.getElementById("customerModalEnd").value;
        const chartType = document.getElementById("customerChartType").value;

        if (!customerId) {
            alert("고객을 선택하세요.");
            return;
        }

        if (customerId.startsWith("user_")) {
            customerId = customerId.replace("user_", ""); // 'user_' 제거
        }

        if (!startDate || !endDate) {
            alert("시작일과 종료일을 선택하세요.");
            return;
        }

        if (!["bar", "line", "pie"].includes(chartType)) {
            alert("유효하지 않은 차트 유형입니다.");
            return;
        }

        // 차트 초기화 및 로딩 표시
        initializeCustomerChart();
        const chartDom = document.getElementById("customerChartContainer");
        const customerChart = echarts.init(chartDom);
        customerChart.showLoading();

        fetch(`/admin/orders/analyze?customerId=${encodeURIComponent(customerId)}&startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}&chartType=${encodeURIComponent(chartType)}`)
            .then((response) => {
                if (!response.ok) {
                    return response.json().then((error) => {
                        throw new Error(error.error || "HTTP Error " + response.status);
                    });
                }
                return response.json();
            })
            .then((data) => {
                updateCustomerChart(data, chartType);
            })
            .catch((error) => {
                console.error("분석 오류:", error.message);
                alert("분석 중 문제가 발생했습니다.");
            });
    },
});
