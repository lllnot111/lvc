package Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import static javafx.scene.input.KeyCode.F;

public class HttpUtils {

    HttpClient client = HttpClients.createDefault();
    public String post(String uri,String body) {
        HttpPost post = new HttpPost(uri);
        try {
            post.setHeader("Content-type","application/json");
            post.setEntity(new StringEntity(body));
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            return result;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String postByForm(String uri,MultipartEntityBuilder entityBuilder) throws Exception{
        HttpPost post = new HttpPost(uri);
            post.setEntity(entityBuilder.build());
            HttpResponse response = client.execute(post);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            return result;
    }
}



class Get{
    HttpClient client = HttpClients.createDefault();
    public void run() {
        HttpGet get = new HttpGet("http://www.baidu.com");
        try {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            System.out.println(result);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
class Test{
    String a;
}