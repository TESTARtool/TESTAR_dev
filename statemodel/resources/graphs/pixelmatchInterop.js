window.pixelmatchInterop = {
    compareImages: (newImage, oldImage, thresholdValue) => {
        return new Promise((resolve, reject) => {
            // Create image elements for the new and old images
            const newImg = new Image();
            newImg.src = 'data:image/png;base64,' + newImage;

            const oldImg = new Image();
            oldImg.src = 'data:image/png;base64,' + oldImage;

            // Ensure both images are loaded before comparing
            Promise.all([new Promise(resolve => newImg.onload = resolve), new Promise(resolve => oldImg.onload = resolve)])
                .then(() => {
                    console.log("Images loaded successfully.");
                    // Create canvas elements for drawing the images
                    const canvas = document.createElement('canvas');
                    const context = canvas.getContext('2d');
                    canvas.width = newImg.width;
                    canvas.height = newImg.height;

                    // Draw the images on the canvas to get the pixel data of the images
                    context.drawImage(newImg, 0, 0);
                    const newImageData = context.getImageData(0, 0, canvas.width, canvas.height);
                    context.drawImage(oldImg, 0, 0);
                    const oldImageData = context.getImageData(0, 0, canvas.width, canvas.height);

                    // Create canvas for the diff image
                    const diffCanvas = document.createElement('canvas');
                    const diffContext = diffCanvas.getContext('2d');
                    diffCanvas.width = newImg.width;
                    diffCanvas.height = newImg.height;

                    // Use pixelmatch to compare images and generate diff
                    const diff = new Uint8Array(newImageData.data.length);
                    const numDiffPixels = pixelmatch(oldImageData.data, newImageData.data, diff, diffCanvas.width, diffCanvas.height, {
                        threshold: thresholdValue,
                        includeAA: true,
                        alpha: 0.6,
                        aaColor: [0, 0, 0],
                        diffColor: [255, 0, 0],
                        diffColorAlt: [0, 0, 255],
                        diffMask: null
                    });

                    // Create ImageData object from the diff data
                    const diffImageData = new ImageData(new Uint8ClampedArray(diff), newImg.width, newImg.height);

                    // Apply the diff data to the diff canvas
                    diffContext.clearRect(0, 0, canvas.width, canvas.height);
                    diffContext.putImageData(diffImageData, 0, 0);

                    // Convert the diff canvas to a base64 string
                    const diffBase64 = diffCanvas.toDataURL('image/png').split(',')[1];

                    resolve(diffBase64);
                })
                .catch(error => {
                    console.error("Error during image comparison:", error);
                    reject(error);
                });
        });
    }
};
