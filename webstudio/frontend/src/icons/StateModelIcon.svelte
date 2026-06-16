<script>
    export let size = 24;
    export let title = "state model";
</script>

<svg
    width={size}
    height={size}
    viewBox="0 0 128 128"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    role="img"
    aria-label={title}
>
    <title>{title}</title>
    <defs>
        <marker
            id="state-model-arrow"
            markerWidth="8"
            markerHeight="8"
            refX="6.5"
            refY="4"
            orient="auto"
            markerUnits="strokeWidth"
        >
            <path d="M0,0 L8,4 L0,8 Z" fill="currentColor" />
        </marker>
    </defs>

    <!-- main GUI states -->
    <circle cx="39" cy="67" r="32" fill="#229ca3" stroke="currentColor" stroke-width="2.5" />
    <circle cx="91" cy="93" r="18" fill="#229ca3" stroke="currentColor" stroke-width="2.5" />

    <!-- state transitions -->
    <path
      d="M69 58 C82 55 91 65 91 77"
      fill="none"
      stroke="#1aa3ad"
      stroke-width="4"
      stroke-linecap="round"
      marker-end="url(#state-model-arrow)"
    />
    <path
      d="M68 101 C56 106 46 100 45 91"
      fill="none"
      stroke="#1aa3ad"
      stroke-width="4"
      stroke-linecap="round"
      marker-end="url(#state-model-arrow)"
    />

    <!-- small UI-model hint -->
    <path
      d="M32 57 h14 M32 67 h21 M32 77 h12"
      stroke="white"
      stroke-width="3"
      stroke-linecap="round"
      opacity=".65"
    />
</svg>