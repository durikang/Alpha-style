document.addEventListener("DOMContentLoaded", () => {
    const checkboxes = document.querySelectorAll(".select-checkbox");
    const selectAllCheckbox = document.getElementById("selectAll");

    if (selectAllCheckbox) {
        selectAllCheckbox.addEventListener("change", () => {
            checkboxes.forEach(checkbox => {
                checkbox.checked = selectAllCheckbox.checked;
            });
        });
    }
});
