package com.example.demoproject.consumer;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.example.demoproject.producer.GroupInfo;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;

@AllArgsConstructor
@Slf4j
public class MultiUploadTask implements Runnable{
    private MultipartFile[] multipartFiles;
    private String fullFilePath;



    @Override
    public void run() {
        try {
            for (MultipartFile file : multipartFiles) {
                // 创建文件，通过 UUID 保证唯一
                File fileTemp = File.createTempFile(file.getOriginalFilename() + "_" + IdUtil.simpleUUID(), null);
                // 标记 JVM 退出时，自动删除
                fileTemp.deleteOnExit();
                //
                FileUtil.writeBytes(file.getBytes(), fileTemp);
                String filePath = FileUtil.writeFromStream(file.getInputStream(), new File(fullFilePath+file.getOriginalFilename())).getAbsolutePath();
                log.info(filePath);
            }
        } catch (Exception e){

        }
    }


}
