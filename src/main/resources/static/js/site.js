/* ===============================
FLASH SALE COUNTDOWN
================================ */

document.addEventListener("DOMContentLoaded", function () {
  const el = document.getElementById("countdown");
  if (!el) return;

  let seconds = 3799;

  function updateCountdown() {
    if (seconds < 0) seconds = 3799;

    const h = String(Math.floor(seconds / 3600)).padStart(2, "0");
    const m = String(Math.floor((seconds % 3600) / 60)).padStart(2, "0");
    const s = String(seconds % 60).padStart(2, "0");

    el.textContent = `${h} : ${m} : ${s}`;
    seconds--;
  }

  updateCountdown();
  setInterval(updateCountdown, 1000);
});


/* ===============================
CHAT AI TGDD
================================ */

function toggleChat() {
  const box = document.getElementById("chatWindow");
  if (!box) return;

  if (box.style.display === "flex") {
    box.style.display = "none";
  } else {
    box.style.display = "flex";
  }
}

document.addEventListener("DOMContentLoaded", function () {
  const toggle = document.getElementById("chatToggle");
  const close = document.getElementById("chatClose");
  const sendBtn = document.getElementById("chatSend");

  if (toggle) toggle.onclick = toggleChat;
  if (close) close.onclick = toggleChat;
  if (sendBtn) sendBtn.onclick = sendChat;
});


/* ===============================
CHAT MESSAGE
================================ */

function addMessage(text, type) {
  const body = document.getElementById("chatBody");
  if (!body) return;

  const div = document.createElement("div");
  div.className = "chat-message " + type;
  div.innerText = text;

  body.appendChild(div);
  body.scrollTop = body.scrollHeight;
}

function addTyping() {
  const body = document.getElementById("chatBody");
  if (!body) return;

  const div = document.createElement("div");
  div.className = "chat-message ai typing";
  div.id = "aiTyping";
  div.innerText = "AI đang trả lời...";

  body.appendChild(div);
  body.scrollTop = body.scrollHeight;
}

function removeTyping() {
  const typing = document.getElementById("aiTyping");
  if (typing) typing.remove();
}


/* ===============================
SEND CHAT
================================ */

function sendChat() {
  const input = document.getElementById("chatInput");
  if (!input) return;

  const message = input.value.trim();
  if (message === "") return;

  addMessage(message, "user");
  input.value = "";
  addTyping();

  fetch("/api/chat", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ message: message })
  })
    .then(async (res) => {
      if (!res.ok) {
        throw new Error("Server response error");
      }
      return res.json();
    })
    .then((data) => {
      removeTyping();

      if (data && data.reply) {
        addMessage(data.reply, "ai");
      } else {
        addMessage("Xin lỗi AI chưa trả lời được.", "ai");
      }
    })
    .catch(() => {
      removeTyping();
      addMessage("Xin lỗi, hệ thống đang bận.", "ai");
    });
}

document.addEventListener("DOMContentLoaded", function () {
  const input = document.getElementById("chatInput");
  if (!input) return;

  input.addEventListener("keydown", function (e) {
    if (e.key === "Enter") {
      e.preventDefault();
      sendChat();
    }
  });
});


/* ===============================
HOME BANNER SLIDER
================================ */

document.addEventListener("DOMContentLoaded", function () {
  const slider = document.querySelector(".slider");
  const slides = document.querySelector(".slides");

  if (!slider || !slides) return;

  const images = document.querySelectorAll(".slides img");
  const totalSlides = images.length;

  if (totalSlides === 0) return;

  let slideIndex = 0;
  let autoSlide = null;

  function updateSlide() {
    const width = slider.offsetWidth;
    slides.style.transform = "translateX(-" + (slideIndex * width) + "px)";
  }

  function nextSlide() {
    slideIndex++;
    if (slideIndex >= totalSlides) {
      slideIndex = 0;
    }
    updateSlide();
  }

  function prevSlide() {
    slideIndex--;
    if (slideIndex < 0) {
      slideIndex = totalSlides - 1;
    }
    updateSlide();
  }

  const nextBtn = slider.querySelector(".next");
  const prevBtn = slider.querySelector(".prev");

  if (nextBtn) {
    nextBtn.onclick = function () {
      nextSlide();
    };
  }

  if (prevBtn) {
    prevBtn.onclick = function () {
      prevSlide();
    };
  }

  function startAuto() {
    stopAuto();
    autoSlide = setInterval(nextSlide, 3000);
  }

  function stopAuto() {
    if (autoSlide) {
      clearInterval(autoSlide);
      autoSlide = null;
    }
  }

  slider.addEventListener("mouseenter", stopAuto);
  slider.addEventListener("mouseleave", startAuto);

  updateSlide();
  if (totalSlides > 1) {
    startAuto();
  }

  window.addEventListener("resize", updateSlide);
});