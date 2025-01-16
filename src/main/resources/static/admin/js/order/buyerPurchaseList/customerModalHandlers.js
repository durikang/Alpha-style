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
        if (options.analyzeButtonClass) {
            const analyzeButton = modal.querySelector(`.${options.analyzeButtonClass}`);
            if (analyzeButton) {
                analyzeButton.addEventListener("click", () => {
                    if (typeof options.onAnalyzeClick === "function") {
                        options.onAnalyzeClick(modal);
                    }
                });
            }
        }
    }
}

// 개인 고객 분석 모달 초기화
initializeModal("individualCustomerChartModal", {
    analyzeButtonClass: "btn-individual-analyze",
    onModalShow: (event, modal) => {
        // 개인 고객 분석 초기화 로직
        const button = event.relatedTarget;
        const customerId = button.getAttribute("data-customer-id");
        modal.dataset.customerId = customerId;

        const searchStartDate = document.getElementById("startDate").value;
        const searchEndDate = document.getElementById("endDate").value;
        document.getElementById("individualCustomerStartDate").value = searchStartDate || "";
        document.getElementById("individualCustomerEndDate").value = searchEndDate || "";
    },
    onAnalyzeClick: (modal) => {
        // 개인 고객 분석 처리 로직
        const customerId = modal.dataset.customerId;
        const startDate = document.getElementById("individualCustomerStartDate").value;
        const endDate = document.getElementById("individualCustomerEndDate").value;
        const chartType = document.getElementById("individualCustomerChartType").value;

        if (!customerId || !startDate || !endDate || !["bar", "line", "pie"].includes(chartType)) {
            alert("유효하지 않은 입력 값입니다.");
            return;
        }

        initializeIndividualCustomerChart("individualCustomerChartContainer");
        fetchIndividualCustomerData(customerId, startDate, endDate, chartType)
            .then((data) => updateIndividualCustomerChart(data, chartType))
            .catch((error) => {
                console.error("개인 고객 데이터 로드 오류:", error.message);
                alert("데이터를 가져오는 중 문제가 발생했습니다.");
            });
    }
});


// 고객 분석 모달 초기화
initializeModal("customerChartModal", {
    analyzeButtonClass: "btn-customer-analyze",
    onModalShow: (event, modal) => {
        // 고객 분석 초기화 로직
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
            .then((data) => updateCustomerDropdown(data.customers))
            .catch((error) => {
                console.error("모달 데이터 로드 오류:", error);
                alert("고객 데이터를 불러오지 못했습니다.");
            });
    },
    onAnalyzeClick: (modal) => {
        // 고객 분석 처리 로직
        const customerId = document.getElementById("customerSelect").value;
        const startDate = document.getElementById("customerModalStart").value;
        const endDate = document.getElementById("customerModalEnd").value;
        const chartType = document.getElementById("customerChartType").value;

        if (!customerId || !startDate || !endDate || !["bar", "line", "pie"].includes(chartType)) {
            alert("유효하지 않은 입력 값입니다.");
            return;
        }

        initializeCustomerChart();
        const chartDom = document.getElementById("customerChartContainer");
        const customerChart = echarts.init(chartDom);
        customerChart.showLoading();

        fetch(`/admin/orders/analyze?customerId=${encodeURIComponent(customerId)}&startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}&chartType=${encodeURIComponent(chartType)}`)
            .then((response) => {
                if (!response.ok) throw new Error("분석 오류");
                return response.json();
            })
            .then((data) => updateCustomerChart(data, chartType))
            .catch((error) => {
                console.error("분석 오류:", error.message);
                alert("분석 중 문제가 발생했습니다.");
            });
    }
});

