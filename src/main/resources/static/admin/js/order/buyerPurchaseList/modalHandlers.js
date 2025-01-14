import { initializeCustomerChart, updateCustomerChart } from "./chartManager.js";
import { updateCustomerDropdown } from "./dropdownManager.js";

export function initializeModalHandlers() {
    const chartModalEl = document.getElementById("customerChartModal");
    const analyzeBtn = document.getElementById("customerAnalyzeButton");

    if (chartModalEl) {
        chartModalEl.addEventListener("shown.bs.modal", () => {
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
        });
    }

    if (analyzeBtn) {
        analyzeBtn.addEventListener("click", () => {
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
        });
    }
}

document.addEventListener("DOMContentLoaded", initializeModalHandlers);
