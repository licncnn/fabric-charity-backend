package org.ncu.BlockChainCharity.controller;


import org.apache.commons.io.FileUtils;
import org.ncu.BlockChainCharity.dto.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;



import javax.servlet.http.HttpServletResponse;
import java.io.*;


@Controller
@RequestMapping("image")
public class ImageController {



    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("getPicture")
    public void getPicture(String path, HttpServletResponse response) {
        if(path.contains(".jpg")){
            response.setContentType("image/jpg");
        }else if(path.contains(".png")){
            response.setContentType("image/png");
        }
//        System.out.println("图片地址----"+path);
        try{
            File file = new File(path);
            FileInputStream fis = new FileInputStream(file);
            long size = file.length();
            byte [] picture = new byte[(int)size];
            fis.read(picture);
            fis.close();
            OutputStream fos = response.getOutputStream();
            fos.write(picture);
            fos.flush();
            fos.close();
        }catch(IOException ioException){
//            System.out.println("图片地址----"+path);
//            logger.error("获取图片失败");
        }
    }


    // path --> avatar/xxx.png
    @GetMapping("getUploadPicture")
    public void getUploadPicture(String path, HttpServletResponse response) {
        if(path.contains(".jpg")){
            response.setContentType("image/jpg");
        }else if(path.contains(".png")){
            response.setContentType("image/png");
        }

        String currentDirectory = System.getProperty("user.dir");
        String uploadDirectory = currentDirectory+"/upload/"+path;

//        System.out.println("getUploadPicture=====>"+uploadDirectory);
//        System.out.println("图片地址----"+path);
        try{
            File file = new File(uploadDirectory);
            FileInputStream fis = new FileInputStream(file);
            long size = file.length();
            byte [] picture = new byte[(int)size];
            fis.read(picture);
            fis.close();
            OutputStream fos = response.getOutputStream();
            fos.write(picture);
            fos.flush();
            fos.close();
        }catch(IOException ioException){
//            System.out.println("图片地址----"+path);
//            logger.error("获取图片失败");
        }
    }


    @ResponseBody
    @PostMapping("uploadPicture")
    public ResponseEntity uploadPicture(@RequestParam("file") MultipartFile file, String dirnamme) {
        if (file.isEmpty()) {
            return new ResponseEntity(400,"文件为空，请选择一个文件上传", HttpStatus.BAD_REQUEST);
        }

        if(dirnamme.equals(null)){
            dirnamme="/";
        }

        try {
            // 获取项目resources下的static文件夹路径
            String currentDirectory = System.getProperty("user.dir");
            String uploadDirectory = currentDirectory+"/upload/"+dirnamme;


//            File staticFolder = new ClassPathResource("static").getFile();
//            if (!staticFolder.exists()) {
//                staticFolder.mkdirs();
//            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String newFileName = System.currentTimeMillis() + "_" + originalFilename;

            File targetFile =new File(uploadDirectory,newFileName);
            if(!targetFile.getParentFile().exists()){
                targetFile.getParentFile().mkdirs();
            }

            // 将上传的图片保存到static文件夹中
//            Path targetPath = Paths.get(targetFile.getAbsolutePath(), newFileName);
            FileUtils.writeByteArrayToFile(targetFile, file.getBytes());
            return new ResponseEntity(200,"success",dirnamme+"/"+newFileName);
//            return new ResponseEntity<>("文件上传成功，保存路径为：" + targetFile.getAbsolutePath(), HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();

            return new ResponseEntity(400, "error!!!","上传失败");
        }
    }

//    @PostMapping("uploadPicture")
//    public ResponseEntity<?> uploadPicture(@RequestParam("file") MultipartFile file) {
//        String toPath = "/";
//        if (file.isEmpty()) {
//            return new ResponseEntity<>("文件为空，请选择一个文件上传", HttpStatus.BAD_REQUEST);
//        }
//
//        try {
//            // 获取项目resources下的static文件夹路径
//            File staticFolder = new ClassPathResource("static").getFile();
//            if (!staticFolder.exists()) {
//                staticFolder.mkdirs();
//            }
//
//            // 生成文件名
//            String originalFilename = file.getOriginalFilename();
//            String newFileName = System.currentTimeMillis() + "_" + originalFilename;
//
//            // 将上传的图片保存到static文件夹中
//            Path targetPath = Paths.get(staticFolder.getAbsolutePath(), newFileName);
//            FileUtils.writeByteArrayToFile(targetPath.toFile(), file.getBytes());
//
//            return new ResponseEntity<>("文件上传成功，保存路径为：" + targetPath.toString(), HttpStatus.OK);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ResponseEntity<>("文件上传失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

}
