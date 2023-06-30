import argparse
import os
import platform
import sys
import os
import time

from pathlib import Path

import torch

from models.common import DetectMultiBackend
from utils.dataloaders import IMG_FORMATS, LoadImages, LoadScreenshots, LoadStreams
from utils.general import (LOGGER, Profile, check_file, check_img_size, check_imshow, check_requirements, colorstr, cv2,
                           increment_path, non_max_suppression, print_args, scale_boxes, strip_optimizer, xyxy2xywh)
from utils.plots import Annotator, colors, save_one_box
from utils.torch_utils import select_device, smart_inference_mode

@smart_inference_mode()
def run(
        weights,  # GUI model path
        input_img_dir,  # TESTAR screenshot that needs to be processed
        output_txt_dir,  # Output directory to store Yolo results
        dnn=True,  # use OpenCV DNN for ONNX inference
        data='data/coco128.yaml',  # dataset.yaml path
        imgsz=(640, 640),  # inference size (height, width)
        conf_thres=0.25,  # confidence threshold
        iou_thres=0.45,  # NMS IOU threshold
        max_det=1000,  # maximum detections per image
        device='',  # cuda device, i.e. 0 or 0,1,2,3 or cpu
        save_conf=False,  # save confidences in --save-txt labels
        save_crop=False,  # save cropped prediction boxes
        classes=None,  # filter by class: --class 0, or --class 0 2 3
        agnostic_nms=False,  # class-agnostic NMS
        augment=False,  # augmented inference
        visualize=False,  # visualize features
        line_thickness=3,  # bounding box thickness (pixels)
        half=False,  # use FP16 half-precision inference
        vid_stride=1,  # video frame-rate stride
):
    # Load model
    device = select_device(device)
    model = DetectMultiBackend(weights, device=device, dnn=dnn, data=data, fp16=half)
    stride, names, pt = model.stride, model.names, model.pt
    imgsz = check_img_size(imgsz, s=stride)  # check image size

    # Run inference
    model.warmup(imgsz=(1 if pt or model.triton else bs, 3, *imgsz))  # warmup
    seen, windows, dt = 0, [], (Profile(), Profile(), Profile())

    # We dont want to run this script service forever
    # Wait a maximum of X tries
    counter = 0  # Counter variable
    maxTries = 60

    while counter < maxTries:

        counter += 1  # Increment the counter

        # If the directory is empty wait for 1 second
        if not os.listdir(input_img_dir):
            time.sleep(1)

        # Else, process the image with the yolo model
        else:
            input_img_dir = str(input_img_dir)

            output_file = os.path.join(str(output_txt_dir), 'widgets.txt')
            ## If the GUI widgets txt file exists, delete it before writing a new one
            if os.path.exists(output_file):
                os.remove(output_file)

            # Dataloader
            bs = 1  # batch_size
            dataset = LoadImages(input_img_dir, img_size=imgsz, stride=stride, auto=pt, vid_stride=vid_stride)

            for path, im, im0s, vid_cap, s in dataset:
                with dt[0]:
                    im = torch.from_numpy(im).to(model.device)
                    im = im.half() if model.fp16 else im.float()  # uint8 to fp16/32
                    im /= 255  # 0 - 255 to 0.0 - 1.0
                    if len(im.shape) == 3:
                        im = im[None]  # expand for batch dim

                # Inference
                with dt[1]:
                    visualize = increment_path(save_dir / Path(path).stem, mkdir=True) if visualize else False
                    pred = model(im, augment=augment, visualize=visualize)

                # NMS
                with dt[2]:
                    pred = non_max_suppression(pred, conf_thres, iou_thres, classes, agnostic_nms, max_det=max_det)

                # Process predictions
                for i, det in enumerate(pred):  # per image
                    seen += 1
                    p, im0, frame = path, im0s.copy(), getattr(dataset, 'frame', 0)

                    gn = torch.tensor(im0.shape)[[1, 0, 1, 0]]  # normalization gain whwh
                    imc = im0.copy() if save_crop else im0  # for save_crop
                    annotator = Annotator(im0, line_width=line_thickness, example=str(names))
                    if len(det):
                        # Rescale boxes from img_size to im0 size
                        det[:, :4] = scale_boxes(im.shape[2:], det[:, :4], im0.shape).round()

                        # Write GUI widgets coordinates
                        for *xyxy, conf, cls in reversed(det):
                            xywh = (xyxy2xywh(torch.tensor(xyxy).view(1, 4)) / gn).view(-1).tolist()  # normalized xywh
                            line = (cls, *xywh, conf) if save_conf else (cls, *xywh)  # label format
                            with open(f'{str(output_file)}', 'a') as f:
                                f.write(('%g ' * len(line)).rstrip() % line + '\n')

                        # Get the TESTAR GUI images and delete them (clean for next iteration)
                        files = os.listdir(input_img_dir)
                        for file_name in files:
                            file_path = os.path.join(input_img_dir, file_name)  # Get the full file path
                            print(f"RESULTS! for image '{file_path}'")
                            os.remove(file_path)  # Delete the image file

    print("END... BYE")

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--weights', type=str, default='yolo_weights.pt', help='GUI model path')
    parser.add_argument('--input_img_dir', type=str, default='data/images/input/', help='input images directory')
    parser.add_argument('--output_txt_dir', type=str, default='data/images/output/', help='output results directory')

    args = parser.parse_args()
    run(**vars(args))
