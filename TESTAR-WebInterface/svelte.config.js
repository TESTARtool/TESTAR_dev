import adapter from '@sveltejs/adapter-static';

/** @type {import('@sveltejs/kit').Config} */
const config = {
	kit: {
		adapter: adapter({
			pages: '../testar/target/resources/main/dist',
			assets: '../testar/target/resources/main/dist',
			fallback: 'index.html',
			precompress: true,
			strict: true
		})

	}
};

export default config;
