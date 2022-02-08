package tech.pixelw.dmr_core.service;

import android.content.Context;

import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.fourthline.cling.support.model.Protocol;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConnectionManagerServiceImpl extends ConnectionManagerService {
    public ConnectionManagerServiceImpl(Context context) {
        try {
            sinkProtocolInfo.addAll(new ProtocolInfos(getCsvFile(context)));
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    private String getCsvFile(Context context) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            InputStream inputStream = context.getAssets().open("sink_protocol_info.csv");
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }
}
