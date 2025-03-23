document.addEventListener("DOMContentLoaded", function() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    document.querySelectorAll('.reaction-btn').forEach(button => {
        button.addEventListener('click', function() {
            const postId = this.getAttribute('data-post-id');

            fetch(`/forum/post/${postId}/react`, {
                method: 'POST',
                headers: {
                    [csrfHeader]: csrfToken
                }
            })
            .then(response => {
                if (response.ok) {
                    return response.text();
                }
                throw new Error('Fehler beim Senden der Reaktion.');
            })
            .then(newReactions => {
                button.querySelector('span').innerText = newReactions;
            })
            .catch(error => console.error('Error:', error));
        });
    });
});
