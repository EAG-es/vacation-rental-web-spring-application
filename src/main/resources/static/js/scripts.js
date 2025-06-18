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
});