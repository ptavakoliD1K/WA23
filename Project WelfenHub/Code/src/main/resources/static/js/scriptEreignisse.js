const newEventPopUp = document.getElementById('createEventPopUp');
const createNewNews = document.getElementById('createNewNews');
const title = document.getElementById('title');
const content = document.getElementById('content');
const publish = document.getElementById('publishForm');
const status = document.getElementById('status');

const csrfToken = getCsrfToken();

/**
 *  shows or removes pop up to create event if button is pressed
 */

createNewNews.addEventListener("submit", (e) => {
    e.preventDefault();

    if (newEventPopUp.style.display === "block") {
        newEventPopUp.style.display = "none";
    } else {
        newEventPopUp.style.display = "block";
    }
});

/**
 *  sends new event to back end
 */

publish.addEventListener("submit", async (e) => {
    e.preventDefault();

    const titleValue = title.value;
    const contentValue = content.value;

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
    } else {
        status.textContent = "Technischer Fehler: Ereignis wurde nicht erstellt";
        status.style.color = "red";
        await wait(6000);
    }

    newEventPopUp.style.display = "none";

    await getEvents();

});

document.addEventListener("DOMContentLoaded", getEvents);

async function getEvents() {
    const response = await fetch("http://localhost:8080/event/get-event", {
        method: "GET",
        headers: {
            "X-XSRF-TOKEN": csrfToken,
        }
    });

    const data = await response.json();

    console.log(data)
    console.log(data.length);
    console.log(data[2])
    console.log(data[2].title)

    for (let i = data.length; i <= 0; i--) {

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