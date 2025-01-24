"use strict"

const feedbackForm = document.getElementById("formFeedback");
const nameInput = document.getElementById("name");
const emailInput = document.getElementById("email");
const messageInput = document.getElementById("message")

/**
 * event-listener, which sends POST request when submit
 */

feedbackForm.addEventListener("submit", (e) => {
    e.preventDefault()

    const email = emailInput.value;
    const name = nameInput.value;
    const message = messageInput.value;

    const formData = new FormData();

    if (!isEmpty(email)) {
        formData.append('senderMail', 'Unbekannt');
    } else {
        formData.append('senderEmail', email);
    }

    if (!isEmpty(name)) {
        formData.append('name', 'Unbekannt');
    } else {
        formData.append('name', name);
    }

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
 * @returns {number}
 */

function isEmpty(str) {
    return str.trim().length === 0;
}