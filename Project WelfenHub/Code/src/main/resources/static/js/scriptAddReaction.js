document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('.reaction-btn').forEach(button => {
        button.addEventListener('click', () => {
            const postId = button.getAttribute('data-post-id');

            const xhr = new XMLHttpRequest();
            xhr.open('POST', `/posts/${postId}/react`);

            const csrfToken = getCsrfToken();
            xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);

            xhr.onload = () => {
                if (xhr.status === 200) {
                    const reactionCount = xhr.responseText;
                    button.querySelector('span').innerText = reactionCount;

                    // Button visuell ein- und ausschalten
                    button.classList.toggle('reacted');
                } else {
                    alert("Fehler beim Reagieren. Status: " + xhr.status);
                }
            };

            xhr.onerror = () => {
                alert("Netzwerkfehler. Reaktion konnte nicht gesendet werden.");
            };

            xhr.send();
        });
    });
});

// CSRF-Token aus Cookie holen (unverändert)
function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? match[1] : null;
}
