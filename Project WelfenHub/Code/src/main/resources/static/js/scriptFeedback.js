"use strict"

const feedbackForm = document.getElementById("formFeedback");
const nameInput = document.getElementById("name");
const emailInput = document.getElementById("email");
const messageInput = document.getElementById("message")

feedbackForm.addEventListener("submit", (e) => {
   e.preventDefault()

    const email = emailInput.value;
    const name = nameInput.value;
    const message = messageInput.value;

    const formData = new FormData();
    formData.append('senderEmail', email);
    formData.append('name', name);
    formData.append('message', message);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/feedbackMessage')

    const csrfToken = getCsrfToken();
    xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);

    xhr.onload = () => {
        if (xhr.status !== 200) {
            console.error("Fehler: E-Mail wurde nicht gesendet");
        } else if (xhr.status === 200) {
            alert("Die Nachricht wurde erfolgreich an gesendet. Vielen Dank für Ihr Feedback");
        }
    };

    xhr.onerror = () => {
        console.error('Netzwerkfehler');
    };

    xhr.send(formData);

});

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    const token = match ? match[1] : null;
    return token;
}