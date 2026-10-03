(() => {
  const ICONS = {
    search: '<circle cx="11" cy="11" r="6.5"/><path d="M16 16l4.5 4.5"/>',
    import: '<path d="M12 4v10M8 10l4 4 4-4"/><path d="M4 15v3a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-3"/>',
    export: '<path d="M12 15V4M8 8l4-4 4 4"/><path d="M4 15v3a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2v-3"/>',
    plus: '<path d="M12 5v14M5 12h14"/>',
    pin: '<path d="M9 4h6l-1 6 3 3v1H7v-1l3-3z"/><path d="M12 14v6"/>',
    "pin-off": '<path d="M9 4h6l-1 6 3 3v1H7v-1l3-3z"/><path d="M12 14v6"/><path d="M4 4l16 16"/>',
    back: '<path d="M15 5l-7 7 7 7"/>',
    "back-a": '<path d="M20 12H5M11 6l-6 6 6 6"/>',
    more: '<circle cx="12" cy="5.5" r="1.1"/><circle cx="12" cy="12" r="1.1"/><circle cx="12" cy="18.5" r="1.1"/>',
    "more-h": '<circle cx="5.5" cy="12" r="1.1"/><circle cx="12" cy="12" r="1.1"/><circle cx="18.5" cy="12" r="1.1"/>',
    close: '<path d="M6 6l12 12M18 6L6 18"/>',
    clear: '<circle cx="12" cy="12" r="8.5"/><path d="M9.2 9.2l5.6 5.6M14.8 9.2l-5.6 5.6"/>',
    trash: '<path d="M4.5 7h15M10 4h4M6.5 7l1 12a1.5 1.5 0 0 0 1.5 1.4h6a1.5 1.5 0 0 0 1.5-1.4l1-12"/><path d="M10 11v6M14 11v6"/>',
    list: '<path d="M9 6h11M9 12h11M9 18h11"/><circle cx="4.5" cy="6" r=".9"/><circle cx="4.5" cy="12" r=".9"/><circle cx="4.5" cy="18" r=".9"/>',
    checklist: '<rect x="3.5" y="3.5" width="17" height="17" rx="5"/><path d="M8 12l3 3 5-6"/>',
    quote: '<path d="M5 5v14M10 8h9M10 12h9M10 16h6"/>',
    code: '<path d="M8 7l-5 5 5 5M16 7l5 5-5 5"/>',
    link: '<path d="M10 14a4 4 0 0 0 5.7 0l3-3a4 4 0 0 0-5.7-5.7l-1 1"/><path d="M14 10a4 4 0 0 0-5.7 0l-3 3a4 4 0 0 0 5.7 5.7l1-1"/>',
    check: '<path d="M5 12.5l4.5 4.5L19 7.5"/>',
    alert: '<circle cx="12" cy="12" r="8.5"/><path d="M12 7.5v5.5M12 16.3v.2"/>',
    file: '<path d="M14 3.5H7a2 2 0 0 0-2 2v13a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V8.5z"/><path d="M14 3.5v5h5"/><path d="M8.5 16.5v-4l2 2 2-2v4M15.5 12.5v4M14 15l1.5 1.5L17 15"/>',
  };
  window.MK_ICONS = Object.keys(ICONS);

  const sprite = document.createElementNS("http://www.w3.org/2000/svg", "svg");
  sprite.setAttribute("aria-hidden", "true");
  sprite.style.cssText = "position:absolute;width:0;height:0";
  sprite.innerHTML = Object.entries(ICONS)
    .map(([id, d]) => `<symbol id="i-${id}" viewBox="0 0 24 24">${d}</symbol>`)
    .join("");
  document.body.prepend(sprite);

  const store = {
    get(k) { try { return localStorage.getItem(k); } catch { return null; } },
    set(k, v) { try { localStorage.setItem(k, v); } catch {} },
  };

  const page = document.body.dataset.page;
  const hasScreens = page !== "system";
  const nav = document.createElement("nav");
  nav.className = "site-nav";
  const links = [
    ["system", "index.html", "Design system"],
    ["list", "notes-list.html", "Notes list"],
    ["note", "note.html", "Note"],
    ["search", "search.html", "Search"],
  ];
  nav.innerHTML = `
    <a class="brand" href="index.html"><i></i>Marky</a>
    <div class="links">${links.map(([id, href, label]) => `<a href="${href}" class="${id === page ? "on" : ""}">${label}</a>`).join("")}</div>
    ${hasScreens ? `<div class="switches">
      <div class="switch" data-switch="theme"><button data-v="light">Light</button><button data-v="dark">Dark</button></div>
      <div class="switch" data-switch="platform"><button data-v="ios">iOS</button><button data-v="android">Android</button></div>
    </div>` : ""}`;
  document.body.prepend(nav);

  // Light/dark specimens: write the component once, render it on both themes.
  document.querySelectorAll(".both").forEach((src) => {
    const grid = document.createElement("div");
    grid.className = "both-grid";
    for (const [cls, label] of [["mk panel", "Light"], ["mk panel dark", "Dark"]]) {
      const p = document.createElement("div");
      p.className = cls;
      p.dataset.label = label;
      p.innerHTML = src.innerHTML;
      grid.appendChild(p);
    }
    src.replaceWith(grid);
  });

  document.querySelectorAll(".screen").forEach((s) => {
    s.insertAdjacentHTML("afterbegin", `<div class="status"><span class="i">9:41</span><span class="a">9:41</span><span class="icons"><i></i><i></i></span></div>`);
    s.insertAdjacentHTML("beforeend", '<div class="home-ind"></div>');
  });

  const rows = ["qwertyuiop", "asdfghjkl", "zxcvbnm"];
  document.querySelectorAll(".kbd").forEach((kbd) => {
    rows.forEach((keys, i) => {
      let html = i === 2 ? '<span class="k w">⇧</span>' : "";
      for (const c of keys) html += `<span class="k">${c}</span>`;
      if (i === 2) html += '<span class="k w">⌫</span>';
      kbd.insertAdjacentHTML("beforeend", `<div class="r">${html}</div>`);
    });
    kbd.insertAdjacentHTML("beforeend", '<div class="r"><span class="k w">123</span><span class="k sp"></span><span class="k rt">return</span></div>');
  });

  function applyTheme(v) {
    document.body.classList.toggle("screens-dark", v === "dark");
    store.set("mk-theme", v);
    mark("theme", v);
  }
  function applyPlatform(v) {
    const android = v === "android";
    document.body.classList.toggle("android", android);
    document.querySelectorAll("use").forEach((u) => {
      const h = u.getAttribute("href");
      if (h === "#i-back" || h === "#i-back-a") u.setAttribute("href", android ? "#i-back-a" : "#i-back");
      if (h === "#i-more" || h === "#i-more-h") u.setAttribute("href", android ? "#i-more" : "#i-more-h");
    });
    store.set("mk-platform", v);
    mark("platform", v);
  }
  function mark(name, v) {
    document.querySelectorAll(`[data-switch="${name}"] button`).forEach((b) => b.classList.toggle("on", b.dataset.v === v));
  }
  if (hasScreens) {
    const params = new URLSearchParams(location.search);
    applyTheme(params.get("theme") || store.get("mk-theme") || "light");
    applyPlatform(params.get("platform") || store.get("mk-platform") || "ios");
    nav.querySelector('[data-switch="theme"]').addEventListener("click", (e) => e.target.dataset.v && applyTheme(e.target.dataset.v));
    nav.querySelector('[data-switch="platform"]').addEventListener("click", (e) => e.target.dataset.v && applyPlatform(e.target.dataset.v));
  }

  document.addEventListener("click", (e) => {
    const cb = e.target.closest(".md .cb");
    if (cb) {
      cb.classList.toggle("x");
      cb.closest("li")?.classList.toggle("done", cb.classList.contains("x"));
      return;
    }
    const link = e.target.closest("[data-href]");
    if (link) location.href = link.dataset.href;
  });
})();
