document.addEventListener('DOMContentLoaded', function() {
    const tabs = document.querySelectorAll('.options-tab');
    const tabPanes = document.querySelectorAll('.options-tab-pane');

    // Function to activate a tab based on the tab name
    function activateTab(tabName) {
        tabs.forEach(tab => {
            if (tab.getAttribute('data-tab') === tabName) {
                tab.classList.add('active');
            } else {
                tab.classList.remove('active');
            }
        });

        tabPanes.forEach(pane => {
            if (pane.id === tabName) {
                pane.classList.add('active');
            } else {
                pane.classList.remove('active');
            }
        });
    }

    // Get activeTab from URL parameters
    const urlParams = new URLSearchParams(window.location.search);
    const activeTab = urlParams.get('activeTab') || 'regions';
    activateTab(activeTab);

    tabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const target = this.getAttribute('data-tab');
            activateTab(target);

            // Update the URL parameter without reloading the page
            const url = new URL(window.location);
            url.searchParams.set('activeTab', target);
            window.history.pushState({}, '', url);
        });
    });
});