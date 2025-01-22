// graph.js

// 년도를 설정하고 그래프를 로드하는 함수
function setYearAndLoadGraph(year) {
    console.log('버튼에서 넘긴 year 값:', year); // year 값 확인
    loadDataAndRender(year);
    showAnalysisTableByYear1(year);
    showAnalysisTableByYear2(year);
    showAnalysisTableByYear3(year);
    showAnalysisTableByYear4(year);
    showAnalysisTableByYear5(year);
    showAnalysisTableByYear6(year);
}

// 동적으로 로드된 스크립트를 실행하는 함수
function executeScripts(container) {
    const scripts = container.querySelectorAll('script');
    scripts.forEach((oldScript) => {
        const newScript = document.createElement('script');
        [...oldScript.attributes].forEach(attr => newScript.setAttribute(attr.name, attr.value));
        if (oldScript.src) {
            newScript.src = oldScript.src;
        } else {
            newScript.textContent = oldScript.textContent;
        }
        document.body.appendChild(newScript);
        oldScript.remove();
    });
}

// 데이터를 로드하고 렌더링하는 비동기 함수
async function loadDataAndRender(year) {
    const contentIds = [
        'tab-content',
        'tab-content1',
        'tab-content2',
        'tab-content3',
        'tab-content4',
        'tab-content5'
    ];

    try {
        // 로딩 메시지 표시
        contentIds.forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                element.innerHTML = '<p class="loading">Loading...</p>';
            } else {
                console.warn(`Element with ID '${id}' not found. Skipping.`);
            }
        });

        // 서버 요청
        const response = await fetch('http://127.0.0.1:5000/graph', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ year })
        });

        if (!response.ok) {
            throw new Error(`Error: ${response.status}`);
        }

        // JSON 데이터 파싱
        const result = await response.json();

        // HTML 콘텐츠 렌더링
        result.html_results.forEach((item, index) => {
            const contentElement = document.getElementById(contentIds[index]);
            if (contentElement) {
                contentElement.innerHTML = item.content || `<p>${item.path} 파일을 찾을 수 없습니다.</p>`;
                executeScripts(contentElement); // HTML 삽입 후 스크립트 실행
            } else {
                console.warn(`Element with ID '${contentIds[index]}' not found.`);
            }
        });

        // 테이블 데이터 렌더링
        if (result.table_data) {
            renderTableData(result.table_data);
        }

        console.log(`Data for year ${year} loaded successfully.`);
    } catch (error) {
        // 에러 처리
        contentIds.forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                element.innerHTML = `<p>Error: ${error.message}</p>`;
            }
        });
        console.error("Error fetching graph data:", error);
    }
}

// 테이블 데이터 렌더링 함수
function renderTableData(data) {
    // 1. Summary 데이터 처리
    if (data.summary && data.summary.length > 0) {
        const overall = data.summary.find(item => item.년도 === '전체') || {};
        const summary = data.summary[0]; // 첫 번째 데이터 기준

        // 실제 데이터 표시
        updateElementTextContent('sales-value', formatValue(overall['매출'], summary['매출']));
        updateElementTextContent('admin-cost-value', formatValue(overall['판관비'], summary['판관비']));
        updateElementTextContent('net-income-value', formatValue(overall['당기순이익'], summary['당기순이익']));

        // 예측 데이터 표시 (ID에 'feature_' 접두사 추가)
        updateElementTextContent('feature-sales-value', formatValue(overall['예측매출'], summary['예측매출']));
        updateElementTextContent('feature-admin-cost-value', formatValue(overall['예측판관비'], summary['예측판관비']));
        updateElementTextContent('feature-net-income-value', formatValue(overall['예측당기순이익'], summary['예측당기순이익']));
    }
    // 2. Category Sales 데이터 처리
    if (data.category_sales && data.category_sales.length > 0) {
        // 데이터를 '실제공급가액' 기준으로 내림차순 정렬
        const sortedCategorySales = data.category_sales.sort((a, b) => b['실제공급가액'] - a['실제공급가액']);

        // 정렬된 데이터를 기반으로 DOM에 반영
        sortedCategorySales.forEach((item, index) => {
            // 현재 인덱스를 기반으로 ID 생성 (category_value1, feature_category_value1, category_name1 등)
            const actualId = `category_value${index + 1}`;
            const predictedId = `feature_category_value${index + 1}`;
            const categoryNameId = `category_name${index + 1}`;

            // 1. 실제/예측 공급가액 업데이트
            const actualElement = document.getElementById(actualId);
            const predictedElement = document.getElementById(predictedId);

            if (actualElement) {
                actualElement.textContent = formatValue(item['실제공급가액'], null);
            } else {
                console.warn(`Element with ID '${actualId}' not found.`);
            }

            if (predictedElement) {
                predictedElement.textContent = formatValue(item['예측공급가액'], null);
            } else {
                console.warn(`Element with ID '${predictedId}' not found.`);
            }

            // 2. 카테고리 이름 업데이트
            const categoryNameElement = document.getElementById(categoryNameId);
            if (categoryNameElement) {
                categoryNameElement.textContent = item['카테고리'];
            } else {
                console.warn(`Element with ID '${categoryNameId}' not found.`);
            }
        });
    }

    // 3. Age Group Sales 데이터 처리
    if (data.age_group_sales && data.age_group_sales.length > 0) {
        const ageGroupIdMap = {
            '전체': { actual: 'age_overall', predicted: 'feature_age_overall' },
            '10대': { actual: '10age', predicted: 'feature_10age' },
            '20대': { actual: '20age', predicted: 'feature_20age' },
            '30대': { actual: '30age', predicted: 'feature_30age' },
            '40대': { actual: '40age', predicted: 'feature_40age' }
        };
        data.age_group_sales.forEach(item => {
            const ageGroup = item['나이대'];
            const elementIds = ageGroupIdMap[ageGroup];
            if (elementIds) {
                document.getElementById(elementIds.actual).textContent = formatValue(item['실제공급가액'], null);
                document.getElementById(elementIds.predicted).textContent = formatValue(item['예측공급가액'], null);
            }
        });
    }

    // 4. Gender Sales 데이터 처리
    if (data.gender_sales && data.gender_sales.length > 0) {
        const genderIdMap = {
            '전체': { actual: 'gender_overall', predicted: 'feature_gender_overall' },
            '남': { actual: 'male_data', predicted: 'feature_male_data' },
            '여': { actual: 'female_data', predicted: 'feature_female_data' }
        };
        data.gender_sales.forEach(item => {
            const gender = item['성별'];
            const elementIds = genderIdMap[gender];
            if (elementIds) {
                document.getElementById(elementIds.actual).textContent = formatValue(item['실제공급가액'], null);
                document.getElementById(elementIds.predicted).textContent = formatValue(item['예측공급가액'], null);
            }
        });
    }

    // 5. VIP Sales 데이터 처리
    if (data.vip_sales && data.vip_sales.length > 0) {
        const vipIdMap = {
            "전체": { actual: "vip_overall", predicted: "feature_vip_overall" },
            "10%": { actual: "10percent", predicted: "feature_10percent" },
            "20%": { actual: "20percent", predicted: "feature_20percent" },
            "30%": { actual: "30percent", predicted: "feature_30percent" }
        };
        data.vip_sales.forEach(item => {
            const ratio = item['비율'];
            const elementIds = vipIdMap[ratio];
            if (elementIds) {
                document.getElementById(elementIds.actual).textContent = formatValue(item['실제공급가액'], null);
                document.getElementById(elementIds.predicted).textContent = formatValue(item['예측공급가액'], null);
            }
        });
    }

    // 6. Area Sales 데이터 처리
    if (data.area_sales && data.area_sales.length > 0) {
        // 데이터를 '실제공급가액' 기준으로 내림차순 정렬
        const sortedAreaSales = data.area_sales.sort((a, b) => b['실제공급가액'] - a['실제공급가액']);

        // 정렬된 데이터를 기반으로 DOM에 반영
        sortedAreaSales.forEach((item, index) => {
            // 현재 인덱스를 기반으로 ID 생성 (area_value1, feature_area_value1, area_name1 등)
            const actualId = `area_value${index + 1}`;
            const predictedId = `feature_area_value${index + 1}`;
            const areaNameId = `area_name${index + 1}`;

            // 1. 실제/예측 공급가액 업데이트
            const actualElement = document.getElementById(actualId);
            const predictedElement = document.getElementById(predictedId);

            if (actualElement) {
                actualElement.textContent = formatValue(item['실제공급가액'], null);
            } else {
                console.warn(`Element with ID '${actualId}' not found.`);
            }

            if (predictedElement) {
                predictedElement.textContent = formatValue(item['예측공급가액'], null);
            } else {
                console.warn(`Element with ID '${predictedId}' not found.`);
            }

            // 2. 지역 이름 업데이트
            const areaNameElement = document.getElementById(areaNameId);
            if (areaNameElement) {
                areaNameElement.textContent = item['지역'];
            } else {
                console.warn(`Element with ID '${areaNameId}' not found.`);
            }
        });
    }


    // 2. 기타 데이터 처리 (Category Sales, Age Group, Gender 등)...
    // 여기에도 동일한 방식으로 처리
}

// 값 포맷팅 함수: 실제 공급가액과 예측 공급가액을 처리
function formatValue(actual, summaryActual) {
    if (actual === 'N/A' || actual === null || actual === undefined) {
        return 'N/A';
    } else if (typeof actual === 'number') {
        return actual.toLocaleString();
    } else {
        return actual;
    }
}

// 안전하게 요소에 텍스트 업데이트하는 함수
function updateElementTextContent(id, text) {
    const element = document.getElementById(id);
    if (element) {
        element.textContent = text;
    } else {
        console.warn(`Element with ID '${id}' not found.`);
    }
}

// 버튼 클릭 이벤트 등록
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.tabs button').forEach(button => {
        button.addEventListener('click', (event) => {
            const target = event.target;
            const year = target.dataset.year !== undefined
                ? target.dataset.year
                : (target.textContent.trim() === '전체' ? 'all' : target.textContent.trim());
            setYearAndLoadGraph(year);
        });
    });
});

// 개별 연도 분석 테이블 업데이트 함수
function showAnalysisTableByYear1(year) {
    console.log(`showAnalysisTableByYear1 called with year: ${year}`);
}

function showAnalysisTableByYear2(year) {
    console.log(`showAnalysisTableByYear2 called with year: ${year}`);
}

function showAnalysisTableByYear3(year) {
    console.log(`showAnalysisTableByYear3 called with year: ${year}`);
}

function showAnalysisTableByYear4(year) {
    console.log(`showAnalysisTableByYear4 called with year: ${year}`);
}

function showAnalysisTableByYear5(year) {
    console.log(`showAnalysisTableByYear5 called with year: ${year}`);
}

function showAnalysisTableByYear6(year) {
    console.log(`showAnalysisTableByYear6 called with year: ${year}`);
}
