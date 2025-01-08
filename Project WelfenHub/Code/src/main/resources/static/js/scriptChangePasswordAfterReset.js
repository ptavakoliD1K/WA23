"use strict"

const urlParams = new URLSearchParams(window.location.search);
const token = urlParams.get('token');
const newPasswordForm = document.getElementById('newPasswordForm');
const newPassword = document.getElementById('newPassword');
const passwordRepeat = document.getElementById('passwordRepeat');

/**
 * Event-listener which checks if formular is submitted
 * when submitted, new password is sent to backend
 */

newPasswordForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const newInputPasswort = newPassword.value;
    const repeatNewInputPasswort = passwordRepeat.value;

    const uppercaseLetters = newInputPasswort.match(/[A-Z]/g);
    const lowercaseLetters = newInputPasswort.match(/[a-z]/g);
    const numbers = newInputPasswort.match(/[0-9]/g);

    const uppercaseCount = uppercaseLetters ? uppercaseLetters.length : 0;
    const lowercaseCount = lowercaseLetters ? lowercaseLetters.length : 0;
    const numberCount = numbers ? numbers.length : 0;

    if (newInputPasswort.length < 8 || uppercaseCount === 0 || lowercaseCount === 0 || numberCount === 0) {
        alert("Das Passwort muss mindestens 8 Zeichen lang sein, einen Großbuchstaben, einen Kleinbuchstaben und eine Zahl beinhalten.");
        return;
    } else if (newInputPasswort !== repeatNewInputPasswort) {
        alert("Die Passwörter sind nicht identisch!")
        return
    }

    const formData = new FormData();
    formData.append('newPassword', newInputPasswort);
    formData.append('token', token);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', "http://localhost:8080/setNewPassword");

    const csrfToken = getCsrfToken();
    xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);

    xhr.onload = () => {
        const response = JSON.parse(xhr.responseText);
        if (response.status === "success") {
             window.location.href = "/login";
            alert(response.message);
        } else {
            alert(response.message);
        }
    }

    xhr.onerror = () => {
        console.error('Netzwerkfehler')
    }

    xhr.send(formData)

})

/**
 * Function to get CSRF-token
 * @returns {string}
 */

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    const token = match ? match[1] : null;
    return token;
}