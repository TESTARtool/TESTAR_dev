var s1 = document.createElement('script');
s1.src = chrome.extension.getURL('testar.state.js');
(document.head || document.documentElement).appendChild(s1);

var s2 = document.createElement('script');
s2.src = chrome.extension.getURL('testar.canvas.js');
(document.head || document.documentElement).appendChild(s2);
