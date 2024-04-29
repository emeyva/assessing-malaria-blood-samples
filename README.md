# Assessing blood samples for Malaria

Use of an app that captures photographs of blood samples and finds if they are infected with Malaria

There are two folders on these project, the first one is about the Mobile App and the second one is more focused on running test under Python with OpenCV. 

Most of the techniques are firstly used under Python and then converted manually to an Android mobile app.

- Final project report [here](finalProjectReport.pdf)
- A simple summary [here](summaryPoster.pdf) 

## Getting Started

These instructions will get you a copy of the Assessing blood samples for Malaria project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

Install OpenCV and Python

On Linux
```
sudo apt-get install python3
sudo apt-get install libopencv-dev python-opencv
sudo apt-get install libavcodec-dev libavformat-dev libswscale-dev libv4l-dev
```

On MacOS
```
brew install python
brew tap homebrew/science
brew install opencv3 --with-contrib
brew install numpy
brew install jpeg libpng libtiff openexr
ln -s /usr/local/opt/opencv3/lib/python2.7/site-packages/cv2.so \
  /usr/local/lib/python2.7/site-packages/
echo ’#Homebrew’ >> ~/.bash_profile
echo ’export PATH=/usr/local/bin:$PATH’ >> ~/.bash_profile
source ~/.bash_profile
```

### Installing

Install PyCharm and Android Studio

* [PyCharm](https://www.jetbrains.com/pycharm/) - Python IDE
* [Android Studio](https://developer.android.com/studio) - Android IDE


## Running the tests

Open Python test on PyCharm or run it from the terminal

Open Android folder on Android Studio and run it on you mobile phone or an emulator

### Break down into end to end tests

Android mobile app:

It asks the user to take a photograph of a blood sample (microscopic one), then it show the image taken with all the contours detected drawn on the same image.
Instead of taking a picture, it is possible to import a file from the phone library.
It also loads cell Cascade to detect infected cells, drawn with green squares (not efficient at the moment)

Python:

It take the 'london_cell.jpg' picture, analyses it and checks every cell with a 'cell_cascade.xml' classifier, trained before-hand. Then, outputs the actual infected cells as black rectangles, but also the detected infected cells as green/red rectangles.

Cascade Training:

Cascade training is under the terminal and uses OpenCV method - traincascade and also positive and negative images of Malaria infected or uninfected cells


## Contributing

Please read [Coding Robin](https://coding-robin.de/2013/07/22/train-your-own-opencv-haar-classifier.html) for information about Training Cascade Classifier

## Versioning

Version 1

## Authors

Eurico Pinto

University of Essex
