package com.example.demoproject.producer;

import com.example.demoproject.consumer.MultiUploadTask;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;

@RestController
@Slf4j
public class RedisController {
    @Value("${spring.data.redis.filepath}")
    private String fullFilePath;

    @Autowired
    private RedisStreamTemplate redisStream;

    @RequestMapping(value = "/redis/create", method = RequestMethod.GET)
    public String redisCreate(@RequestParam(name="key") String key,@RequestParam(name="group") String group) throws Exception {
        return redisStream.creartGroup();
    }

    @RequestMapping(value = "/redis/add")
    public String redisSend(@RequestParam(name="key") String key,@RequestParam(name = "message") String message) throws UnknownHostException {
        return redisStream.add(InetAddress.getLocalHost().getHostAddress(),message);
    }
    @RequestMapping(value = "/redis/consume", method = RequestMethod.GET)
    public String redisConsume(@RequestParam(name = "key") String key,@RequestParam(name = "group")String group) throws Exception {
        StreamInfo.XInfoConsumers consumer = redisStream.consumers();
        return consumer.toString();
    }

    @RequestMapping(value = "/redis/groups", method = RequestMethod.GET)
    public String redisConsume(@RequestParam(name = "key") String key) throws Exception {
        StreamInfo.XInfoGroups groups = redisStream.groups(key);
        return groups.toString();
    }

    @RequestMapping(value = "/redis/upload",method = RequestMethod.POST)
    public String filesUpload(@RequestParam(name = "files")MultipartFile[] files) throws IOException {

        MultiUploadTask multiUploadTask = new MultiUploadTask(files,fullFilePath);
        Thread t = new Thread(multiUploadTask);
        t.start();

        return "OK";
    }
    @RequestMapping(value = "/redis/download",method = RequestMethod.GET)
    public void download(HttpServletResponse response) throws Exception {
        File f = new File(fullFilePath);

        File[] files = new File[0];
        if(f.isDirectory()){
            files = f.listFiles();
        }
        FileInputStream fis = new FileInputStream(files[0]);

        byte[] bytes = new byte[fis.available()];
        fis.read(bytes);
        // 清空response
        response.reset();
        // 设置response的Header
        response.setCharacterEncoding("UTF-8");
        //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
        //attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
        // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(files[0].getName(), "UTF-8"));
        // 告知浏览器文件的大小
        response.addHeader("Content-Length", "" + files[0].length());

        OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
        response.setContentType(MediaTypeFactory.getMediaType(files[0].getName()).orElse(MediaType.APPLICATION_OCTET_STREAM).toString());
        outputStream.write(bytes);

        outputStream.flush();
        //response.sendRedirect("http://www.baidu.com"); //不可以和flush同时进行 因为response 已经提交

        fis.close();


    }
}
