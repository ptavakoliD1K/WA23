const createPopUp = document.getElementById('createPopUp');
const removePopUp = document.getElementById('removePopUp');

const title = document.getElementById('title');
const content = document.getElementById('content');
const url = document.getElementById('url');

function showCreate() {
    if (createPopUp.style.display === "none") {
        createPopUp.style.display = "block";
    } else {
        createPopUp.style.display = "none";
    }
}

function showRemove() {
    if (removePopUp.style.display === "none") {
        removePopUp.style.display = "block";
    } else {
        removePopUp.style.display = "none";
    }
}

async function submitNewJob() {
    titleValue = title.value;
    contentValue = content.value;
    urlValue = url.value;

    let csrfToken = getCsrfToken();

    const response = await fetch("http://localhost:8080/job/post", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken,
        },
        body: JSON.stringify({
            "title": titleValue,
            "content": contentValue,
            "url": urlValue
        }),
    });
}

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    const token = match ? match[1] : null;
    return token;
}

document.addEventListener("DOMContentLoaded", function () {
    createPopUp.style.display = "none";
    removePopUp.style.display = "none";
});