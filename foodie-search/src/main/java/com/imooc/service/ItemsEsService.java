package com.imooc.service;

import com.imooc.utils.PagedGridResult;

public interface ItemsEsService {

    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize);

}
