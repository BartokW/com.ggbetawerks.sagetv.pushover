**Pushover Notifications for SageTV**

This SageTV Plugin enables SageTV System Messages to be pushed to a desktop or mobile device running the Pushover Application.

A Pushover account is required, and you will need to create an Application. You will need to configure your User key, and App token in the Plugin Configuration.

The easiest way to do this is to simply edit the Sage.properties (when the server is not running) and add the following key

```
com/ggbetawerks/plugin/pushover/userkey=USER_KEY_FROM_ACCOUNT
com/ggbetawerks/plugin/pushover/apptoken=APP_TOKEN_FROM_APPS_TAB
```

Take note to replace **USER_KEY_FROM_ACCOUNT** with your actual Pushover USER KEY, and replace **APP_TOKEN_FROM_APPS_TAB** with the API Key from the Application you created on the Apps & Plugins page (https://pushover.net/apps).

If you do this **BEFORE** you install the plugin, then Pushover will be configured.  You can then go into the Plugin Configuration options and turn on/off the messages that you want to receive.  Also, you can send a test message to verify that it is working.


You can sign-up for a Pushover account here
https://pushover.net

