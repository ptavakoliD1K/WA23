let stompClient = null;

document.addEventListener("DOMContentLoaded", () => {
    connectWebSocket();
});

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log("✅ Verbunden mit WebSocket: " + frame);

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

// ⬆️ Reaktion aktualisieren
function toggleReaction(postId) {
    stompClient.send("/app/reactToPost", {}, JSON.stringify({ postId: postId }));
}

function updateReactionCountOnPage(postId, newCount) {
    const span = document.getElementById(`reaction-count-${postId}`);
    if (span) {
        span.textContent = newCount;
    }
}

// 📝 Kommentar senden
function postComment(event, postId) {
    event.preventDefault();
    const content = document.getElementById(`comment-content-${postId}`).value;

    // per REST senden, nicht WebSocket!
    fetch('/posts/comment', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: `postId=${postId}&content=${encodeURIComponent(content)}`
    });

    document.getElementById(`comment-content-${postId}`).value = "";
}


// ➕ Post erstellen
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

// 🔽 Kommentare ein-/ausblenden
function toggleComments(button) {
    const commentsSection = button.nextElementSibling;
    const commentList = commentsSection.querySelector("ul");
    const allComments = commentList.querySelectorAll("li");
    const loadMoreBtn = commentsSection.querySelector(".load-more-comments-button");

    const isVisible = commentsSection.style.display === "block";

    if (isVisible) {
        // Zuklappen
        commentsSection.style.display = "none";
        button.textContent = "▼";

        // Reset auf Standardzustand beim nächsten Öffnen
        allComments.forEach((comment, index) => {
            if (index >= 3) {
                comment.classList.add("hidden-comment");
            } else {
                comment.classList.remove("hidden-comment");
            }
        });

        // Button neu einblenden, wenn mehr als 3 Kommentare da sind
        if (loadMoreBtn) {
            loadMoreBtn.style.display = allComments.length > 3 ? "block" : "none";
        }

    } else {
        // Aufklappen
        commentsSection.style.display = "block";
        button.textContent = "▲";

        // Nur 3 Kommentare zeigen
        allComments.forEach((comment, index) => {
            if (index >= 3) {
                comment.classList.add("hidden-comment");
            } else {
                comment.classList.remove("hidden-comment");
            }
        });

        // Button sichtbar machen (wenn nötig)
        if (loadMoreBtn) {
            loadMoreBtn.style.display = allComments.length > 3 ? "block" : "none";
        }
    }
}




// ➕ Dynamischen Post hinzufügen
function addPostToPage(post) {
    let postList = document.getElementById("post-list");
    let newPostItem = document.createElement("li");
    newPostItem.className = "post-item";
    newPostItem.id = `post-${post.id}`;

    newPostItem.innerHTML = `
        <div class="post-menu">
            <button class="options-button" onclick="toggleDropdown(this)">
              <i class="fas fa-ellipsis-v"></i>
            </button>
            <div class="dropdown-menu">
                <button class="actionButton editButton" onclick="editPost(${post.id})">
                    <i class="fas fa-edit"></i> Bearbeiten
                </button>
                <button class="actionButton deleteButton" onclick="deletePost(${post.id})">
                    <i class="fas fa-trash-alt"></i> Löschen
                </button>
            </div>
        </div>

        <h2>${post.title}</h2>
        <p>${post.content}</p>
        <p>Gepostet von ${post.user.username} am ${post.createdAt.split("T")[0]}</p>

        <button class="actionButton likeButton" onclick="toggleReaction(${post.id})">
            <i class="fa-solid fa-heart"></i> <span id="reaction-count-${post.id}">${post.reactionCount || 0}</span>
        </button>

        <button type="button" class="comment-toggle" onclick="toggleComments(this)">▼</button>
        <div class="comments-section" style="display: none;">
            <h3>Kommentare:</h3>
            <ul id="comments-${post.id}"></ul>
            <div class="comment-form">
                <form onsubmit="postComment(event, ${post.id});">
                    <textarea id="comment-content-${post.id}" name="content" rows="3" required></textarea>
                    <button id="kommentierenButton" type="submit">Kommentieren</button>
                </form>
            </div>
        </div>
    `;

    postList.prepend(newPostItem);
}

// 💬 Dynamisch Kommentar hinzufügen
function addCommentToPage(comment) {
    console.log("Looking for comment list:", `comments-${comment.postId}`);
    console.log("Found:", document.getElementById(`comments-${comment.postId}`));

    const commentList = document.getElementById(`comments-${comment.postId}`);
    if (!commentList) return;

    // Neues Kommentar-Element erstellen
    const newCommentItem = document.createElement("li");
    newCommentItem.id = `comment-${comment.id}`;
    newCommentItem.className = "comment-item";

    newCommentItem.innerHTML = `
        <div class="comment-header">
            <span class="comment-author">${comment.username}</span>
            <span class="comment-date">${comment.createdDate.split("T")[0]}</span>
        </div>
        <div class="comment-body">${comment.content}</div>
    `;

    // Kommentar anhängen
    commentList.appendChild(newCommentItem);

    // Alle Kommentare zählen
    const comments = commentList.querySelectorAll(".comment-item");
    const loadMoreButton = commentList.parentElement.querySelector(".load-more-comments-button");

    // Alle ausblenden, außer die letzten 3
    comments.forEach((c, i) => {
        if (i < comments.length - 3) {
            c.classList.add("hidden-comment");
        } else {
            c.classList.remove("hidden-comment");
        }
    });

    // Button aktualisieren
    if (comments.length > 3 && loadMoreButton) {
        loadMoreButton.style.display = "block";
    }
}



// 🔍 Suchleiste
function toggleSearch() {
    const searchBar = document.getElementById("searchBar");
    searchBar.style.display = searchBar.style.display === "none" ? "block" : "none";
}

// 🔓 Modal öffnen/schließen
function openModal() {
    document.getElementById("newPostModal").style.display = "flex";
}
function closeModal() {
    document.getElementById("newPostModal").style.display = "none";
}

// ⬇️ Dropdown-Menü anzeigen
function toggleDropdown(button) {
    const dropdown = button.nextElementSibling;
    dropdown.classList.toggle("show");
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
        if (menu !== dropdown) menu.classList.remove("show");
    });
}

document.addEventListener("click", function (event) {
    const isDropdown = event.target.closest('.dropdown-menu') || event.target.closest('.options-button');
    if (!isDropdown) {
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            menu.classList.remove("show");
        });
    }
});

function showAllComments(button) {
    const commentsSection = button.closest(".comments-section");
    const commentList = commentsSection.querySelector("ul");
    const hiddenComments = commentList.querySelectorAll(".hidden-comment");

    hiddenComments.forEach(comment => {
        comment.classList.remove("hidden-comment");
    });

    button.style.display = "none";
}



