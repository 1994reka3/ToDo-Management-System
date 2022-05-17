// ブラウザバック対策
window.onload = function() {
  history.pushState(null, null, null);

  window.addEventListener("popstate", function (e) {
    history.pushState(null, null, null);
    return;
  });
};