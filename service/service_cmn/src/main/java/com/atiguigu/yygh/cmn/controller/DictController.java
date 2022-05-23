package com.atiguigu.yygh.cmn.controller;

import com.atiguigu.yygh.model.cmn.Dict;
import com.atiguigu.yygh.cmn.service.DictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import com.atiguigu.yygh.common.result.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Api(value = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn/dict")
//@CrossOrigin //允许跨域
public class DictController {
    @Autowired
    private DictService dictService;

    //根据dictCode获取下级节点
    @ApiOperation(value = "根据dictCode获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result findByDictCoed(@PathVariable String dictCode){
        List<Dict> list = dictService.findByDictCoed(dictCode);
        return  Result.ok(list);
    }

    //导入数据字典
    @PostMapping("importData")
    public Result importDict(MultipartFile file){
        dictService.importDictData(file);
        return Result.ok();
    }

    //导出数据字典接口
    @GetMapping("exportData")
    public void exportDict(HttpServletResponse response){
        dictService.exportDictData(response);
        //return Result.ok();
    }


    //根据数据id查询子数据列表
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> list =  dictService.findChildData(id);
        return Result.ok(list);
    }

    //根据dictcode和value差值
    @GetMapping("getName/{dictCode}/{value}")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        String dictName = dictService.getDictName(dictCode,value);
        return dictName;
    }

    //根据value查询
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dictName = dictService.getDictName("",value);
        return dictName;
    }
}
