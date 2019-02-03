import glob
import cv2
import numpy as np

def main():
    load_images('./unedited/*png')                                      #choose path of images folder that best suits

def load_images(path):
    print('--- Images Loading ---')
    i=0
    for file in glob.glob(path):                                        #loads each image that needs to be edited
        print('Editing File: ', file)
        img=cv2.imread(file)
        black_to_white_2(img,i)
        i=i+1

def black_to_white_2(img, i):
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

    ret, thresh = cv2.threshold(gray, 0, 255, cv2.THRESH_BINARY)        #threshold operation to turn image to black and white

    img[thresh == 0] = 255                                              #where image is black (background) turn it to white

    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (5, 5))
    erosion = cv2.erode(img, kernel, iterations=1)

    output = cv2.resize(erosion, (80, 80))                              #image resize for future Cascade training
    cv2.imwrite('./edited/negatives_{:>0}.png'.format(i), output)       #saves images as positive or negative


if __name__ == "__main__":
    main()
