let stompClient = null;

document.addEventListener("DOMContentLoaded", () => {
    connectWebSocket();
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

        // ❌ WebSocket für Reactions ist überflüssig – REST übernimmt das
        // --> alles zu Reactions läuft jetzt via fetch() in scriptAddReaction.js
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
    closeModal();
}

// Kommentar senden (via WebSocket)
function postComment(event, postId) {
    event.preventDefault();
    let comment = {
        postId: postId,
        content: document.getElementById(`comment-content-${postId}`).value
    };
    stompClient.send("/app/newComment", {}, JSON.stringify(comment));
}

// Dynamisch neuen Post hinzufügen
function addPostToPage(post) {
    let postList = document.getElementById("post-list");
    let newPostItem = document.createElement("li");
    newPostItem.className = "post-item";
    newPostItem.id = `post-${post.id}`;

    newPostItem.innerHTML = `
        <h2>${post.title}</h2>
        <p>${post.content}</p>
        <p>Gepostet von ${post.user.username} am ${post.createdAt.split("T")[0]}</p>
        <button onclick="toggleReaction(${post.id})">
            ❤️ <span id="reaction-count-${post.id}">${post.reactions || 0}</span>
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

// Kommentare ein-/ausblenden
function toggleComments(button) {
    const commentsSection = button.nextElementSibling;
    commentsSection.style.display = commentsSection.style.display === "none" ? "block" : "none";
}

// Modal-Funktionen
function openModal() {
    document.getElementById('newPostModal').style.display = 'block';
}

function closeModal() {
    document.getElementById('newPostModal').style.display = 'none';
}
