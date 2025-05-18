package com.xu.xuoj.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * EasyExcel 测试
 *
 * @author <a href="https://github.com/lixu">程序员鱼皮</a>
 * @from <a href="https://xu.icu">编程导航知识星球</a>
 */
@SpringBootTest
public class EasyExcelTest {

    @Test
    public void doImport() throws FileNotFoundException {
        File file = ResourceUtils.getFile("classpath:test_excel.xlsx");
        List<Map<Integer, String>> list = EasyExcel.read(file)
            .excelType(ExcelTypeEnum.XLSX)
            .sheet()
            .headRowNumber(0)
            .doReadSync();
        System.out.println(list);
    }

    @Test
    public void doTest() {
        String content = "ZZZaaabbbccc中文1234";
        String resultExtractMulti = ReUtil.extractMulti("(\\w)aa(\\w)", content, "$1-$2");
        Assert.equals("Z-a", resultExtractMulti);
    }

}