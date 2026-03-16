/* ===============================
FLASH SALE COUNTDOWN
================================ */

document.addEventListener("DOMContentLoaded", function () {

  const el = document.getElementById("countdown");

  if (!el) return;

  let seconds = 3799;

  function updateCountdown(){

    if (seconds < 0) {
      seconds = 3799;
    }

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
CHAT AI
================================ */

document.addEventListener("DOMContentLoaded", function(){

  const toggle = document.getElementById("chatToggle");
  const chat = document.getElementById("chatWindow");
  const closeBtn = document.getElementById("chatClose");

  if(!toggle || !chat) return;

  toggle.onclick = () => {

    if(chat.style.display === "flex" || chat.style.display === "block"){
      chat.style.display = "none";
    }else{
      chat.style.display = "flex";
    }

  };

  if(closeBtn){
    closeBtn.onclick = () => {
      chat.style.display = "none";
    };
  }

});