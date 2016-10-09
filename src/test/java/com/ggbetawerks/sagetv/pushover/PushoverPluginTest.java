package com.ggbetawerks.sagetv.pushover;

import org.junit.Test;
import sage.SageTVPluginRegistry;
import sage.msg.SystemMessage;
import sage.plugin.PluginEventManager;
import sagex.plugin.SageEvents;
import sagex.remote.json.JSONObject;
import sagex.util.LogProvider;

import static org.mockito.Mockito.*;

/**
 * Created by seans on 18/12/15.
 */
public class PushoverPluginTest {

    @Test
    public void testOnSystemMessage() throws Exception {
        LogProvider.useSystemOut();
        LogProvider.getLogger(PushoverPlugin.class).debug("Logging Initialized");

        // setup the event message
        PushoverPlugin plugin = spy(new PushoverPlugin(mock(SageTVPluginRegistry.class)));
        // just in case we are using a rest test
        //doReturn(getUserKey()).when(plugin).getUserKey();
        //doReturn(getAppToken()).when(plugin).getAppToken();
        // normally we just prevent the pushbullet call
        doNothing().when(plugin).sendMessage(any(String.class),any(String.class));

        // use the test send command
        plugin.onTestSend();

        // verify that the message was sent
        //verify(plugin, times(1)).sendMessage(any(String.class),any(String.class));
    }

    private Object getUserKey() {
        String key = System.getProperty("PUSHOVER_USERKEY",null);
        if (key==null) throw new RuntimeException("set -DPUSHOVER_USERKEY=YOUR_USER_KEY");
        return key;
    }
    private Object getAppToken() {
        String key = System.getProperty("PUSHOVER_APPTOKEN",null);
        if (key==null) throw new RuntimeException("set -DPUSHOVER_APPTOKEN=YOUR_APP_TOKEN");
        return key;
    }
}