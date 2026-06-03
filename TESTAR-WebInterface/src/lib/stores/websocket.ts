import { writable } from "svelte/store";

// ported from backend
export type WSMessage =
  | {
      type: "status";
      status: "running" | "idle" | "finished" | "failed";
      message: string;
      threadId?: number;
    }
  | { type: "log"; log: string }
  | { type: "error"; message: string };

// stores
export const wsStatus = writable<number>(WebSocket.CLOSED);
export const wsLastMessage = writable<WSMessage | null>(null);

// if we ever want to keep track of a list of messages, we can do something like so:
// export const wsMessages = writable<WSMessage[]>([]);

const WS_URL = `ws://${window.location.host}/ws`;
let ws: WebSocket | undefined;
const maxReconnectionAttempts = 5;
let reconnectionAttempts = 0;

export function initWebSocket(url: string = WS_URL) {
  ws = new WebSocket(url);
  wsStatus.set(WebSocket.CONNECTING);

  ws.onopen = () => {
    wsStatus.set(WebSocket.OPEN);
    reconnectionAttempts = 0;
  };
  ws.onmessage = (event) => {
    try {
      wsLastMessage.set(JSON.parse(event.data));
      console.log("Received message:", JSON.parse(event.data));
    } catch {
      console.warn("Received non-JSON data:", event.data);
    }
  };
  ws.onclose = (event) => {
    if (!event.wasClean) reconnect(url);
  };
}

function reconnect(url: string): void {
  if (reconnectionAttempts < maxReconnectionAttempts) {
    reconnectionAttempts++;
    wsStatus.set(WebSocket.CONNECTING);
    console.log(
      `Reconnection attempt ${reconnectionAttempts}/${maxReconnectionAttempts}`,
    );
    setTimeout(() => initWebSocket(url), 3000);
  } else {
    console.error("Max reconnection attempts reached");
    wsStatus.set(WebSocket.CLOSED);
  }
}

export function sendMessage(message: any) {
  if (ws?.readyState === WebSocket.OPEN) {
    ws.send(JSON.stringify(message));
  } else {
    console.error("WebSocket is not connected");
  }
}
