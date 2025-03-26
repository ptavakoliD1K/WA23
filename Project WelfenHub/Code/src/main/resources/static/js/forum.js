let stompClient = null;

document.addEventListener("DOMContentLoaded", () => {
    connectWebSocket();
    bindReactionButtons(); // wichtig, da Post-Liste schon im DOM sein könnte
});

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log("Connected: " + frame);

        stompClient.subscribe('/topic/posts', function (message) {
            let post = JSON.parse(message.body);
            addPostToPage(post);
        });

        stompClient.subscribe('/topic/comments', function (message) {
            let comment = JSON.parse(message.body);
            addCommentToPage(comment);
        });

        stompClient.subscribe('/topic/reactions', function (message) {
            let reactionUpdate = JSON.parse(message.body);
            updateReactionCountOnPage(reactionUpdate.postId, reactionUpdate.reactionCount);
        });
    });
}



// Neuer Post inklusive aller benötigten Felder
function postNewPost(event) {
    event.preventDefault();

    let post = {
        title: document.getElementById("newPostTitle").value,
        content: document.getElementById("newPostContent").value,
        subject: document.querySelector('input[name="subject"]').value,
        semester: document.querySelector('input[name="semester"]').value,
        course: document.querySelector('input[name="course"]').value
    };

    stompClient.send("/app/newPost", {}, JSON.stringify(post));

    closeModal();  // Optional: schließt das Modal nach Erstellung
}

// Kommentar senden (passt bereits!)
function postComment(event, postId) {
    event.preventDefault();
    let comment = {
        postId: postId,
        content: document.getElementById(`comment-content-${postId}`).value
    };
    stompClient.send("/app/newComment", {}, JSON.stringify(comment));
}

// Dynamisch neuen Post hinzufügen (wichtig!)
function addPostToPage(post) {
    let postList = document.getElementById("post-list");
    let newPostItem = document.createElement("li");
    newPostItem.className = "post-item";
    newPostItem.id = `post-${post.id}`;

    newPostItem.innerHTML = `
        <h2>${post.title}</h2>
        <p>${post.content}</p>
        <p>Gepostet von ${post.user.username} am ${post.createdAt.split("T")[0]}</p>
        <button class="reaction-btn" data-post-id="${post.id}">
            👍 <span>${post.reactions || 0}</span>
        </button>
        <button type="button" class="comment-toggle" onclick="toggleComments(this)">▼</button>
        <div class="comments-section" style="display: none;">
            <h3>Kommentare:</h3>
            <ul id="comments-${post.id}"></ul>
            <div class="comment-form">
                <form onsubmit="postComment(event, ${post.id});">
                    <textarea id="comment-content-${post.id}" name="content" rows="3" required></textarea>
                    <button type="submit">Kommentieren</button>
                </form>
            </div>
        </div>
    `;

    postList.prepend(newPostItem);
    bindReactionButtons();

}

// Dynamisch Kommentar hinzufügen
function addCommentToPage(comment) {
    let commentList = document.getElementById(`comments-${comment.post.id}`);
    let newCommentItem = document.createElement("li");
    newCommentItem.id = `comment-${comment.id}`;

    newCommentItem.innerHTML = `
        <p>${comment.user.username}</p>
        <p>${comment.content}</p>
        <p>${comment.createdDate.split("T")[0]}</p>
    `;

    commentList.appendChild(newCommentItem);
}

// Kommentare ein- und ausblenden
function toggleComments(button) {
    const commentsSection = button.nextElementSibling;
    commentsSection.style.display = commentsSection.style.display === "none" ? "block" : "none";
}

// Hilfsfunktionen Modal
function openModal() {
    document.getElementById('newPostModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('newPostModal').style.display = 'none';
}


function updateReactionCountOnPage(postId, newCount) {
    const button = document.querySelector(`button[data-post-id="${postId}"]`);
    if(button) {
        button.querySelector('span').innerText = newCount;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    bindReactionButtons();
});

function bindReactionButtons() {
    document.querySelectorAll('.reaction-btn').forEach(button => {
        button.removeEventListener('click', handleReaction); // doppelte Bindung vermeiden
        button.addEventListener('click', handleReaction);
    });
}

function handleReaction(event) {
    const button = event.currentTarget;
    const postId = button.getAttribute('data-post-id');

    const xhr = new XMLHttpRequest();
    xhr.open('POST', `/posts/${postId}/react`);
    xhr.setRequestHeader('X-CSRF-TOKEN', getCsrfToken());

    xhr.onload = () => {
        if (xhr.status === 200) {
            const newReactionCount = xhr.responseText;
            button.querySelector('span').innerText = newReactionCount;
        } else {
            alert("Fehler beim Reagieren. Status: " + xhr.status);
        }
    };

    xhr.onerror = () => {
        alert("Netzwerkfehler. Reaktion konnte nicht gesendet werden.");
    };

    xhr.send();
}

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? match[1] : null;
}