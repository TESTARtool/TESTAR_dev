/*
 * Load and append the 2 scripts at 'document end'
 * Configured in manifest.json
 */

var s1 = document.createElement('script');
var s2 = document.createElement('script');
if (typeof chrome.runtime != 'undefined') {
	s1.src = chrome.runtime.getURL('js/testar.state.js');
	s2.src = chrome.runtime.getURL('js/testar.canvas.js');
} 
else {
	s1.src = browser.runtime.getURL('js/testar.state.js');
	s2.src = browser.runtime.getURL('js/testar.canvas.js');
}
(document.head || document.documentElement).appendChild(s1);
(document.head || document.documentElement).appendChild(s2);
