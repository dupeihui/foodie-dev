package com.imooc.controller;

import com.imooc.service.ItemsEsService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("items")
public class ItemsController {

    @Autowired
    private ItemsEsService itemsEsService;

    @GetMapping("/hello")
    public Object hello() {
        return "hello elasticsearch~";
    }

    @GetMapping("/es/search")
    public IMOOCJSONResult search(String keywords, String sort, Integer page, Integer pageSize) {

        if (StringUtils.isBlank(keywords)) {
            return IMOOCJSONResult.errorMsg(null);
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        page--;

        PagedGridResult grid = itemsEsService.searchItems(keywords, sort, page, pageSize);

        return IMOOCJSONResult.ok(grid);
    }

}
