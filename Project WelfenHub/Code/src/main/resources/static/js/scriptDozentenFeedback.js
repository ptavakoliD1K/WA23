const formular = document.getElementById('mainForm');
const inputYear = document.getElementById('inputYear');
const fachrichtung = document.getElementById('fachrichtung');
const semester = document.getElementById('semester');
const dozent = document.getElementById('dozent');
const lehrveranstaltung = document.getElementById('lehrveranstaltung');
const textInput = document.getElementById('hints');
const status = document.getElementById('status');

const feedbackValues = {};

/**
 * sammelt alle values der Labels dynamisch
 */

document.querySelectorAll('label').forEach((label) => {
    label.addEventListener('click', () => {
        // Finde den ersten ausgewählten Input innerhalb des Labels
        const selectedInput = label.querySelector('input:checked');
        if (selectedInput) {
            feedbackValues[label.id] = selectedInput.value;
        } else {
            delete feedbackValues[label.id];
        }
    });
});

/**
 * event listener which submits all information of lecturer evaluation to back end
 */

formular.addEventListener('submit', (e) => {
    feedbackValues["Lehrveranstaltung"] = lehrveranstaltung.value;
    feedbackValues["Dozent"] = dozent.value;
    feedbackValues["Semester"] = semester.value;
    feedbackValues["Jahrgang"] = inputYear.value;
    feedbackValues["Fachrichtung"] = fachrichtung.value;

    e.preventDefault();

    if (semester.value === "blocked") {
        alert("Bitte wähle ein Semester aus.");
        return;
    }

    const formData = new FormData();
    formData.append('feedbackList', JSON.stringify(feedbackValues));
    formData.append('text', textInput.value);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', '/evaluation');

    const csrfToken = getCsrfToken();
    xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);

    status.textContent = "Nachricht wird gesendet...";
    status.style.color = "orange";

    xhr.onload = () => {
        if (xhr.status !== 200) {
            console.error("Fehler: Nachricht wurde nicht gesendet");
            status.textContent = "Nachricht wurde nicht gesendet"
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
 * event listener to set max year to current year
 */

document.addEventListener('DOMContentLoaded', (e) => {
   const date = new Date();

   let year = date.getFullYear();

   inputYear.max = year;
});

/**
 * function to get Csrf-Token
 * @returns {string}
 */

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    const token = match ? match[1] : null;
    return token;
}
