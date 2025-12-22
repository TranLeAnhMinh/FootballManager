document.addEventListener("DOMContentLoaded", () => {
  const langBtn = document.getElementById("langBtn");
  const langDropdown = document.querySelector(".lang-dropdown");

  if (langBtn && langDropdown) {
    langBtn.addEventListener("click", (e) => {
      e.stopPropagation();
      langDropdown.classList.toggle("active");
    });

    document.addEventListener("click", () => {
      langDropdown.classList.remove("active");
    });
  }
});
