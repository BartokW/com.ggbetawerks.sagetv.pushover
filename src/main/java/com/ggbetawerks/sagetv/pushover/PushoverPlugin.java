package com.ggbetawerks.sagetv.pushover;

import sage.SageTVPlugin;
import sage.SageTVPluginRegistry;
import sage.msg.SystemMessage;
import sage.plugin.PluginEventManager;
import sagex.plugin.*;
import sagex.remote.json.JSONException;
import sagex.remote.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static sage.msg.SystemMessage.*;

/**
 * Created by seans on 18/12/15.
 * Modified by jonw on 2016-10-08.
 */
public class PushoverPlugin extends AbstractPlugin {
    public static final String PUSHOVER_URL = "https://api.pushover.net/1/messages.json";
    public static final String PROP_BASE = "com/ggbetawerks/plugin/pushover/";
    public static final String PROP_USER_KEY = PROP_BASE + "userkey";
    public static final String PROP_APP_TOKEN = PROP_BASE + "apptoken";
    public static final String PROP_ENABLE_ALL = PROP_BASE + "enableAll";
    public static final String PROP_TEST_SEND = PROP_BASE + "testSend";

    public PushoverPlugin(SageTVPluginRegistry registry) {
        super(registry);
    }

    @Override
    public void start() {
        super.start();

        addProperty(SageTVPlugin.CONFIG_TEXT, PROP_USER_KEY, "", "USER KEY", "Pushover User Key");
        addProperty(SageTVPlugin.CONFIG_TEXT, PROP_APP_TOKEN, "", "APP TOKEN", "Pushover Application API Token");
        addProperty(SageTVPlugin.CONFIG_BOOL, PROP_ENABLE_ALL, "false", "Enable All System Events", "If enabled, then ALL System Events will be pushed");

        int messages[] = new int[] {
        LINEUP_LOST_FROM_SERVER_MSG,
        NEW_CHANNEL_ON_LINEUP_MSG,
        CHANNEL_SCAN_NEEDED_MSG,
        EPG_UPDATE_FAILURE_MSG,
        EPG_LINKAGE_FOR_MR_CHANGED_MSG,

        MISSED_RECORDING_FROM_CONFLICT_MSG,
        CAPTURE_DEVICE_LOAD_ERROR_MSG,
        PARTIAL_RECORDING_FROM_CONFLICT_MSG,

        ENCODER_HALT_MSG,
        CAPTURE_DEVICE_RECORD_ERROR_MSG,
        MISSED_RECORDING_FROM_CAPTURE_FAILURE_MSG,
        DISKSPACE_INADEQUATE_MSG,
        CAPTURE_DEVICE_DATASCAN_ERROR_MSG,
        VIDEO_DIRECTORY_OFFLINE_MSG,
        PLAYLIST_MISSING_SEGMENT,

        SYSTEM_LOCKUP_DETECTION_MSG,
        OUT_OF_MEMORY_MSG,
        SOFTWARE_UPDATE_AVAILABLE_MSG,
        STORAGE_MONITOR_MSG,
        GENERAL_MSG,
        PLUGIN_INSTALL_MISSING_FILE_MSG
        };

        for (int message: messages) {
            addProperty(SageTVPlugin.CONFIG_BOOL, PROP_BASE + String.valueOf(message), "false", SystemMessage.getNameForMsgType(message), SystemMessage.getNameForMsgType(message))
                    .setVisibility(new IPropertyVisibility() {
                        @Override
                        public boolean isVisible() {
                            return !getConfigBoolValue(PROP_ENABLE_ALL);
                        }
                    });
        }

        addProperty(SageTVPlugin.CONFIG_BUTTON, PROP_TEST_SEND, "TEST", "Send Test Notification", "Send Test Notification");
    }

    @Override
    public void stop() {
        super.stop();
    }

    @ButtonClickHandler(PROP_TEST_SEND)
    public void onTestSend() {
        log.debug("Sending Test Notification");
        try {
            Properties props = new Properties();
            props.setProperty("testa", "Field A Value");
            props.setProperty("testb", "Field B Value");
            SystemMessage systemMessage = new SystemMessage(Integer.MAX_VALUE, SystemMessage.ERROR_PRIORITY, "Test Message", props);
            Map event = new HashMap();
            event.put(PluginEventManager.VAR_SYSTEMMESSAGE, systemMessage);
            event.put(SageEvents.SystemMessagePosted, event);

            // call the plugin with the event
            onSystemMessage(event);
        } catch (Throwable t) {
            log.error("Test PushBullet Send Failed", t);
        }
    }

    @SageEvent(value= SageEvents.SystemMessagePosted, background = true)
    public void onSystemMessage(Map vars) {
        //Authorization: Bearer $API
        //https://api.pushbullet.com/v2/pushes
        // Content-Type: application/json
        // {\"type\": \"note\", \"title\": \"Rename Failed!!!\", \"body\": \"$FILE was not renamed because $MSG \"}
        try {
            SystemMessage msg = (SystemMessage) vars.get(PluginEventManager.VAR_SYSTEMMESSAGE);
            if (!canSendNotification(msg)) {
                return;
            }
/*
            JSONObject pb = new JSONObject();
            pb.put("type","note");
            pb.put("title", msg.getMessageText());
            pb.put("message", formatMessageBody(msg));*/

            sendMessage(msg.getMessageText(), formatMessageBody(msg));
        } /*catch (JSONException e) {
            log.error("JSON Error", e);
        } */catch (IOException e) {
            log.error("Failed to use Pushover", e);
        } catch (Throwable t) {
            log.error("Erorr", t);
        }
    }

    @SageEvent(value = SageEvents.RecordingCompleted, background = true)
    public void onRecordingComplete(Map vars) {
        Object mf = vars.get(PluginEventManager.VAR_MEDIAFILE);

    }

    void sendMessage(String title, String message) throws IOException {
        RequestBuilder builder = new RequestBuilder(PUSHOVER_URL);
        builder.setContentType("application/x-www-form-urlencoded");
        //builder.addHeader("Authorization", "Bearer " + getUserKey());
        //builder.setBody(pb.toString());
        builder.addParameter("token", getAppToken());
        builder.addParameter("user", getUserKey());
        builder.addParameter("title", title);
        builder.addParameter("message", message);
        builder.postRequest();
    }

    boolean canSendNotification(SystemMessage msg) {
        if (msg==null) return false;
        if (getConfigBoolValue(PROP_ENABLE_ALL)) return true;
        if (msg.getType() == Integer.MAX_VALUE) return true; // test message
        return getConfigBoolValue(PROP_BASE + String.valueOf(msg.getType()));
    }

    String getUserKey() {
        return getConfigValue(PROP_USER_KEY);
    }
    String getAppToken() {
        return getConfigValue(PROP_APP_TOKEN);
    }

    String formatMessageBody(SystemMessage msg) {
        if (msg==null) return "";
        StringBuilder sb = new StringBuilder();
        if (msg.getRepeatCount()>1) {
            sb.append("Repeat Count" + msg.getRepeatCount() + "\n");
        }
        if (msg.getMessageVarNames()!=null) {
            for (String s: msg.getMessageVarNames()) {
                sb.append(s).append(": ").append(msg.getMessageVarValue(s)).append("\n");
            }
        }
        return sb.toString();
    }
}
