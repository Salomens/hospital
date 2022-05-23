package com.atiguigu.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.atiguigu.yygh.model.cmn.Dict;
import com.atiguigu.yygh.vo.cmn.DictEeVo;
import com.atiguigu.yygh.cmn.listener.DictListener;
import com.atiguigu.yygh.cmn.mapper.DictMapper;
import com.atiguigu.yygh.cmn.service.DictService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    @Autowired
    private DictMapper dictMapper;

    //根据数据id查询子数据列表
    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        //向list集合每个dict对象中设置hasChildren
        for(Dict dict:dictList){
            Long dictId = dict.getId();
            boolean ischild = this.isChildren(dictId);
            dict.setHasChildren(ischild);
        }
        return dictList;
    }

    //导出数据字典
    @Override
    @CacheEvict(value = "dict", allEntries=true) //alEntries表示清空缓存中的内容
    public void exportDictData(HttpServletResponse response) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = "dict";
        response.setHeader("Content-disposition", "attachment;filename="+ fileName + ".xlsx");
        //查询数据库
        List<Dict> dictList = baseMapper.selectList(null);

        List<DictEeVo> dictVoList = new ArrayList<>(dictList.size());
        for(Dict dict : dictList) {
            DictEeVo dictVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictVo);
            //BeanUtils.copyBean(dict, dictVo, DictEeVo.class);
            dictVoList.add(dictVo);
        }
        //调用方法完成写的操作
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict")
                    .doWrite(dictVoList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void importDictData(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();//或者是用dictmapper
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //判断id下面是否有子节点
    private  boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Integer count = baseMapper.selectCount(wrapper);
        return count>0;
    }
//    @Autowired
//    private HospitalSetMapper hospitalSetMapper;

    //根据dictcode和value查询
    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictcode为空
        System.out.println("进入到getDictName");
        if(StringUtils.isEmpty(dictCode)){
            System.out.println("进入到if中");
            QueryWrapper<Dict>wrapper = new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(wrapper);
            return dict.getName();
        }else {//如果dictCode不为空，根据dictCode和value查询
            //根据dictcode查询dict对象，得到dict的id值
            Dict codeDict = this.getDictByDictCode(dictCode);
            Long parent_id = codeDict.getId();
            //根据parent_id和value进行查询
            QueryWrapper<Dict> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("value",value);
            wrapper1.eq("parent_id",parent_id);
            Dict finalDict = baseMapper.selectOne(wrapper1);
            System.out.println("finalDict："+finalDict);
            return finalDict.getName();
        }
    }

    private Dict getDictByDictCode(String dictCode){
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict codedict = baseMapper.selectOne(wrapper);
        System.out.println("进入getDictByDictCode");
        return codedict;
    }

    //根据dictCode获取下级节点
    @Override
    public List<Dict> findByDictCoed(String dictCode) {
        //根据dictcode获取对应的id
        Dict dict = this.getDictByDictCode(dictCode);
        //根据id获取子节点
        List<Dict> childData = this.findChildData(dict.getId());
        return childData;
    }

}
