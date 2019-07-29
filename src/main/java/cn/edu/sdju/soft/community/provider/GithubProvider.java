package cn.edu.sdju.soft.community.provider;

import cn.edu.sdju.soft.community.dto.AccessTokenDTO;
import cn.edu.sdju.soft.community.dto.GithubUser;
import com.alibaba.fastjson.JSON;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    //@Controller是将当前类作为路由API的一个承载者，@Component是仅仅将当前类初始化到spring容器的上下文（不需要实例化对象，要用的时候直接拿出来）

    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split("&")[0].split("=")[1];
            /*String tokenstr = split[0];
            String token = tokenstr.split("=")[1];*/
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=31929b909d039b0d0f93a7fd819f66c5ae447776")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            //将json对象自动解析转换成java的类对象
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            System.out.println(githubUser);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
