# Assessing blood samples for Malaria

Use of an app that captures photographs of blood samples and finds if they are infected with Malaria

There are two folders on these project, the first one is about the Mobile App and the second one is more focused on running test under Python with OpenCV. 

Most of the techniques are firstly used under Python and then converted manually to an Android mobile app.

##### Dependencies on both platforms:
import OpenCV

### Android mobile app:
It asks the user to take a photograph of a blood sample (microscopic one), then it show the image taken with all the contours detected drawn on the same image.
Instead of taking a picture, it is possible to import a file from the phone library.
It also loads cell Cascade to detect infected cells, drawn with green squares (not efficient at the moment)

### Python:
It take the 'london_cell.jpg' picture, analyses it and checks every cell with a 'cell_cascade.xml' classifier, trained before-hand. Then, outputs the actual infected cells as black rectangles, but also the detected infected cells as green/red rectangles.

### Cascade Training:
Cascade training is under the terminal and uses OpenCV method - traincascade and also positive and negative images of Malaria infected or uninfected cells


##### In progress...


