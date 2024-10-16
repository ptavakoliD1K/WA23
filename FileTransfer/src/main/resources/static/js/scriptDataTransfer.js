const dropZone = document.getElementById('drop_zone');
const fileInput = document.getElementById('file-input');
const uploadForm = document.getElementById('upload-form');
const fileUploadInput = document.getElementById('file-upload-input');
const fileList = document.getElementById('file-list');
const progressBarContainer = document.getElementById('progress-bar-container');
const progressBar = document.getElementById('progress-bar');
const message = document.getElementById('message');

function updateFileList(files) {
    fileList.innerHTML = '';
    for (let i = 0; i < files.length; i++) {
        const fileItem = document.createElement('p');
        fileItem.textContent = files[i].name;
        fileList.appendChild(fileItem);
    }
}

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

uploadForm.addEventListener('submit', (e) => {
    e.preventDefault();
    if (!fileUploadInput.files.length) {
        alert('Please select files to upload');
        return;
    }

    const formData = new FormData();
    for (let i = 0; i < fileUploadInput.files.length; i++) {
        formData.append('files[]', fileUploadInput.files[i]);
    }

    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/upload', true);

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
            message.textContent = 'Files uploaded successfully!';
            message.style.color = '#28a745';
            fetchFileList();
        } else {
            message.textContent = 'File upload failed!';
            message.style.color = '#dc3545';
        }
        progressBarContainer.style.display = 'none';
    };

    xhr.onerror = () => {
        message.textContent = 'File upload failed!';
        message.style.color = '#dc3545';
        progressBarContainer.style.display = 'none';
    };

    xhr.send(formData);
});

document.addEventListener('DOMContentLoaded', function() {
    fetchFileList();
});

function downloadFile(fileName) {
    const downloadUrl = `/files/download?fileName=${encodeURIComponent(fileName)}`;
    window.location.href = downloadUrl;
}

async function deleteFile(fileName) {
    if (!fileName) {
        console.error('File name not found');
        return;
    }

    try {
        const response = await fetch(`/delete-file?name=${encodeURIComponent(fileName)}`, {
            method: 'DELETE'
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
}

async function fetchFileList() {
    try {
        const response = await fetch('/files/list');
        const data = await response.json();
        displayFiles(data);
    } catch (error) {
        console.error('Fehler beim Abrufen der Dateiliste:', error);
    }
}

async function searchFiles() {
    const query = document.getElementById('searchQuery').value;
    try {
        const response = await fetch(`/api/search?query=${encodeURIComponent(query)}`);
        const results = await response.json();
        displayFiles(results.map(file => file.name));
    } catch (error) {
        console.error('Fehler bei der Suche:', error);
    }
}

function displayFiles(files) {
    const fileList = document.getElementById('fileList');
    fileList.innerHTML = '';
    files.forEach(fileName => {
        const listItem = document.createElement('li');
        listItem.innerHTML = `
                    <button style="display: block" class="downloadButton" type="button" onclick="downloadFile('${fileName}')">Herunterladen</button>
                    <button style="display: block" class="deleteButton" type="button" onclick="deleteFile('${fileName}')">Löschen</button>
                    <button class="previewButton" type="button" onclick="previewFile('${fileName}')">Vorschau</button>
                    <span class="fileName">${fileName}</span>
                `;
        fileList.appendChild(listItem);
    });
}

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

document.addEventListener('DOMContentLoaded', (event) => {
    fetchFileList();

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
