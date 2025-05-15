"use strict"

const feedbackForm = document.getElementById("formFeedback");
const nameInput = document.getElementById("name");
const emailInput = document.getElementById("email");
const messageInput = document.getElementById("message")
const status = document.getElementById('status');

/**
 * event-listener, which sends POST request when submit
 */

feedbackForm.addEventListener("submit", (e) => {
    e.preventDefault()

    const email = emailInput.value;
    const name = nameInput.value;
    const message = messageInput.value;

    const formData = new FormData();

    if (isEmpty(email)) {
        formData.append('senderEmail', 'Unbekannt');
    } else {
        formData.append('senderEmail', email);
    }

    if (isEmpty(name)) {
        formData.append('name', 'Unbekannt');
    } else {
        formData.append('name', name);
    }

    formData.append('message', message);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/feedbackMessage')

    const csrfToken = getCsrfToken();
    xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);

    status.textContent = "Nachricht wird gesendet...";
    status.style.color = "orange";

    xhr.onload = () => {
        if (xhr.status !== 200) {
            console.error("Fehler: E-Mail wurde nicht gesendet");
            status.textContent = "Nachricht wurde nicht gesendet";
            status.style.color = "red";
        } else if (xhr.status === 200) {
            status.textContent = "Nachricht wurde gesendet. Vielen Dank für Dein Feedback.";
            status.style.color = "green";
        }
    };

    xhr.onerror = () => {
        console.error('Netzwerkfehler');
    };

    xhr.send(formData);

});

/**
 * gets CSRF-Token
 * @returns {string}
 */

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    const token = match ? match[1] : null;
    return token;
}

/**
 * checks if input field is empty
 * @param str
 * @returns {boolean}
 */

function isEmpty(str) {
    return !str.trim().length;
}