const newEventPopUp = document.getElementById('createEventPopUp');
const title = document.getElementById('title');
const content = document.getElementById('content');
const status = document.getElementById('status');
const newsArea = document.getElementById('newsArea');
const removeEvent = document.getElementById('removeEventPopUp');
const removeList = document.getElementById('toRemoveList');
const pages = document.getElementById('page');
const showEditEventPage = document.getElementById('editEventPopUp');
const editList = document.getElementById('toEditList');
const editSelected = document.getElementById('editSelectedEventPopUp');
const editInput = document.getElementById('editInput');
const editTextarea = document.getElementById('editTextarea');
const eventName = document.getElementById('eventName');

const csrfToken = getCsrfToken();

/**
 *  shows or removes pop up to create event if button is pressed
 */

function showAddEvent() {
    if (newEventPopUp.style.display === "block") {
        newEventPopUp.style.display = "none";
    } else {
        removeEvent.style.display = "none";
        newEventPopUp.style.display = "block";
    }

    showEditEventPage.style.display = "none";
    editSelected.style.display = "none";
}

/**
 * shows or removes pop up to remove event
 */

function showRemoveEvent() {
    if (removeEvent.style.display === "block") {
        removeEvent.style.display = "none";
    } else {
        newEventPopUp.style.display = "none";
        removeEvent.style.display = "block";
    }

    removeList.innerHTML = "";

    showEditEventPage.style.display = "none";
    editSelected.style.display = "none";

    getToRemoveEvents();
}

/**
 * shows pop up to select event which should be edited
 */

function showEditEvent() {
    if (showEditEventPage.style.display === "block") {
        showEditEventPage.style.display = "none";
    } else {
        showEditEventPage.style.display = "block";
    }

    editList.innerHTML = "";

    removeEvent.style.display = "none";
    newEventPopUp.style.display = "none";

    getToEditEvents();
}

/**
 * shows pop up in which you can edit the selected event
 */

function showEventToEdit() {
    if (editSelected.style.display === "none") {
        editSelected.style.display = "block";
    } else {
        editSelected.style.display = "none";
    }

    removeEvent.style.display = "none";
    newEventPopUp.style.display = "none";
}

/**
 * gets all function which can be edited
 * @returns {Promise<void>}
 */

async function getToEditEvents() {
    const response = await fetch("http://localhost:8080/event/get-event", {
        method: "GET",
        headers: {
            "X-XSRF-TOKEN": csrfToken,
        }
    });

    const data = await response.json();

    for (let i = data.length - 1; i >= 0; i--) {
        const removeLi = document.createElement('span');

        removeLi.className = "removeList";

        removeLi.textContent = '"' + data[i].title + '"' + " vom " + data[i].date.replace(/-/g, ".");
        removeLi.title = "Bearbeiten";
        removeLi.onclick = function () {
            showEventToEdit();
            editInput.value = data[i].title;
            editTextarea.textContent = data[i].content;
        }
        editList.appendChild(removeLi);
    }
}

/**
 * submits update
 * @returns {Promise<void>}
 */

async function submitUpdate() {
    const textAreaValue = editTextarea.value;
    const editInputValue = editInput.value;

    const response = await fetch("http://localhost:8080/event/update-event", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken,
        },
        body: JSON.stringify({
            "title": editInputValue,
            "content": textAreaValue
        }),
    });

    showEditEventPage.style.display = "none";
    editSelected.style.display = "none";

    newsArea.innerHTML = "";

    showFirstPage();
}

/**
 * gets all events which can be removed
 * @returns {Promise<void>}
 */

async function getToRemoveEvents() {
    const response = await fetch("http://localhost:8080/event/get-event", {
        method: "GET",
        headers: {
            "X-XSRF-TOKEN": csrfToken,
        }
    });

    const data = await response.json();

    for (let i = data.length - 1; i >= 0; i--) {
        const removeLi = document.createElement('span');

        removeLi.className = "removeList";

        removeLi.textContent = '"' + data[i].title + '"' + " vom " + data[i].date.replace(/-/g, ".");
        removeLi.title = "Entfernen";
        removeLi.onclick = function () {
            removeEventFunc(data[i].title, data[i].content, data[i].date);
        }

        removeList.appendChild(removeLi);
    }
}

/**
 * sends http request to remove event
 * @param title
 * @param content
 * @param date
 * @returns {Promise<void>}
 */

async function removeEventFunc(title, content, date) {
    const response = await fetch("http://localhost:8080/event/remove", {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken,
        },
        body: JSON.stringify({
            "title": title,
            "content": content,
            "date": date
        }),
    });


    newsArea.innerHTML = "";

    removeList.innerHTML = "";

    await showFirstPage();

    await getToRemoveEvents();

    await showPage();

}

/**
 *  sends new event to back end
 */

async function publishEvent() {

    const titleValue = title.value;
    const contentValue = content.value;
    const nameValue = eventName.value;

    if (titleValue === "" || contentValue === "" || nameValue === "") {
        status.textContent = "Bitte fülle die Felder aus";
        status.style.color = "red";

        return;
    }

    const response = await fetch("http://localhost:8080/event/post", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-XSRF-TOKEN": csrfToken,
        },
        body: JSON.stringify({
            "title": titleValue,
            "content": contentValue,
            "author": nameValue
        }),
    });

    if (response.ok) {
        status.textContent = "Das Ereignis wurde erstellt";
        status.style.color = "green";
        await wait(2000);
        status.innerHTML = "";
    } else {
        status.textContent = "Technischer Fehler: Ereignis wurde nicht erstellt";
        status.style.color = "red";
        status.innerHTML = ""
        await wait(6000);
    }

    newEventPopUp.style.display = "none";

    newsArea.innerHTML = "";

    await showPage();

    await showFirstPage();

}

/**
 * shows page section and function to change page
 * @returns {Promise<void>}
 */


async function showPage() {

    pages.innerHTML = "";

    const response = await fetch("http://localhost:8080/event/get-event-count", {
        method: "GET",
        headers: {
            "X-XSRF-TOKEN": csrfToken,
        }
    });

    const data = await response.json();

    let numPages = data / 5;

    numPages = Math.ceil(numPages);

    // TODO: hinzufügen, dass Seitenzahlen bold sind, wenn man auf der jeweiligen Seite ist

    for (let i = 1; i <= numPages; i++) {
        const page = document.createElement('span');
        page.className = "page";
        page.textContent = " " + i.toString() + " ";
        page.onclick = async function () {

            newsArea.innerHTML = "";

            const response = await fetch(`http://localhost:8080/event/show?page=${encodeURIComponent(i)}`);

            const data = await response.json();

            for (let i = 0; i < data.length; i++) {
                const newsDiv = document.createElement('div');
                const newsH3 = document.createElement('H3');
                const newsText = document.createElement('p');
                const newsDate = document.createElement('div');
                const newsAuthor = document.createElement('div');

                newsDiv.className = "newsDiv";

                const date = data[i].date;
                const dateFront = date.replace(/-/g, ".");

                newsAuthor.textContent = data[i].author;
                newsH3.textContent = data[i].title;
                newsText.innerHTML = data[i].content.replace(/\n\n/g, "</p><p>").replace(/\n/g, "<br>");
                newsDate.textContent = dateFront;

                newsArea.appendChild(newsDiv);
                newsDiv.appendChild(newsAuthor);
                newsDiv.appendChild(newsDate);
                newsDiv.appendChild(newsH3);
                newsDiv.appendChild(newsText);

                window.scrollTo(0, 0);
            }
        }
        pages.appendChild(page);
    }
}

/**
 * gets XSRF-Token for HTTP-Header
 * @returns {string} XSRF-Token
 */

function getCsrfToken() {
    const match = document.cookie.match(/XSRF-TOKEN=([^;]+)/);
    const token = match ? match[1] : null;
    return token;
}

/**
 * stops function for a limited time
 * @param ms time in milliseconds
 * @returns {Promise<unknown>}
 */

function wait(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

document.addEventListener('DOMContentLoaded', showPage);

/**
 * shows first page
 * @returns {Promise<void>}
 */

async function showFirstPage() {
    const response = await fetch(`http://localhost:8080/event/show?page=1`);

    const data = await response.json();

    console.log(data);

    for (let i = 0; i < data.length; i++) {
        const newsDiv = document.createElement('div');
        const newsH3 = document.createElement('H3');
        const newsText = document.createElement('p');
        const newsDate = document.createElement('div');
        const newsAuthor = document.createElement('div');

        newsDiv.className = "newsDiv";


        const date = data[i].date;
        const dateFront = date.replace(/-/g, ".");

        newsAuthor.textContent = data[i].author;
        newsH3.textContent = data[i].title;
        newsText.innerHTML = data[i].content.replace(/\n\n/g, "</p><p>").replace(/\n/g, "<br>");
        newsDate.textContent = dateFront;

        newsArea.appendChild(newsDiv);
        newsDiv.appendChild(newsAuthor);
        newsDiv.appendChild(newsDate);
        newsDiv.appendChild(newsH3);
        newsDiv.appendChild(newsText);
    }
}

document.addEventListener('DOMContentLoaded', showFirstPage);

document.addEventListener("DOMContentLoaded", function () {
    editSelected.style.display = "none";
});