# TimeOff

A simple application to store time off data to a remote database.

Libraries:
* Latest Design Support Library
* Volley Networking Library

This app will respond to user actions if device is offline and syncs the data with database when device is online.

App has a background service with a timer task which repeat itself for an interval and checks for network connecction.
If newtwok is available, data is synced with the help of volley network queue mechanism.


