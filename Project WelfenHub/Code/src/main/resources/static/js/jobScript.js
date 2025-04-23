const createPopUp = document.getElementById('createPopUp');
const removePopUp = document.getElementById('removePopUp');

const title = document.getElementById('title');
const content = document.getElementById('content');
const url = document.getElementById('url');
const colorPicker = document.getElementById('colorPicker');
const colorText = document.getElementById('colorText');
const status = document.getElementById('status');

const jobArea = document.getElementById('jobArea');
const toRemoveList = document.getElementById('toRemoveList');

let csrfToken = getCsrfToken();

function showCreate() {
    if (createPopUp.style.display === "none" || createPopUp.style.display === "") {
        // Popup öffnen und Felder leeren
        createPopUp.style.display = "block";
        title.value = "";
        content.value = "";
        url.value = "";
        colorPicker.value = "#ffffff";
        colorText.value = "#ffffff";
        status.textContent = "";
    } else {
        // Popup schließen
        createPopUp.style.display = "none";
    }
}


function showRemove() {
    if (removePopUp.style.display === "none") {
        removePopUp.style.display = "block";
        toRemoveList.innerHTML = "";
        getToRemoveJobs();
    } else {
        removePopUp.style.display = "none";
    }
}

colorPicker.addEventListener('input', () => {
    colorText.value = colorPicker.value;
});

colorText.addEventListener('input', () => {
    if (/^#([A-Fa-f0-9]{6})$/.test(colorText.value)) {
        colorPicker.value = colorText.value;
    }
});

async function submitNewJob() {
    const titleValue = title.value;
    const contentValue = content.value;
    const urlValue = url.value;
    const colorValue = colorText.value;

    const response = await fetch("/job/post", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken,
        },
        body: JSON.stringify({
            "title": titleValue,
            "content": contentValue,
            "url": urlValue,
            "color": colorValue
        }),
    });

    if (response.status === 200) {
        status.textContent = "Das Inserat wurde erfolgreich erstellt";
        status.style.color = "green";
        await wait(2000);
        status.innerHTML = "";
    } else {
        status.textContent = "Technischer Fehler: Inserat wurde nicht erstellt";
        status.style.color = "red";
        await wait(6000);
        status.innerHTML = "";
    }

    if (createPopUp.style.display === "block") {
        showCreate();
    }

    jobArea.innerHTML = "";
    await getEvents();
}

async function getEvents() {
    const response = await fetch("/job/get", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken
        }
    });

    const data = await response.json();

    for (let i = 0; i < data.length; i++) {
        const divJob = document.createElement('div');
        divJob.className = "jobContainer";

        const color = data[i].color || "#cccccc";

        divJob.style.borderColor = color;
        divJob.style.backgroundColor = hexToRGBA(color, 0.07);

        const divTitle = document.createElement("h5");
        divTitle.className = "jobTitle";
        divTitle.textContent = data[i].title;

        const divJobContent = document.createElement("p");
        divJobContent.className = "divContent";
        divJobContent.innerHTML = data[i].content.replace(/\n\n/g, "</p><p>").replace(/\n/g, "<br>");

        const divJobUrl = document.createElement("a");
        divJobUrl.href = data[i].url;
        divJobUrl.className = "divUrl";
        divJobUrl.textContent = data[i].url;
        divJobUrl.target = "_blank";

        divJob.appendChild(divTitle);
        divJob.appendChild(divJobContent);
        divJob.appendChild(divJobUrl);
        jobArea.appendChild(divJob);
    }
}

function hexToRGBA(hex, alpha) {
    const r = parseInt(hex.slice(1, 3), 16);
    const g = parseInt(hex.slice(3, 5), 16);
    const b = parseInt(hex.slice(5, 7), 16);
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
}

async function getToRemoveJobs() {
    const response = await fetch("/job/get", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken
        }
    });

    const data = await response.json();

    for (let i = 0; i < data.length; i++) {
        const divTitle = document.createElement("span");
        divTitle.className = "removeJobTitle";
        divTitle.textContent = data[i].title;
        divTitle.title = "Entfernen";

        divTitle.onclick = async function () {
            await fetch("/job/delete", {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    "X-XSRF-TOKEN": csrfToken,
                },
                body: JSON.stringify({
                    "title": data[i].title,
                    "content": data[i].content,
                    "url": data[i].url
                }),
            });

            toRemoveList.innerHTML = "";
            await getToRemoveJobs();
            jobArea.innerHTML = "";
            await getEvents();
        };

        toRemoveList.appendChild(divTitle);
    }
}

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    return match ? match[1] : null;
}

function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

document.addEventListener("DOMContentLoaded", function () {
    createPopUp.style.display = "none";
    removePopUp.style.display = "none";
    getEvents();
});
