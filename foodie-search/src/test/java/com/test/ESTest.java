package com.test;

import com.imooc.Application;
import com.imooc.es.pojo.Items;
import com.imooc.es.pojo.Stu;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ESTest {

    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Test
    public void createIndexStu() {
        Stu stu = new Stu();
        stu.setStuId(1002L);
        stu.setName("spider man");
        stu.setAge(20);
        stu.setMoney(20.8f);
        stu.setSign("good");
        stu.setDescription("I want to be rich");

        IndexQuery indexQuery = new IndexQueryBuilder().withObject(stu).build();
        esTemplate.index(indexQuery);
    }

    @Test
    public void deleteIndexStu() {
        esTemplate.deleteIndex(Stu.class);
    }

    @Test
    public void updateStuDoc() {
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("name", "spider man");
        sourceMap.put("age", 24);

        IndexRequest indexRequest = new IndexRequest();
        indexRequest.source(sourceMap);

        UpdateQuery updateQuery = new UpdateQueryBuilder().withClass(Stu.class).withId("1002").withIndexRequest(indexRequest).build();

        esTemplate.update(updateQuery);
    }

    @Test
    public void getStuDoc() {
        GetQuery query = new GetQuery();
        query.setId("1002");
        Stu stu = esTemplate.queryForObject(query, Stu.class);

        System.out.println(stu.toString());
    }

    @Test
    public void getItemDoc() {
        GetQuery query = new GetQuery();
        query.setId("cake-1001");
        Items items = esTemplate.queryForObject(query, Items.class);

        System.out.println(items.toString());
    }

    @Test
    public void deleteStuDoc() {
        esTemplate.delete(Stu.class, "1002");
    }

    @Test
    public void searchStuDoc() {

        Pageable pageable =  PageRequest.of(0,10);

        SortBuilder sortBuilder = new FieldSortBuilder("age").order(SortOrder.DESC);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                                        .withQuery(QueryBuilders.matchQuery("name", "man"))
                                        .withSort(sortBuilder)
                                        .withPageable(pageable)
                                        .build();

        AggregatedPage<Stu> pageStu = esTemplate.queryForPage(searchQuery, Stu.class);
        System.out.println("检索后的总分页数目为：" + pageStu.getTotalPages());
        List<Stu> stuList = pageStu.getContent();
        for(Stu s : stuList){
            System.out.println(s);
        }
    }

    @Test
    public void searchItemDoc() {

        Pageable pageable =  PageRequest.of(0,10);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("itemName", "好吃"))
                .withPageable(pageable)
                .build();

        AggregatedPage<Items> pageItems = esTemplate.queryForPage(searchQuery, Items.class);
        System.out.println("检索后的总分页数目为：" + pageItems.getTotalPages());
        List<Items> itemsList = pageItems.getContent();
        for(Items item : itemsList){
            System.out.println(item);
        }
    }

    @Test
    public void hightLightStuDoc() {

        String preTag = "<font color='red'>";
        String postTag = "</font>";

        Pageable pageable =  PageRequest.of(0,10);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("name", "man"))
                .withHighlightFields(new HighlightBuilder.Field("name").preTags(preTag).postTags(postTag))
                .withPageable(pageable)
                .build();

        AggregatedPage<Stu> pageStu = esTemplate.queryForPage(searchQuery, Stu.class, new SearchResultMapper(){

            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {

                List<Stu> stuListHL = new ArrayList<>();

                SearchHits hits = searchResponse.getHits();
                for(SearchHit hit : hits){
                    HighlightField highlightField = hit.getHighlightFields().get("name");
                    String name = highlightField.getFragments()[0].toString();

                    Object stuId = (Object) hit.getSourceAsMap().get("stuId");

                    Stu stu = new Stu();
                    stu.setStuId(Long.valueOf(stuId.toString()));
                    stu.setName(name);

                    stuListHL.add(stu);
                }

                if(stuListHL.size() > 0){
                    return new AggregatedPageImpl<T>((List<T>) stuListHL);
                }
                return null;
            }
        });
        System.out.println("检索后的总分页数目为：" + pageStu.getTotalPages());
        List<Stu> stuList = pageStu.getContent();
        for(Stu s : stuList){
            System.out.println(s);
        }
    }
}
