document.addEventListener("DOMContentLoaded", () => {
  const icon = document.getElementById("chatbot-icon");
  const chatWindow = document.getElementById("chatbot-window");
  const closeBtn = document.getElementById("chatbot-close");
  const input = document.getElementById("chatbot-input");
  const sendBtn = document.getElementById("chatbot-send");
  const messages = document.getElementById("chatbot-messages");

  if (!icon || !chatWindow || !closeBtn || !input || !sendBtn || !messages) return;

  const RASA_URL = "http://localhost:5005/webhooks/rest/webhook";
  const senderId = "web_user_" + Date.now();

  /* ===============================
     OPEN / CLOSE
  =============================== */
  icon.addEventListener("click", () => {
    chatWindow.classList.toggle("hidden");
  });

  closeBtn.addEventListener("click", () => {
    chatWindow.classList.add("hidden");
  });

  /* ===============================
     DRAG ICON
  =============================== */
  let isDragging = false;
  let offsetX = 0;
  let offsetY = 0;

  icon.addEventListener("mousedown", (e) => {
    isDragging = true;
    offsetX = e.clientX - icon.getBoundingClientRect().left;
    offsetY = e.clientY - icon.getBoundingClientRect().top;
    icon.style.cursor = "grabbing";
  });

  document.addEventListener("mousemove", (e) => {
    if (!isDragging) return;

    icon.style.left = e.clientX - offsetX + "px";
    icon.style.top = e.clientY - offsetY + "px";
    icon.style.bottom = "auto";
    icon.style.right = "auto";
  });

  document.addEventListener("mouseup", () => {
    isDragging = false;
    icon.style.cursor = "grab";
  });

  /* ===============================
     CHAT LOGIC
  =============================== */
  function addMessage(text, type) {
    const msg = document.createElement("div");
    msg.className = `msg ${type}`;
    msg.innerText = text;
    messages.appendChild(msg);
    messages.scrollTop = messages.scrollHeight;
  }

  async function sendMessage() {
    const question = input.value.trim();
    if (!question) return;

    addMessage(question, "user");
    input.value = "";

    const typing = document.createElement("div");
    typing.className = "msg bot";
    typing.innerText = "Đang trả lời...";
    messages.appendChild(typing);
    messages.scrollTop = messages.scrollHeight;

    try {
      const res = await fetch(RASA_URL, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          sender: senderId,
          message: question
        })
      });

      if (!res.ok) {
        throw new Error(`HTTP error: ${res.status}`);
      }

      const data = await res.json();
      typing.remove();

      if (!Array.isArray(data) || data.length === 0) {
        addMessage("Tôi chưa có câu trả lời phù hợp.", "bot");
        return;
      }

      data.forEach((item) => {
        if (item.text) {
          addMessage(item.text, "bot");
        }
      });
    } catch (err) {
      typing.remove();
      addMessage("❌ Không thể kết nối tới Rasa chatbot.", "bot");
      console.error("Chatbot error:", err);
    }
  }

  /* ===============================
     EVENTS
  =============================== */
  sendBtn.addEventListener("click", sendMessage);

  input.addEventListener("keydown", (e) => {
    if (e.key === "Enter") {
      sendMessage();
    }
  });
});