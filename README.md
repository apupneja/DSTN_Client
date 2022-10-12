# DSTN_Client

adb forward tcp:5000 localabstract:msg-test. Use this to forward data from adb to localhost to 5000 port.


Anirudh:
-Made the app to send the data over a single thread
-Used https://github.com/facebook/stetho to send data over usb to adb
