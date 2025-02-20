const newEventPopUp = document.getElementById('createEventPopUp');
const title = document.getElementById('title');
const content = document.getElementById('content');
const status = document.getElementById('status');
const newsArea = document.getElementById('newsArea');
const removeEvent = document.getElementById('removeEventPopUp');
const removeList = document.getElementById('toRemoveList');

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

    getToRemoveEvents();
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

    const data =  await response.json();

    for (let i = data.length - 1; i >= 0; i--) {
        const removeLi = document.createElement('span');

        removeLi.className = "removeList";

        removeLi.textContent = '"' + data[i].title + '"' + " vom " + data[i].date.replace(/-/g, ".");
        removeLi.title = "Entfernen";
        removeLi.onclick = function() {
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

    await getEvents();

    await getToRemoveEvents();

}

/**
 *  sends new event to back end
 */

async function publishEvent() {

    const titleValue = title.value;
    const contentValue = content.value;

    if (titleValue === "" || contentValue === "") {
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
            "content": contentValue
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

    await getEvents();

}

document.addEventListener("DOMContentLoaded", getEvents);

/**
 * gets all events from back end and shows them on page
 * @returns {Promise<void>}
 */

async function getEvents() {
    const response = await fetch("http://localhost:8080/event/get-event", {
        method: "GET",
        headers: {
            "X-XSRF-TOKEN": csrfToken,
        }
    });

    const data = await response.json();


    for (let i = data.length - 1; i >= 0; i--) {
        const newsDiv = document.createElement('div');
        const newsH3 = document.createElement('H3');
        const newsText = document.createElement('span');
        const newsDate = document.createElement('div');

        newsDiv.className = "newsDiv";

        const date = data[i].date;
        const dateFront = date.replace(/-/g, ".");

        newsH3.textContent = data[i].title;
        newsText.innerHTML = data[i].content.replace(/\n\n/g, "</p><p>").replace(/\n/g, "<br>");
        newsDate.textContent = dateFront;

        newsArea.appendChild(newsDiv);
        newsDiv.appendChild(newsH3);
        newsDiv.appendChild(newsText);
        newsDiv.appendChild(newsDate);
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