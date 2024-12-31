fetch('../../templates/user/mainHover.html')
    .then((response) => response.text())
    .then((data) => {
        // HTML 내용을 body의 맨 위에 삽입
        document.body.insertAdjacentHTML('afterbegin', data);
    })
    .catch((error) => console.error('Error loading HTML:', error));
