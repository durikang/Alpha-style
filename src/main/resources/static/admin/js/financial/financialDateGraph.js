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

        document.getElementById('sales-value').textContent = overall['매출']?.toLocaleString() || summary['매출']?.toLocaleString() || 'N/A';
        document.getElementById('admin-cost-value').textContent = overall['판관비']?.toLocaleString() || summary['판관비']?.toLocaleString() || 'N/A';
        document.getElementById('net-income-value').textContent = overall['당기순이익']?.toLocaleString() || summary['당기순이익']?.toLocaleString() || 'N/A';
    }

    // 2. Category Sales 데이터 처리
    if (data.category_sales && data.category_sales.length > 0) {
        const categoryIdMap = {
            '상의': 'top_value',
            '하의': 'bottom_value',
            '운동화': 'running_shoes_value',
            '단화': 'loafers_value',
            '슬리퍼': 'slippers_value'
        };
        data.category_sales.forEach(item => {
            const elementId = categoryIdMap[item['카테고리']];
            if (elementId) {
                document.getElementById(elementId).textContent = item['공급가액']?.toLocaleString() || 'N/A';
            }
        });
    }

    // 3. Age Group Sales 데이터 처리
    if (data.age_group_sales && data.age_group_sales.length > 0) {
        const ageGroupIdMap = {
            '10대': '10age',
            '20대': '20age',
            '30대': '30age',
            '40대': '40age'
        };
        data.age_group_sales.forEach(item => {
            const elementId = ageGroupIdMap[item['나이대']];
            if (elementId) {
                document.getElementById(elementId).textContent = item['공급가액']?.toLocaleString() || 'N/A';
            }
        });
    }

    // 4. Gender Sales 데이터 처리
    if (data.gender_sales && data.gender_sales.length > 0) {
        const genderIdMap = {
            '남': 'male_data',
            '여': 'female_data'
        };
        data.gender_sales.forEach(item => {
            const elementId = genderIdMap[item['성별']];
            if (elementId) {
                document.getElementById(elementId).textContent = item['공급가액']?.toLocaleString() || 'N/A';
            }
        });
    }

    // 5. VIP Sales 데이터 처리
    if (data.vip_sales && data.vip_sales.length > 0) {
        const vipIdMap = {
            "상위 10%": "10percent",
            "상위 20%": "20percent",
            "상위 30%": "30percent"
        };
        data.vip_sales.forEach(item => {
            const elementId = vipIdMap[item['비율']];
            if (elementId) {
                document.getElementById(elementId).textContent = item['공급가액']?.toLocaleString() || 'N/A';
            }
        });
    }

    // 6. Area Sales 데이터 처리
    if (data.area_sales && data.area_sales.length > 0) {
        console.log("Area Sales Data:", data.area_sales); // 데이터 확인용
        data.area_sales.forEach((item, index) => {
            if (index < 5) {
                const areaNameElement = document.getElementById(`area_name${index + 1}`);
                const areaValueElement = document.getElementById(`area_value${index + 1}`);

                if (areaNameElement) areaNameElement.textContent = item['지역'] || 'N/A';
                if (areaValueElement) areaValueElement.textContent = item['공급가액']?.toLocaleString() || 'N/A';
            }
        });
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
