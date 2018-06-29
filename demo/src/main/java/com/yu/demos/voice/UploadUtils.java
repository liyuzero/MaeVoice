package com.yu.demos.voice;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadUtils {

    /**
     *
     * @param upLoadUrl 上传文件路径
     * @param filePath  本地文件路径
     * @return
     */
    public static String getResponseString(String upLoadUrl, String filePath) {

        //校验文件是否有内容
        File pcmFile = new File(filePath);
        if (pcmFile.length() == 0l) {
            return "请检查录音权限[在设置中开启JnluRobot的录音权限";
        }

        String res = "";
        HttpURLConnection conn = null;
        String BOUNDARY = "---------------------------" + System.currentTimeMillis(); //boundary就是request头和上传文件内容的分隔符
        try {
            URL url = new URL(upLoadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            OutputStream out = new DataOutputStream(conn.getOutputStream());

            //text
            StringBuffer strBufText = new StringBuffer();

            strBufText.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
            strBufText.append("Content-Disposition: form-data; name=\"deviceId\"\r\n\r\n");
            strBufText.append("android");

            out.write(strBufText.toString().getBytes());


            // file
            String filename = pcmFile.getName();

            StringBuffer strBuf = new StringBuffer();
            strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
            strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n");
            strBuf.append("Content-Type:audio/pcm; rate=16000\r\n\r\n");

            out.write(strBuf.toString().getBytes());

            DataInputStream in = new DataInputStream(new FileInputStream(pcmFile));
            int bytes = 0;
            byte[] bufferOut = new byte[1024];
            while ((bytes = in.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
            }
            in.close();

            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            // 读取返回数据
            StringBuffer strBufRespnse = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                strBufRespnse.append(line).append("\n");
            }
            reader.close();
            res = strBufRespnse.toString();

//            JSONObject jsonObject = new JSONObject(res);
//            Log.d("hehe", "结果：" + jsonObject.getJSONObject("responses").toString());
            return strBufRespnse.toString();

        } catch (Exception e) {
            Log.d("hehe", "上传错误：" + e.getMessage());
            return "";

        } finally {
            if (conn != null) {
                conn.disconnect();
                conn = null;
            }
        }
    }
}
