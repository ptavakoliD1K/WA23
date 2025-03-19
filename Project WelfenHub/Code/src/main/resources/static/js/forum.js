const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

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
});

function postNewPost(event) {
    event.preventDefault();
    let post = {
        title: document.getElementById("newPostTitle").value,
        content: document.getElementById("newPostContent").value
    };
    stompClient.send("/app/newPost", {}, JSON.stringify(post));
}

function postComment(event, postId) {
    event.preventDefault();
    let comment = {
        postId: postId,
        content: document.getElementById(`comment-content-${postId}`).value
    };
    stompClient.send("/app/newComment", {}, JSON.stringify(comment));
}
