/* ===============================
   FLASH SALE COUNTDOWN
================================ */

function initFlashSaleCountdown() {
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
}

/* ===============================
   CHAT AI TGDD
================================ */

function toggleChat(forceOpen = null) {
  const box = document.getElementById("chatWindow");
  if (!box) return;

  const isOpen = box.style.display === "flex";

  if (forceOpen === true) {
    box.style.display = "flex";
    return;
  }

  if (forceOpen === false) {
    box.style.display = "none";
    return;
  }

  box.style.display = isOpen ? "none" : "flex";
}

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

  removeTyping();

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
  toggleChat(true);

  const csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

  const headers = {
    "Content-Type": "application/json"
  };

  if (csrfToken && csrfHeader) {
    headers[csrfHeader] = csrfToken;
  }

  fetch("/api/chat", {
    method: "POST",
    headers: headers,
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

/* ===============================
   HOME BANNER SLIDER
================================ */

function initHomeSlider() {
  const slider = document.querySelector(".slider");
  const slides = document.querySelector(".slides");

  if (!slider || !slides) return;

  const images = slider.querySelectorAll(".slides img");
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
}

/* ===============================
   WISHLIST STORAGE
================================ */

function getWishlistProducts() {
  try {
    return JSON.parse(localStorage.getItem("wishlistProducts") || "[]");
  } catch (e) {
    return [];
  }
}

function setWishlistProducts(items) {
  localStorage.setItem("wishlistProducts", JSON.stringify(items));
  document.dispatchEvent(new CustomEvent("wishlist-updated"));
}

function updateWishlistCountOnly() {
  const count = document.getElementById("wishlistCount");
  if (!count) return;

  const items = getWishlistProducts();
  count.textContent = items.length;
}

/* ===============================
   WISHLIST HEADER PREVIEW
================================ */

function initWishlistHeaderPreview() {
  const btn = document.getElementById("wishlistHeaderBtn");
  const popup = document.getElementById("wishlistPreview");
  const body = document.getElementById("wishlistPreviewBody");
  const count = document.getElementById("wishlistCount");
  const closeBtn = document.getElementById("closeWishlistPreview");

  if (!popup || !body || !count) return;

  function renderWishlistPreview() {
    const items = getWishlistProducts();
    count.textContent = items.length;

    if (!items.length) {
      body.innerHTML = "<p>Chưa có sản phẩm yêu thích.</p>";
      return;
    }

    body.innerHTML = items.slice(0, 6).map((item) => {
      const productId = item.id ?? "";
      const image = item.image || "";
      const name = item.name || "Sản phẩm";
      const price = Number(item.price || 0).toLocaleString("vi-VN");

      return `
        <a class="wishlist-preview-item" href="/products/${productId}">
          <img src="/images/${image}" alt="${name}">
          <div>
            <strong>${name}</strong>
            <span>${price}đ</span>
          </div>
        </a>
      `;
    }).join("");
  }

  if (btn) {
    btn.addEventListener("click", function (e) {
      e.stopPropagation();
      renderWishlistPreview();
      popup.classList.toggle("show");
    });
  }

  if (closeBtn) {
    closeBtn.addEventListener("click", function () {
      popup.classList.remove("show");
    });
  }

  popup.addEventListener("click", function (e) {
    e.stopPropagation();
  });

  document.addEventListener("click", function (e) {
    if (btn && !popup.contains(e.target) && !btn.contains(e.target)) {
      popup.classList.remove("show");
    }
  });

  document.addEventListener("wishlist-updated", renderWishlistPreview);
  window.addEventListener("storage", renderWishlistPreview);

  renderWishlistPreview();
}

/* ===============================
   HEADER MORE MENU
================================ */

function initHeaderMoreMenu() {
  const menu = document.getElementById("headerMoreMenu");
  const btn = document.getElementById("moreMenuBtn");
  const dropdown = document.getElementById("moreMenuDropdown");

  if (!menu || !btn || !dropdown) return;

  btn.addEventListener("click", function (e) {
    e.stopPropagation();
    menu.classList.toggle("open");
  });

  dropdown.addEventListener("click", function (e) {
    e.stopPropagation();
  });

  document.addEventListener("click", function (e) {
    if (!menu.contains(e.target)) {
      menu.classList.remove("open");
    }
  });

  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
      menu.classList.remove("open");
    }
  });
}

/* ===============================
   LOCATION BUTTON SYNC
================================ */

function initLocationSync() {
  const buttons = [
    document.getElementById("openLocationModal"),
    document.getElementById("openLocationModalLogged")
  ].filter(Boolean);

  const labels = [
    document.getElementById("selectedLocation"),
    document.getElementById("selectedLocationLogged")
  ].filter(Boolean);

  const saved = localStorage.getItem("selectedLocationValue") || "Hồ Chí Minh";

  labels.forEach((label) => {
    label.textContent = saved;
  });

  buttons.forEach((btn) => {
    btn.addEventListener("click", function () {
      const currentValue = localStorage.getItem("selectedLocationValue") || "Hồ Chí Minh";
      labels.forEach((label) => {
        label.textContent = currentValue;
      });
    });
  });
}

/* ===============================
   LOCATION MODAL
================================ */

function initLocationModal() {
  const openBtn = document.getElementById("openLocationModal");
  const openBtnLogged = document.getElementById("openLocationModalLogged");
  const overlay = document.getElementById("locationModalOverlay");
  const closeBtn = document.getElementById("closeLocationModal");
  const listItems = document.querySelectorAll("[data-location-value]");
  const labels = [
    document.getElementById("selectedLocation"),
    document.getElementById("selectedLocationLogged")
  ].filter(Boolean);

  if (!overlay) return;

  function openModal() {
    overlay.classList.add("show");
    document.body.classList.add("location-modal-open");
  }

  function closeModal() {
    overlay.classList.remove("show");
    document.body.classList.remove("location-modal-open");
  }

  [openBtn, openBtnLogged].filter(Boolean).forEach((btn) => {
    btn.addEventListener("click", openModal);
  });

  if (closeBtn) {
    closeBtn.addEventListener("click", closeModal);
  }

  overlay.addEventListener("click", function (e) {
    if (e.target === overlay) {
      closeModal();
    }
  });

  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
      closeModal();
    }
  });

  listItems.forEach((item) => {
    item.addEventListener("click", function () {
      const value = item.getAttribute("data-location-value") || "Hồ Chí Minh";
      localStorage.setItem("selectedLocationValue", value);

      labels.forEach((label) => {
        label.textContent = value;
      });

      closeModal();
    });
  });
}

/* ===============================
   PROMO POPUP
================================ */

function initPromoPopup() {
  const popup = document.getElementById("promoPopup");
  const closeBtn = document.getElementById("promoPopupClose");
  const overlay = popup?.querySelector(".promo-popup-overlay");

  if (!popup) return;

  const hiddenKey = "promoPopupClosed";

  function openPopup() {
    popup.classList.add("show");
    document.body.classList.add("popup-open");
  }

  function closePopup() {
    popup.classList.remove("show");
    document.body.classList.remove("popup-open");
    sessionStorage.setItem(hiddenKey, "1");
  }

  if (!sessionStorage.getItem(hiddenKey)) {
    setTimeout(openPopup, 500);
  }

  if (closeBtn) closeBtn.addEventListener("click", closePopup);
  if (overlay) overlay.addEventListener("click", closePopup);

  document.addEventListener("keydown", function (e) {
    if (e.key === "Escape") {
      closePopup();
    }
  });
}

/* ===============================
   ADMIN TABLE SEARCH
================================ */

function initAdminTableSearch() {
  const searchInputs = document.querySelectorAll(
    "#adminTableSearch, #productTableSearch, #categoryTableSearch, [data-admin-search]"
  );

  if (!searchInputs.length) return;

  searchInputs.forEach((input) => {
    const targetSelector = input.getAttribute("data-target-table");
    const table = targetSelector
      ? document.querySelector(targetSelector)
      : input.closest(".admin-shell, .admin-page, main, body")?.querySelector(".admin-table");

    if (!table) return;

    const rows = Array.from(table.querySelectorAll("tbody tr, .table-row")).filter((row) => {
      return !row.classList.contains("table-head");
    });

    input.addEventListener("input", function () {
      const keyword = input.value.trim().toLowerCase();

      rows.forEach((row) => {
        const text = row.innerText.toLowerCase();
        row.style.display = text.includes(keyword) ? "" : "none";
      });
    });
  });
}

/* ===============================
   PRODUCT IMAGE PREVIEW
================================ */

function initProductImagePreview() {
  const input =
    document.getElementById("imageUrl") ||
    document.getElementById("image") ||
    document.getElementById("productImage") ||
    document.querySelector("[data-product-image-input]");

  const preview =
    document.getElementById("productPreviewImage") ||
    document.getElementById("imagePreview") ||
    document.getElementById("productImagePreview") ||
    document.querySelector("[data-product-image-preview]");

  if (!input || !preview) return;

  function renderPreview() {
    const raw = (input.value || "").trim();

    if (!raw) return;

    if (
      raw.startsWith("http://") ||
      raw.startsWith("https://") ||
      raw.startsWith("/") ||
      raw.startsWith("data:")
    ) {
      preview.src = raw;
    } else {
      preview.src = "/images/" + raw;
    }
  }

  input.addEventListener("input", renderPreview);
  input.addEventListener("change", renderPreview);
  renderPreview();
}

/* ===============================
   CATEGORY ICON PREVIEW
================================ */

function initCategoryIconPreview() {
  const input =
    document.getElementById("iconClass") ||
    document.getElementById("icon") ||
    document.querySelector("[data-category-icon-input]");

  const preview =
    document.getElementById("categoryIconPreview") ||
    document.getElementById("iconPreview") ||
    document.querySelector("[data-category-icon-preview]");

  if (!input || !preview) return;

  function renderIcon() {
    const iconClass = (input.value || "").trim();

    if (!iconClass) {
      preview.className = "bi bi-grid";
      return;
    }

    preview.className = "bi " + iconClass;
  }

  input.addEventListener("input", renderIcon);
  input.addEventListener("change", renderIcon);
  renderIcon();
}

/* ===============================
   ADMIN SIDEBAR TOGGLE
================================ */

function initAdminSidebarToggle() {
  const toggleBtn =
    document.getElementById("adminSidebarToggle") ||
    document.querySelector("[data-admin-sidebar-toggle]");

  const sidebar =
    document.getElementById("adminSidebar") ||
    document.querySelector(".admin-sidebar");

  if (!toggleBtn || !sidebar) return;

  toggleBtn.addEventListener("click", function () {
    sidebar.classList.toggle("open");
    document.body.classList.toggle("admin-sidebar-open");
  });
}

/* ===============================
   ADMIN ACTIVE MENU
================================ */

function initAdminActiveMenu() {
  const links = document.querySelectorAll(".admin-sidebar a[href], .admin-nav a[href]");
  if (!links.length) return;

  const currentPath = window.location.pathname;

  links.forEach((link) => {
    const href = link.getAttribute("href");
    if (!href) return;

    const exactMatch = href === currentPath;
    const sectionMatch =
      href !== "/" &&
      href !== "#" &&
      currentPath.startsWith(href) &&
      href.length > 1;

    if (exactMatch || sectionMatch) {
      link.classList.add("active");
    }
  });
}

/* ===============================
   ADMIN CONFIRM DELETE
================================ */

function initAdminConfirmDelete() {
  const deleteButtons = document.querySelectorAll("[data-confirm-delete]");

  deleteButtons.forEach((btn) => {
    btn.addEventListener("click", function (e) {
      const message =
        btn.getAttribute("data-confirm-delete") ||
        "Bạn có chắc muốn xóa mục này không?";

      if (!confirm(message)) {
        e.preventDefault();
      }
    });
  });
}

/* ===============================
   ADMIN CARD CLICK EFFECT
================================ */

function initAdminCardEffects() {
  const cards = document.querySelectorAll(".admin-card, .dashboard-card, .stats-card");
  if (!cards.length) return;

  cards.forEach((card) => {
    card.addEventListener("mouseenter", function () {
      card.classList.add("is-hover");
    });

    card.addEventListener("mouseleave", function () {
      card.classList.remove("is-hover");
    });
  });
}

/* ===============================
   GLOBAL HELPERS
================================ */

function initGlobalButtons() {
  const closeWishlistPreview = document.getElementById("closeWishlistPreview");
  if (closeWishlistPreview) {
    closeWishlistPreview.setAttribute("aria-label", "Đóng yêu thích");
  }

  const chatClose = document.getElementById("chatClose");
  if (chatClose) {
    chatClose.setAttribute("aria-label", "Đóng chat");
  }
}

/* ===============================
   INIT ALL
================================ */

document.addEventListener("DOMContentLoaded", function () {
  const toggle = document.getElementById("chatToggle");
  const close = document.getElementById("chatClose");
  const sendBtn = document.getElementById("chatSend");
  const input = document.getElementById("chatInput");

  if (toggle) toggle.onclick = function () { toggleChat(); };
  if (close) close.onclick = function () { toggleChat(false); };
  if (sendBtn) sendBtn.onclick = sendChat;

  if (input) {
    input.addEventListener("keydown", function (e) {
      if (e.key === "Enter") {
        e.preventDefault();
        sendChat();
      }
    });
  }

  initFlashSaleCountdown();
  initHomeSlider();
  initWishlistHeaderPreview();
  initHeaderMoreMenu();
  initLocationSync();
  initLocationModal();
  initPromoPopup();

  initAdminTableSearch();
  initProductImagePreview();
  initCategoryIconPreview();
  initAdminSidebarToggle();
  initAdminActiveMenu();
  initAdminConfirmDelete();
  initAdminCardEffects();

  initGlobalButtons();
  updateWishlistCountOnly();
});