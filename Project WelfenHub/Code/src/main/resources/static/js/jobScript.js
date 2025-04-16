const createPopUp = document.getElementById('createPopUp');
const removePopUp = document.getElementById('removePopUp');

const title = document.getElementById('title');
const content = document.getElementById('content');
const url = document.getElementById('url');
const status = document.getElementById('status');

const jobArea = document.getElementById('jobArea');

const toRemoveList = document.getElementById('toRemoveList');

let csrfToken = getCsrfToken();

/**
 * shows pop up to create job
 */

function showCreate() {
    if (createPopUp.style.display === "none") {
        createPopUp.style.display = "block";
    } else {
        createPopUp.style.display = "none";
    }
}

/**
 * shows pop up to remove job
 */

function showRemove() {
    if (removePopUp.style.display === "none") {
        removePopUp.style.display = "block";
        toRemoveList.innerHTML = "";
        getToRemoveJobs();
    } else {
        removePopUp.style.display = "none";
    }
}

/**
 * submits new job to backend
 * @returns {Promise<void>}
 */

async function submitNewJob() {
    titleValue = title.value;
    contentValue = content.value;
    urlValue = url.value;

    const response = await fetch("/job/post", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken,
        },
        body: JSON.stringify({
            "title": titleValue,
            "content": contentValue,
            "url": urlValue
        }),
    });

    if (response.status === 200) {
        status.textContent = "Das Inserat wurde erfolgreich erstellt"
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

/**
 * gets events from backend
 * @returns {Promise<void>}
 */

async function getEvents() {
    const response = await fetch("/job/get", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken
        }
    })

    const data = await response.json();

    for (let i = 0; i < data.length; i++) {
        const divJob = document.createElement('div');
        divJob.className = "jobContainer";

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

        divJob.appendChild(divTitle);
        divJob.appendChild(divJobContent);
        divJob.appendChild(divJobUrl);
        jobArea.appendChild(divJob);
    }
}

/**
 * gets job list for remove popup
 * @returns {Promise<void>}
 */

async function getToRemoveJobs() {
    const response = await fetch("/job/get", {
        method: "GET",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken
        }
    })

    const data = await response.json();

    for (let i = 0; i < data.length; i++) {
        const divTitle = document.createElement("span");
        divTitle.className = "removeJobTitle";
        divTitle.textContent = data[i].title;
        divTitle.title = "Entfernen";

        divTitle.onclick = async function() {
            const response = await fetch("/job/delete", {
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
            })

            toRemoveList.innerHTML = "";

            await getToRemoveJobs();

            jobArea.innerHTML = "";

            await getEvents();

        }

        toRemoveList.appendChild(divTitle);
    }
}

document.addEventListener('DOMContentLoaded', getEvents);

/**
 * gets csrf token
 * @returns {string}
 */

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    const token = match ? match[1] : null;
    return token;
}

/**
 * pauses function
 * @param ms
 * @returns {Promise<unknown>}
 */

function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * event listener
 */

document.addEventListener("DOMContentLoaded", function () {
    createPopUp.style.display = "none";
    removePopUp.style.display = "none";
});