#circle detection test - python circle_detection.py --image 012.jpg

# import the necessary packages
import glob
import os

import numpy as np
import cv2
from matplotlib import pyplot as plt

def image_rect(out,x,y,z):
    cv2.rectangle(out, (x, y), (x+75, y+75), (0, 0, 0), z, 4)

def cell_rect(out,x,y,h,w):
    cv2.rectangle(out, (x, y), (x+h, y+w), (0, 25, 155), 3, 4)

def draw_all_cell(out, array):
    for i in array:
        cell_rect(out, i[0], i[1],i[2], i[3])

def draw_all_rect(out):
    image_rect(out, 393, 140, 3)
    image_rect(out, 1005, 796, 3)
    image_rect(out, 580, 679, 3)
    image_rect(out, 1085, 600, 3)
    image_rect(out, 1027, 357, 3)
    image_rect(out, 361, 309, 3)
    image_rect(out, 769, 301, 3)
    image_rect(out, 1103, 225, 3)
    image_rect(out, 640, 225, 3)

def image_text(output,string, string1, string2):
    cv2.putText(output, string, (5, 960), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 0, 0), 2, cv2.LINE_AA)
    cv2.putText(output, string1, (5, 990), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 0, 0), 2, cv2.LINE_AA)
    cv2.putText(output, string2, (5, 1020), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 0, 0), 2, cv2.LINE_AA)

def window(window_name):
    # windows
    # cv2.namedWindow("img")
    cv2.namedWindow(window_name)

def image_read(file):
    # image processing
    img = cv2.imread(file)
    # img = cv2.resize(img,(780,618))
    test = img.copy()
    output2 = img.copy()
    return img

def image_copy(image):
    output = image.copy()
    return output

def gray_method(image):
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    return gray

def thresh_method(gray):
    ret, thresh = cv2.threshold(gray, 161, 255, 1)
    #ret, thresh = cv2.threshold(gray, 161, 255, 1)
    #cv2.imshow('thresh',thresh)
    #cv2.waitKey(0)
    return thresh

def thresh_casc(gray):
    ret, thresh = cv2.threshold(gray, 161, 255, 2)
    #ret, thresh = cv2.threshold(gray, 161, 255, 1)
    #cv2.imshow('thresh',thresh)
    #cv2.waitKey(0)
    return thresh

def thresh_otsu(gray):
    ret, thresh = cv2.threshold(gray, cv2.THRESH_OTSU, 255, 0)
    #ret, thresh = cv2.threshold(gray, 161, 255, 1)
    cv2.imshow('thresh',thresh)
    cv2.waitKey(0)
    return thresh

def contours(thresh, output):
    im2, contours, hierarchy = cv2.findContours(thresh, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    copy = image_copy(output)
    #contours = sorted(contours, key=cv2.contourArea, reverse=True)
    #perimeters = [cv2.arcLength(contours[i], True) for i in range(len(contours))]
    #listindex = [i for i in range(len(contours)) if perimeters[i] > perimeters[0] * 0.01]
    #numcards = len(contours)
    #print(numcards)
    #[cv2.drawContours(output, [contours[i]], 0, (0, 255, 0), 2) for i in listindex]
    cv2.drawContours(copy, contours, -1, (0,255,255), 2)
    cv2.imshow('Contours',copy)
    cv2.waitKey(0)
    #print(contours)
    #print(hierarchy)
    areaArray=[]
    cellArray=[]
    cellCount = 0

    for contour in contours:
        #print(contour)
        area = cv2.contourArea(contour)
        areaArray.append(area)
        print('Area: ',area)
        if(area>500 and area<12000):
            cellCount+=1
            #cv2.drawContours(output, contour, -1, (50, 0, 100), 2)
            #cv2.imshow('Contours', output)
            #cv2.waitKey(200)
            x, y, w, h = cv2.boundingRect(contour)
            cv2.rectangle(copy, (x, y), (x + h, y + w), (255, 255, 255), 2)
            crop_img = output[y:y+h, x:x+w]
            cell_recognition(crop_img, output, x, y, w, h, cellArray)

        '''
        x, y, w, h = cv2.boundingRect(contour)
        if w>200:
            continue
        if(w>55 and h>55):
            print(x, y, w, h)
            crop_img = output[x:x+100, y:y+100]
            cell_recognition(crop_img, output)
        '''
    areaArray.sort()
    numberInfected=len(cellArray)
    print('AreaArray:', areaArray)
    print('Number of Cells:', cellCount)
    print('CellArray: ', cellArray)
    #print(contours)
    ratio = numberInfected*100/cellCount
    actualRatio = 9 * 100 / cellCount
    string = str("Cells found: %d cells" % cellCount)
    string1 = str("Infected cells: %d cells / Actual Infected: 9 cells" % len(cellArray))
    string2 = str("Infected ratio: %.3f %% / Actual ratio: %.3f %%" %(ratio, actualRatio))
    draw_all_rect(output)
    draw_all_cell(output,cellArray)
    image_text(output,string,string1,string2)
    output=cv2.resize(output,(130*7,103*7))
    cv2.imshow('result',output)
    cv2.waitKey(0)
    #string = str("Number of cells: %d cells" % numcards)
    #string1 = str("Number of infected cells: 9 cells")
    #string2 = str("Infected ratio: %.3f %%" % ratio)

    #image_text(output, string, string1, string2)
    #print_log(string,string1,string2)

def print_log(string, string1, string2):
    print(string)
    print(string1)
    print(string2)

def show_output(output):
    cv2.imshow("output", output)
    # cv2.imshow("gray", gray)
    # cv2.imshow("final", thresh)
    cv2.waitKey(0)
    quit()

def data_info(img):
    string = str(img, '1' '0' '0' '100' '100')
    np.savetxt('./data_info/data.txt', string, delimiter=',')

#np.savetxt('malaria_Y_test.csv',Y_valid,fmt='%i',delimiter = ",")

def resize_cell(img,i):
    new = cv2.resize(img,(100,100))
    print(new)
    cv2.imwrite('./classifier/infected_cell_{:>0}.png'.format(i), new)
    print('Done!')

def receive_images(path):
    i=1
    for file in glob.glob(path):
        print('File: ', file)
        img=cv2.imread(file)
        print('Image: ', img)
        resize_cell(img,i)
        i=i+1

def cell_recognition(crop, output, cropX, cropY, cropH, cropW, cellArray):
    cv2.imshow('sample',crop)
    cv2.waitKey(10)
    cell_cascade = cv2.CascadeClassifier('cell_cascade.xml')
    gray = gray_method(crop) 
    thresh=thresh_casc(gray)
    print('Cell Recognition')
    cell = cell_cascade.detectMultiScale(thresh, 1.3, 5)
    for (x, y, w, h) in cell:
        print('Coordinates: ', x, y, w, h)
        #cv2.rectangle(output, (x+cropX, y+cropY), (x + cropX + 32, y + cropY + 32), (20, 100, 50), 3)
        cellArray.append([cropX, cropY, cropH, cropW])
        #cv2.rectangle(output, (cropX, cropY), (cropX + cropH, cropY + cropW), (20, 100, 50), 4)

    #cv2.imshow('img', img)
    #cv2.waitKey(0)
    #quit()

def main():
    # image
    img = image_read("london_cell.jpg")
    # gray and thresh filter
    gray = gray_method(img)
    thresh = thresh_method(gray)
    contours(thresh, img)

if __name__ == "__main__":
    main()


