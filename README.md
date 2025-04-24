# FacebookNotifier

This program enables the user to get desired notifications on his Discord server via Gmail's API. The program is controlled in terminal (GUI in the future).
**To run this program you will need**

- A Facebook account
- A Discord account, server and bot
- A Gmail account, API key and a folder for Facebook's notifications
- Microsoft Edge installed in the default path in C drive

**How to setup Discord server and bot, Gmail folder and API key**
To be added...

**How the program works**

The folder that has been set to receive Facebook notifications is checked for 5 of the latest notifications every 10 seconds. If the Facebook post includes any set keywords, a notification is sent in the Discord server with the post's text and link.

**If the program isn't working correctly**

Facebook gets stuck with notifications about once a day. When this happens, Facebook sends repeated notifications of old posts. To fix it disable all notifications from Facebook's settings, AFK with Facebook open and set the notifications back on.