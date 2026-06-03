<aside class="sidebar" aria-hidden="true"></aside>

<style>
  .sidebar {
    width: 150px;
    background: linear-gradient(180deg, #f8fafc 0%, #edf2f7 55%, #e8eef6 100%);
    border-right: 1px solid rgba(15, 23, 42, 0.08);
    box-shadow: inset -1px 0 0 rgba(148, 163, 184, 0.14);
    height: 100vh;
    z-index: 1;
    position: relative;
  }

  .sidebar::before {
    content: "";
    position: absolute;
    top: 20px;
    left: 16px;
    right: 16px;
    height: 120px;
    border-radius: 18px;
    background: radial-gradient(120px 60px at 30% 30%, rgba(59, 130, 246, 0.18), transparent 70%),
      radial-gradient(120px 60px at 70% 80%, rgba(14, 165, 233, 0.12), transparent 70%);
    opacity: 0.8;
    pointer-events: none;
  }
</style>
