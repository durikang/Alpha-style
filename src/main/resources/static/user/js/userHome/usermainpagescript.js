// '더 보기' 버튼 이벤트 핸들러
const loadMoreButtons = document.querySelectorAll(".btn-load-more");

loadMoreButtons.forEach(button => {
    button.addEventListener("click", function () {
        const mainCategoryId = this.getAttribute("data-category-id");
        let offset = parseInt(this.getAttribute("data-offset"));
        const limit = parseInt(this.getAttribute("data-limit"));

        fetch(`/user/product/items/more?mainCategoryId=${mainCategoryId}&offset=${offset}&limit=${limit}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Network response was not ok");
                }
                return response.json();
            })
            .then(data => {
                const itemGrid = document.getElementById(`item-grid-${mainCategoryId}`);
                if (data.length > 0) {
                    data.forEach(item => {
                        // Create item element
                        const itemDiv = document.createElement("div");
                        itemDiv.classList.add("item");

                        // 이미지
                        if (item.mainImage && item.mainImage.storeFileName) {
                            const img = document.createElement("img");
                            img.src = `/files/${item.mainImage.storeFileName}`;
                            img.alt = "상품 이미지";
                            img.style.maxWidth = "300px";
                            img.style.maxHeight = "300px";
                            itemDiv.appendChild(img);
                        } else {
                            const img = document.createElement("img");
                            img.src = "https://via.placeholder.com/300x300.png?text=No+Image";
                            img.alt = "기본 이미지";
                            img.style.maxWidth = "300px";
                            img.style.maxHeight = "300px";
                            itemDiv.appendChild(img);
                        }

                        // 정보
                        const infoDiv = document.createElement("div");
                        infoDiv.classList.add("info");

                        const name = document.createElement("h2");
                        name.textContent = item.itemName;
                        infoDiv.appendChild(name);

                        const price = document.createElement("p");
                        price.classList.add("price");
                        price.textContent = item.formattedSalePrice || item.salePrice;
                        infoDiv.appendChild(price);

                        const description = document.createElement("p");
                        description.textContent = item.description || "";
                        infoDiv.appendChild(description);

                        const viewLink = document.createElement("a");
                        viewLink.href = `/user/product/items/${item.itemId}`;
                        viewLink.classList.add("btn-view");
                        viewLink.textContent = "자세히 보기";
                        infoDiv.appendChild(viewLink);

                        itemDiv.appendChild(infoDiv);
                        itemGrid.appendChild(itemDiv);
                    });

                    // 업데이트 offset
                    offset += data.length;
                    this.setAttribute("data-offset", offset);

                    // 만약 더 이상 로드할 상품이 없으면 버튼 숨기기
                    if (data.length < limit) {
                        this.style.display = "none";
                    }
                } else {
                    // 더 이상 로드할 상품이 없으면 버튼 숨기기
                    this.style.display = "none";
                }
            })
            .catch(error => {
                console.error("Error fetching more items:", error);
            });
    });
});
