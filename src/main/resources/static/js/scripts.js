document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Property image gallery functionality
    const thumbnails = document.querySelectorAll('.property-thumbnail');
    const mainImage = document.querySelector('.main-property-image');
    
    if (thumbnails.length > 0 && mainImage) {
        thumbnails.forEach(thumbnail => {
            thumbnail.addEventListener('click', function() {
                mainImage.src = this.src;
                
                // Remove active class from all thumbnails
                thumbnails.forEach(t => t.parentElement.classList.remove('border-primary'));
                
                // Add active class to clicked thumbnail
                this.parentElement.classList.add('border-primary');
            });
        });
    }
    
    // Language switching functionality
    const languageLinks = document.querySelectorAll('.language-selector .dropdown-menu a');
    languageLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const lang = this.getAttribute('href').split('lang=')[1];
            changeLanguage(lang);
        });
    });
    
    // Set current language indicator
    updateLanguageIndicator();
});

// Function to change language while preserving current URL
function changeLanguage(lang) {
    const url = new URL(window.location);
    url.searchParams.set('lang', lang);
    window.location.href = url.toString();
}

// Function to update language indicator based on current locale
function updateLanguageIndicator() {
    const currentLang = getUrlParameter('lang') || 'en';
    const languageButton = document.querySelector('#languageDropdown .d-sm-none');
    const flagButton = document.querySelector('#languageDropdown .flag-icon');
    
    if (languageButton) {
        languageButton.textContent = currentLang.toUpperCase();
    }
    
    if (flagButton) {
        const flagMap = {
            'en': '🇺🇸',
            'es': '🇪🇸',
            'hu': '🇭🇺'
        };
        flagButton.textContent = flagMap[currentLang] || '🇺🇸';
    }
}

// Helper function to get URL parameter
function getUrlParameter(name) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(name);
}