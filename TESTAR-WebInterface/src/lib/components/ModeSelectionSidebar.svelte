<script>
  import { page } from '$app/stores';

  const menuItems = [
    { name: "Generate mode", href: "/modes/generatemode", icon: "⚙️" },
    { name: "Spy mode", href: "/modes/spymode", icon: "🕵️" },
    { name: "Replay mode", href: "/modes/replaymode", icon: "⏪" },
    { name: "View mode", href: "/modes/viewmode", icon: "👁️" },
  ];
</script>

<aside class="overlay" role="navigation" aria-label="Mode selection">
  <div class="title">Modes</div>
  {#each menuItems as item}
    <a
      href={item.href}
      class="menu-item"
      class:is-active={$page.url.pathname.startsWith(item.href)}
      aria-current={$page.url.pathname.startsWith(item.href) ? 'page' : undefined}
    >
      <span class="icon">{item.icon}</span>
      <span class="label">{item.name}</span>
    </a>
  {/each}
</aside>

<style>
  .overlay {
    position: absolute;
    top: 0;
    left: 0;
    z-index: 10;
    align-self: flex-start;
    height: auto;
    width: 80px;              /* collapsed width */
    background: linear-gradient(160deg, #0f172a 0%, #111827 100%);
    transition: width 0.22s ease, box-shadow 0.22s ease, border-color 0.22s ease;
    overflow: hidden;
    padding: 18px 12px 16px;
    border-top-right-radius: 20px;
    border-bottom-right-radius: 20px;
    margin-top: 16px;
    border: 1px solid rgba(148, 163, 184, 0.2);
    box-shadow: 0 18px 36px rgba(15, 23, 42, 0.28);
    backdrop-filter: blur(10px);
  }

  .overlay:hover {
    width: 240px;             /* expanded width */
    box-shadow: 0 24px 44px rgba(15, 23, 42, 0.32);
    border-color: rgba(148, 163, 184, 0.35);
  }

  .title {
    color: #cbd5f5;
    font-size: 12px;
    letter-spacing: 0.18em;
    text-transform: uppercase;
    font-weight: 600;
    padding: 0 10px 12px;
    opacity: 0;
    transform: translateX(-6px);
    transition: opacity 0.2s ease, transform 0.2s ease;
  }

  .overlay:hover .title {
    opacity: 1;
    transform: translateX(0);
  }

  .menu-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px 12px;
    color: #e2e8f0;
    text-decoration: none;
    white-space: nowrap;
    border-radius: 10px;
    font-weight: 500;
    position: relative;
    transition: background 0.2s ease, color 0.2s ease, transform 0.2s ease;
  }

  .menu-item:hover {
    background: rgba(148, 163, 184, 0.14);
    color: #f8fafc;
    transform: translateX(2px);
  }

  .menu-item.is-active {
    background: rgba(59, 130, 246, 0.22);
    color: #e0f2fe;
  }

  .menu-item.is-active::before {
    content: "";
    position: absolute;
    left: -12px;
    height: 28px;
    width: 4px;
    border-radius: 999px;
    background: linear-gradient(180deg, #60a5fa 0%, #38bdf8 100%);
  }

  .icon {
    font-size: 18px;
    min-width: 38px;
    height: 38px;
    display: grid;
    place-items: center;
    border-radius: 10px;
    background: rgba(148, 163, 184, 0.14);
    box-shadow: inset 0 0 0 1px rgba(148, 163, 184, 0.16);
  }

  .label {
    opacity: 0;
    transform: translateX(-6px);
    transition: opacity 0.2s ease, transform 0.2s ease;
  }

  .overlay:hover .label {
    opacity: 1;
    transform: translateX(0);
  }
</style>
