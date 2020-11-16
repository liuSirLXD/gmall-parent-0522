package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.SpuService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @Author:LiuSir
 * @Description: spu 操作
 * @Date: Create in 17:30 2020-11-01
 */
@RestController
@RequestMapping("admin/product")
@CrossOrigin
public class SpuApiController {
    @Autowired
    SpuService spuService;

    //得到文件的销售属性
    @RequestMapping("spuSaleAttrList/{spuId}")
    public Result spuSaleAttrList(@PathVariable Long spuId){
       List<SpuSaleAttr> saleAttrLists =  spuService.spuSaleAttrList(spuId);
        return Result.ok(saleAttrLists);
    }

    //得到图片列表
    @RequestMapping("spuImageList/{spuId}")
    public Result spuImageList(@PathVariable Long spuId){
        List<SpuImage> spuImages = spuService.spuImageList(spuId);
        return Result.ok(spuImages);
    }

    //上传文件到FastDFS
    @RequestMapping("fileUpload")
    public Result fileUpload(@RequestParam("file") MultipartFile multipartFile) throws IOException, MyException {
        //初始化文件路径，
        String path = SpuApiController.class.getClassLoader().getResource("tracker.conf").getPath();
        String host = SpuApiController.class.getClassLoader().getResource("tracker.conf").getHost();
        ClientGlobal.init(path);
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();

        StorageClient storageClient = new StorageClient(trackerServer,null);
        //文件上传
        String originalFilename = multipartFile.getOriginalFilename();//得到文件名
        String filenameExtension = StringUtils.getFilenameExtension(originalFilename);//得到文件后缀，什么类型的文件
        String[] strings = storageClient.upload_file(multipartFile.getBytes(), filenameExtension, null);

        StringBuffer url = new StringBuffer("http://");
        url = url.append(host);
        url = new StringBuffer(":8080");
        for (String string : strings) {
            url.append("/"+string);
        }
        return Result.ok(url);
    }

    //获取分页属性
    @RequestMapping("{pageNum}/{pageSize}")
    public Result spuList(Long category3Id, @PathVariable("pageNum")Long pageNum,@PathVariable("pageSize")Long pageSize){
        IPage<SpuInfo> spuInfoIPage = new Page<>();
        spuInfoIPage.setSize(pageSize);
        spuInfoIPage.setCurrent(pageNum);

        IPage<SpuInfo> spuInfoIPages = spuService.spuList(spuInfoIPage,category3Id);
        return Result.ok(spuInfoIPages);
    }

    //获取平台属性
    @RequestMapping("baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> saleAttrLists = spuService.baseSaleAttrList();
        return Result.ok(saleAttrLists);
    }

    //保存商品信息
    @RequestMapping("saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuService.saveSpuInfo(spuInfo);
        return Result.ok();
    }
}
