const dropZone = document.getElementById('drop_zone');
const fileInput = document.getElementById('file-input');
const uploadForm = document.getElementById('upload-form');
const fileUploadInput = document.getElementById('file-upload-input');
const fileList = document.getElementById('file-list');
const progressBarContainer = document.getElementById('progress-bar-container');
const progressBar = document.getElementById('progress-bar');
const message = document.getElementById('message');
const dropdownValue = document.getElementById('semesterDropDown');
const moduleValue = document.getElementById('moduleDropDown');
const fachrichtungValue = document.getElementById('fachrichtung');
const searchBar = document.getElementById('searchQuery');
const title = document.getElementById('fileShareTitle');
const tag = document.getElementById('tag');
const headerContainer = document.getElementById('header-container');

/**
 * Hides progress-bar if Progress is 0%
 */

function hideProgressBar() {
    if (progressBar.textContent == "0%") {
        progressBarContainer.style.display = "none";
    }
}

/**
 * gets CSRF Token
 */

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    const token = match ? match[1] : null;
    return token;
}


/**
 * updates files shown in the frontend
 * @param {*} files
 */

function updateFileList(files) {
    fileList.innerHTML = '';
    for (let i = 0; i < files.length; i++) {
        const fileItem = document.createElement('p');
        fileItem.textContent = files[i].name;
        fileList.appendChild(fileItem);
    }
}

/**
 * displays files in the frontend
 * @param {*} files
 */

function displayFileList(files) {
    const fileListElement = document.getElementById('fileList');
    fileListElement.innerHTML = ''; // Clear previous list
    showSearchBar();

    files.forEach(fileName => {
        // Create a new div for each file
        const fileDiv = document.createElement('div');
        const lineDiv = document.createElement('div')

        // Create text for the file name
        const fileText = document.createTextNode(fileName);

        // Create a download button
        const downloadButton = document.createElement('button');
        downloadButton.className = "fas fa-download";
        downloadButton.onclick = () => downloadFile(fileName);

        // Create a preview button
        const previewButton = document.createElement('button');
        previewButton.className = "fas fa-eye";
        previewButton.onclick = () => previewFile(fileName);

        // Create a delete button
        const deleteButton = document.createElement('button');
        deleteButton.className = "fas fa-trash";
        deleteButton.onclick = () => deleteFile(fileName, dropdownValue.value, moduleValue.value, fachrichtungValue.value, tag.value);

        // set class-name of lineDiv
        lineDiv.className = "lineDiv"

        // Append the file name and button to the div
        fileDiv.appendChild(fileText);
        fileDiv.appendChild(downloadButton);
        fileDiv.appendChild(previewButton);
        fileDiv.appendChild(deleteButton);

        // Append the div to the file list element
        fileListElement.appendChild(fileDiv);
        fileListElement.appendChild(lineDiv);
    });
}

/**
 * updates the value of the drop down menus
 */

function updateSelectedValues() {
    const selectedSemester = dropdownValue.value;
    const selectedModule = moduleValue.value;
    const selectedFachrichtung = fachrichtungValue.value;
    const selectedTag = tag.value;

    fetchFileList();
}


dropdownValue.addEventListener('change', updateSelectedValues);

moduleValue.addEventListener('change', updateSelectedValues);

fachrichtungValue.addEventListener('change', updateSelectedValues);

tag.addEventListener('change', updateSelectedValues);

/**
 * Event-Listener if anything on the site changes
 */

dropZone.addEventListener('click', () => {
    fileInput.click();
});

fileInput.addEventListener('change', () => {
    if (fileInput.files.length) {
        fileUploadInput.files = fileInput.files;
        updateFileList(fileInput.files);
    }
});

dropZone.addEventListener('dragover', (e) => {
    e.preventDefault();
    dropZone.classList.add('dragover');
});

dropZone.addEventListener('dragleave', () => {
    dropZone.classList.remove('dragover');
});

dropZone.addEventListener('drop', (e) => {
    e.preventDefault();
    dropZone.classList.remove('dragover');

    if (e.dataTransfer.files.length) {
        fileUploadInput.files = e.dataTransfer.files;
        updateFileList(e.dataTransfer.files);
    }
});


/**
 * upload formular
 * uploads all selected files when pressing upload button
 */

uploadForm.addEventListener('submit', (e) => {
    e.preventDefault();

    if (dropdownValue.value === "blocked" || moduleValue.value === "blocked" || fachrichtungValue.value === "blocked" || tag.value === "blocked") {
        message.textContent = "Bitte wähle bei allen Dropdown-Menüs einen Wert";
        message.style.color = "red";
        return;
    }

    if (!fileUploadInput.files.length) {
        message.textContent = "Bitte wähle mindestens eine Datei zum hochladen aus";
        message.style.color = "red";
        return;
    }

    const formData = new FormData();
    for (let i = 0; i < fileUploadInput.files.length; i++) {
        formData.append('files[]', fileUploadInput.files[i]);
    }

    formData.append('semester', dropdownValue.value);
    formData.append('module', moduleValue.value);
    formData.append('fachrichtung', fachrichtungValue.value);
    formData.append('tag', tag.value);

    const xhr = new XMLHttpRequest();
    xhr.open('POST', `http://localhost:8080/upload`, true);

    const username = "welfenadmin";
    const password = "Welfenhub";
    xhr.setRequestHeader("Authorization", "Basic " + btoa(`${username}:${password}`));

    const csrfToken = getCsrfToken();
    xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);

    xhr.upload.onprogress = (event) => {
        if (event.lengthComputable) {
            const percentComplete = (event.loaded / event.total) * 100;
            progressBar.style.width = percentComplete + '%';
            progressBar.textContent = Math.round(percentComplete) + '%';
        }
    };

    xhr.onloadstart = () => {
        progressBarContainer.style.display = 'block';
        progressBar.style.width = '0%';
        progressBar.textContent = '0%';
    };

    xhr.onload = () => {
        if (xhr.status === 200) {
            message.textContent = 'Datei(en) erfolgreich hochgeladen!';
            message.style.color = "green";
            fetchFileList();
        } else {
            message.textContent = 'Hochladen der Datei(en) fehlgeschlagen!';
            message.style.color = "red";
        }
        progressBarContainer.style.display = 'none';
    };

    xhr.onerror = () => {
        console.error('Fehler:', xhr.responseText);
        message.textContent = 'Hochladen der Datei(en) fehlgeschlagen!';
        message.style.color = "red";
        progressBarContainer.style.display = 'none';
    };

    xhr.send(formData);
});

/**
 * fetches file list from database
 * communicates with backend
 */

async function fetchFileList() {
    const selectedSemester = dropdownValue.value;
    const selectedModule = moduleValue.value;
    const selectedFachrichtung = fachrichtungValue.value;
    const selectedTag = tag.value;

    try {
        const username = "welfenadmin";
        const password = "Welfenhub";

        const response = await fetch(`/files/list?semester=${encodeURIComponent(selectedSemester)}&module=${encodeURIComponent(selectedModule)}&fachrichtung=${encodeURIComponent(selectedFachrichtung)}&tag=${encodeURIComponent(selectedTag)}`, {
            method: 'GET',
            headers: {
                "Authorization": "Basic " + btoa(`${username}:${password}`)
            }
        });

        if (!response.ok) {
            throw new Error('Netzwerkantwort war nicht ok.');
        }

        const files = await response.json();
        displayFileList(files);
    } catch (error) {
        console.error('Fehler beim Abrufen der Dateiliste:', error);
    }

}

/**
 * downloads file with fileName from database
 * communicates with backend
 * @param {*} fileName
 */

function downloadFile(fileName) {
    const downloadUrl = `/files/download?fileName=${encodeURIComponent(fileName)}`;
    window.location.href = downloadUrl;
}

/**
 * deletes file from the database
 * communicates with backend
 * @param {*} fileName
 * @param {*} selectedSemester
 * @param {*} selectedModule
 * @param {*} selectedFachrichtung
 * @param {*} tag
 * @returns
 */

async function deleteFile(fileName, selectedSemester, selectedModule, selectedFachrichtung, tag) {
    if (confirm("Möchten Sie die Datei wirklich löschen?")) {
        if (!fileName) {
            console.error('File name not found');
            return;
        }

        try {
            const response = await fetch(`/delete-file?name=${encodeURIComponent(fileName)}&semester=${encodeURIComponent(selectedSemester)}&module=${encodeURIComponent(selectedModule)}&fachrichtung=${encodeURIComponent(selectedFachrichtung)}&tag=${encodeURIComponent(tag)}`, {
                method: 'DELETE',
                headers: {
                    'X-XSRF-TOKEN': getCsrfToken()
                }
            });

            if (response.ok) {
                const result = await response.text();
                console.log(result);
                fetchFileList();
            } else {
                console.error('Failed to delete file');
            }
        } catch (error) {
            console.error('Error:', error);
            console.error('An error occurred while deleting the file');
        }
    } else {
        console.log("Löschen abgebrochen");
    }
}


/**
 * searches files in the database
 * communicates with backend
 */

async function searchFiles() {
    const query = document.getElementById('searchQuery').value;
    const selectedSemester = dropdownValue.value;
    const selectedModule = moduleValue.value;
    const selectedFachrichtung = fachrichtungValue.value;
    const selectedTag = tag.value;

    try {
        const response = await fetch(`/api/search?query=${encodeURIComponent(query)}&semester=${encodeURIComponent(selectedSemester)}&module=${encodeURIComponent(selectedModule)}&fachrichtung=${encodeURIComponent(selectedFachrichtung)}&tag=${encodeURIComponent(selectedTag)}`);
        const results = await response.json();
        displayFileList(results.map(file => file.name));
    } catch (error) {
        console.error('Fehler bei der Suche:', error);
    }
}

searchBar.addEventListener('input', searchFiles);

/**
 * gets files from database and previews the
 * @param {*} fileName
 */

async function previewFile(fileName) {
    try {
        const response = await fetch(`/files/preview?name=${encodeURIComponent(fileName)}`);
        const fileContent = await response.blob();

        const modal = document.getElementById('previewModal');
        const modalContent = document.getElementById('modalContent');

        const fileURL = URL.createObjectURL(fileContent);

        modalContent.innerHTML = '';

        if (fileContent.type.startsWith('image/')) {
            const img = document.createElement('img');
            img.src = fileURL;
            img.style.width = '100%';
            modalContent.appendChild(img);
        } else if (fileContent.type === 'application/pdf') {
            const iframe = document.createElement('iframe');
            iframe.src = fileURL;
            iframe.style.width = '100%';
            iframe.style.height = '500px';
            modalContent.appendChild(iframe);
        } else {
            const p = document.createElement('p');
            p.textContent = 'Dateivorschau nicht unterstützt.';
            modalContent.appendChild(p);
        }

        modal.style.display = 'block';
    } catch (error) {
        console.error('Fehler beim Abrufen der Dateivorschau:', error);
    }
}

/**
 * changes searchBar display style from "none" to "flex"
 */

function showSearchBar() {
    const searchBar = document.getElementById('searchBar');

    searchBar.style.display = 'block';
}

/**
 * Event-Listener if site is fully loaded
 */

document.addEventListener('DOMContentLoaded', (event) => {

    // Modal schließen
    document.querySelector('.close').onclick = function() {
        document.getElementById('previewModal').style.display = 'none';
    };

    window.onclick = function(event) {
        const modal = document.getElementById('previewModal');
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    };
});

fileInput.addEventListener('change', () => {
    if (fileInput.files.length) {
        const MAX_FILE_NAME_LENGTH = 60; // Maximale Länge des Dateinamens
        const validFiles = []; // Liste für gültige Dateien

        // Überprüfen der Dateinamen und nur gültige hinzufügen
        for (let i = 0; i < fileInput.files.length; i++) {
            const fileName = fileInput.files[i].name;
            if (fileName.length <= MAX_FILE_NAME_LENGTH) {
                validFiles.push(fileInput.files[i]); // Nur gültige Dateien hinzufügen
            } else {
                alert(`Der Dateiname "${fileName}" ist zu lang. Maximal ${MAX_FILE_NAME_LENGTH} Zeichen sind erlaubt.`);
            }
        }

        // Setze nur gültige Dateien in das fileUploadInput
        if (validFiles.length > 0) {
            const dataTransfer = new DataTransfer(); // Neues DataTransfer-Objekt erstellen
            validFiles.forEach(file => dataTransfer.items.add(file));
            fileUploadInput.files = dataTransfer.files; // Nur gültige Dateien setzen
            updateFileList(validFiles); // Liste mit gültigen Dateien aktualisieren
        } else {
            fileUploadInput.files = new DataTransfer().files; // Input zurücksetzen
            fileList.innerHTML = ''; // Leere Liste anzeigen
        }
    }
});



document.addEventListener('DOMContentLoaded', updateSelectedValues);
document.addEventListener('DOMContentLoaded', hideProgressBar);


