const formular = document.getElementById('mainForm');
const inputYear = document.getElementById('inputYear');
const fachrichtung = document.getElementById('fachrichtung');
const semester = document.getElementById('semester');
const dozent = document.getElementById('dozent');
const lehrveranstaltung = document.getElementById('lehrveranstaltung');
const textInput = document.getElementById('hints');

const feedbackValues = {};

// Sammle alle Labels mit den zugehörigen Inputs dynamisch
document.querySelectorAll('label').forEach((label) => {
    label.addEventListener('click', () => {
        // Finde den ersten ausgewählten Input innerhalb des Labels
        const selectedInput = label.querySelector('input:checked');
        if (selectedInput) {
            feedbackValues[label.id] = selectedInput.value;
        } else {
            delete feedbackValues[label.id];
        }
        console.log(feedbackValues);
    });
});


formular.addEventListener('submit', (e) => {
    feedbackValues["Lehrveranstaltung"] = lehrveranstaltung.value;
    feedbackValues["Dozent"] = dozent.value;
    feedbackValues["Semester"] = semester.value;
    feedbackValues["Jahrgang"] = inputYear.value;
    feedbackValues["Fachrichtung"] = fachrichtung.value;

    e.preventDefault();

    const formData = new FormData();
    formData.append('feedbackList', JSON.stringify(feedbackValues));
    formData.append('text', textInput.value);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/evaluation');

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

document.addEventListener('DOMContentLoaded', (e) => {
   const date = new Date();

   let year = date.getFullYear();

   inputYear.max = year;
});

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    const token = match ? match[1] : null;
    return token;
}
