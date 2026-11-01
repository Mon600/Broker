export function startRealtime(onProductUpdate) {
  let socket;
  let source;
  let retry;
  let stopped = false;

  const status = (channel, text) => {
    document.querySelector(`#${channel}-status`).textContent = text;
  };
  const receive = (channel, label, raw) => {
    console.log(`[${label}]`, raw);
    if (raw === 'Подключено') return;
    try {
      const update = JSON.parse(raw);
      if (typeof update.productId !== 'string' || !Number.isFinite(update.price) || !Number.isInteger(update.stock)) {
        throw new Error('Некорректное обновление товара');
      }
      document.querySelector(`#${channel}-message`).textContent =
        `${new Date().toLocaleTimeString('ru-RU')}\n${JSON.stringify(update, null, 2)}`;
      onProductUpdate(update);
    } catch (error) {
      console.error(`[${label}] Не удалось обработать сообщение`, error);
    }
  };
  const connectSocket = () => {
    if (stopped) return;
    status('ws', 'Подключение…');
    const url = new URL('/ws', location.href);
    url.protocol = location.protocol === 'https:' ? 'wss:' : 'ws:';
    socket = new WebSocket(url);
    socket.onopen = () => status('ws', 'Подключён');
    socket.onmessage = event => receive('ws', 'WebSocket', event.data);
    socket.onerror = () => { status('ws', 'Ошибка соединения'); socket.close(); };
    socket.onclose = () => {
      status('ws', 'Нет соединения');
      if (!stopped) retry = setTimeout(connectSocket, 3000);
    };
  };
  const start = () => {
    stopped = false;
    connectSocket();
    source = new EventSource(`/api/v1/sse/subscribe?clientId=${crypto.randomUUID()}`);
    source.onopen = () => status('sse', 'Подключён');
    source.onmessage = event => receive('sse', 'SSE', event.data);
    source.onerror = () => status('sse', 'Переподключение…');
  };
  window.addEventListener('pagehide', () => {
    stopped = true;
    clearTimeout(retry);
    socket?.close();
    source?.close();
  });
  window.addEventListener('pageshow', event => { if (event.persisted) start(); });
  start();
}
