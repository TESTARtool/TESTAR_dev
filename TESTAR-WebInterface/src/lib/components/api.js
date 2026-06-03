/**
 * @param {String} [runMode]
 * @param {Record<string, unknown>} [settings]
 */
export async function sendSettings(runMode, settings) {
  const response = await fetch("/api/conf", {
    method: "POST",
    body: JSON.stringify({
      ...settings,
      Mode: runMode,
    }),
    headers: {
      "Content-type": "application/json",
    },
  });

  return response.status;
}

/**
 * @param {String} [mode]
 * @param {Record<string, unknown>} [settings]
 */

export async function runMode(mode, settings) {
  const status = await sendSettings(mode, settings);

  if (status === 200) {
    const response = await fetch("/api/start/", {
      method: "POST",
      body: JSON.stringify({}),
    });

    return response.status;
  } else {
    return status;
  }
}
