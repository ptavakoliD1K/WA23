
    console.log("Skript geladen");

   function toggleComments(button) {
            const commentsSection = button.nextElementSibling;
            commentsSection.style.display = commentsSection.style.display === "none" ? "block" : "none";
            button.textContent = commentsSection.style.display === "block" ? "▲" : "▼";
        }

    function postComment(event, postId) {
    console.log("postComment wird aufgerufen für postId:", postId);
    event.preventDefault(); // Verhindert das Neuladen der Seite

    const form = document.getElementById(`commentForm_${postId}`);
    const formData = new FormData(form);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', "http://localhost:8080/posts/comment");

    // CSRF-Token setzen
    /*const csrfToken = document.querySelector('input[name="_csrf"]').value;
    xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
    */
    xhr.onload = () => {
        console.log("XHR Response:", xhr.responseText);

        if (xhr.status === 200) {
            const response = JSON.parse(xhr.responseText);
            if (response.success) {
                // Dynamisch den Kommentar zur Liste hinzufügen
                const commentsSection = form.closest('.comments-section').querySelector('ul');
                const newComment = document.createElement('li');
                newComment.innerHTML = `
                    <p>${response.comment.user.username}</p>
                    <p>${response.comment.content}</p>
                    <p>${response.comment.createdDate}</p>
                `;
                commentsSection.appendChild(newComment);

                // Textfeld leeren
                form.reset();
            } else {
                alert("Fehler beim Hinzufügen des Kommentars: " + response.message);
            }
        } else {
            alert("Fehler beim Hinzufügen des Kommentars. HTTP-Status: " + xhr.status);
        }
    };

    xhr.onerror = () => {
        console.error("XHR Fehler aufgetreten:", xhr.statusText);
        alert("Netzwerkfehler. Kommentar konnte nicht gesendet werden.");
    };

    xhr.send(formData);
}

    function openModal() {
        document.getElementById("newPostModal").style.display = "block";
    }

    function closeModal() {
        document.getElementById("newPostModal").style.display = "none";
    }

    function toggleSearch() {
        const searchBar = document.getElementById("searchBar");
        searchBar.style.display = searchBar.style.display === "none" ? "block" : "none";
    }
