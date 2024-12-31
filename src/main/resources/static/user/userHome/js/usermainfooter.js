fetch('../../templates/user/usermainfooter.html')
    .then((response) => response.text())
    .then((data) => {
        // HTML 내용을 body의 맨 아래에 삽입
        document.body.insertAdjacentHTML('beforeend', data);
    })
