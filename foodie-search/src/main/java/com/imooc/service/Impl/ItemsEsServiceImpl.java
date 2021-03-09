package com.imooc.service.Impl;

import com.imooc.es.pojo.Items;
import com.imooc.service.ItemsEsService;
import com.imooc.utils.PagedGridResult;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemsEsServiceImpl implements ItemsEsService {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Override
    public PagedGridResult searchItems(String keywords, String sort, Integer page, Integer pageSize) {
        String preTag = "<font color='red'>";
        String postTag = "</font>";

        Pageable pageable =  PageRequest.of(page,pageSize);

        SortBuilder sortBuilder = null;

        if (sort.equals("c")) {
            sortBuilder = new FieldSortBuilder("sellCounts").order(SortOrder.DESC);
        } else if (sort.equals("p")) {
            sortBuilder = new FieldSortBuilder("price").order(SortOrder.ASC);
        } else {
            sortBuilder = new FieldSortBuilder("itemName.keyword").order(SortOrder.ASC);
        }

        String itemNameField = "itemName";

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(itemNameField, keywords))
                .withHighlightFields(new HighlightBuilder.Field(itemNameField)
//                        .preTags(preTag)
//                        .postTags(postTag)
                )
                .withSort(sortBuilder)
                .withPageable(pageable)
                .build();

        AggregatedPage<Items> pageItems = esTemplate.queryForPage(searchQuery, Items.class, new SearchResultMapper(){

            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                List<Items> itemsListHL = new ArrayList<>();

                SearchHits hits = searchResponse.getHits();
                for(SearchHit hit : hits){
                    HighlightField highlightField = hit.getHighlightFields().get(itemNameField);
                    String itemName = highlightField.getFragments()[0].toString();

                    String itemId = (String)hit.getSourceAsMap().get("itemId");
                    String imgUrl = (String)hit.getSourceAsMap().get("imgUrl");
                    Integer price = (Integer)hit.getSourceAsMap().get("price");
                    Integer sellCounts = (Integer)hit.getSourceAsMap().get("sellCounts");

                    Items item = new Items();
                    item.setItemId(itemId);
                    item.setItemName(itemName);
                    item.setImgUrl(imgUrl);
                    item.setPrice(price);
                    item.setSellCounts(sellCounts);

                    itemsListHL.add(item);
                }

                return new AggregatedPageImpl<>((List<T>) itemsListHL, pageable, searchResponse.getHits().totalHits);

            }
        });

        System.out.println("检索后的总分页数目为：" + pageItems.getTotalPages());
        List<Items> itemsList = pageItems.getContent();
        for(Items s : itemsList){
            System.out.println(s);
        }

        PagedGridResult gridResult = new PagedGridResult();
        gridResult.setRows(pageItems.getContent());
        gridResult.setPage(page + 1);
        gridResult.setTotal(pageItems.getTotalPages());
        gridResult.setRecords(pageItems.getTotalElements());

        return  gridResult;
    }
}
