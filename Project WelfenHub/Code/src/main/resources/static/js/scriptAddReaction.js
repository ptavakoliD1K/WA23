
function toggleReaction(postId) {
    fetch(`/api/reactions/${postId}`, {
        method: 'POST'
    })
    .then(res => res.json())
    .then(data => {
        document.getElementById(`reaction-count-${postId}`).textContent = data.reactionCount;
    })
    .catch(err => console.error("Fehler bei der Reaktion:", err));
}

