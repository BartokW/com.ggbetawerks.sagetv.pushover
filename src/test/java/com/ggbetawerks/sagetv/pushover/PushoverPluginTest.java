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
        // doReturn(getApiKey()).when(plugin).getApiKey();
        // normally we just prevent the pushbullet call
        doNothing().when(plugin).sendMessage(any(JSONObject.class));

        // use the test send command
        plugin.onTestSend();

        // verify that the message was sent
        verify(plugin, times(1)).sendMessage(any(JSONObject.class));
    }

    private Object getApiKey() {
        String key = System.getProperty("PUSHBUTTON_API",null);
        if (key==null) throw new RuntimeException("set -DPUSHBUTTON_API=YOUR_API_KEY");
        return key;
    }
}