export function fetchIndividualCustomerData(customerId, startDate, endDate, chartType) {
    return fetch(`/admin/orders/analyze?customerId=${encodeURIComponent(customerId)}&startDate=${encodeURIComponent(startDate)}&endDate=${encodeURIComponent(endDate)}&chartType=${encodeURIComponent(chartType)}`)
        .then((response) => {
            if (!response.ok) {
                throw new Error("HTTP Error " + response.status);
            }
            return response.json();
        })
        .catch((error) => {
            console.error("개인 고객 데이터 로드 오류:", error.message);
            throw error;
        });
}
