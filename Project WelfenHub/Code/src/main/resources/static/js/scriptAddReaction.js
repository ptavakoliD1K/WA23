function toggleReaction(postId) {
    fetch(`/api/reactions/${postId}`, {
        method: 'POST'
    })
    .then(response => response.json())
    .then(data => {
        const countSpan = document.getElementById(`reaction-count-${postId}`);
        const heartIcon = document.getElementById(`heart-icon-${postId}`);

        if (countSpan) countSpan.textContent = data.reactionCount;

        if (heartIcon) {
            if (data.likedByUser === true) {
                heartIcon.classList.add("liked-heart");
            } else {
                heartIcon.classList.remove("liked-heart");
            }
        }
    })
    .catch(error => {
        alert('Fehler beim Liken: ' + error);
    });
}
