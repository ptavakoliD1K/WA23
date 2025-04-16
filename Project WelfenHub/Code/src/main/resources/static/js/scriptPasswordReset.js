"use strict"

const emailInput = document.getElementById('emailReq');
const resetForm = document.getElementById('resetForm')

/**
 * Event-Listener which sends email of input-field to back-end
 */

resetForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const formData = new FormData();
    formData.append('email', emailInput.value);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/passwordResetProcess');

    const csrfToken = getCsrfToken();
    xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);

    xhr.onload = () => {
        if (xhr.status !== 200) {
            console.error("Fehler: E-Mail wurde nicht gesendet")
        }
    };

    xhr.onerror = () => {
        console.error('Netzwerkfehler');
    };

    xhr.send(formData);
    alert("Passwort-Reset wurde beantragt. Sie erhalten in kürze eine E-Mail.")
    window.location.href = "/login";
});

/**
 * function to get csrf-token
 * @returns {string}
 */

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    const token = match ? match[1] : null;
    return token;
}

