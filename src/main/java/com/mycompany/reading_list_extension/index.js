console.log("chrome:", typeof chrome);
console.log("chrome.tabs:", typeof chrome?.tabs);

document.getElementById("sendBtn").addEventListener("click", () => {
    chrome.tabs.query({ active: true, currentWindow: true }, (tabs) => {
        console.log("tabs:", tabs);
        console.log("tab:", tabs[0]);
        console.log("url:", tabs[0]?.url);
        const currentTab = tabs[0];
        fetch("http://localhost:8080", {
            method: "POST",
            headers: { "Content-Type": "text/plain" },
            body: currentTab?.url
        })
        .then(res => res.text())
        .then(data => console.log("Server response:", data))
        .catch(err => console.error("Error:", err));
    });
});