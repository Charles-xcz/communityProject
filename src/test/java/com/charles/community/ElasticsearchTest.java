package com.charles.community;

import com.charles.community.dao.DiscussPostMapper;
import com.charles.community.dao.elasticsearch.DiscussPostRepository;
import com.charles.community.model.DiscussPost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.connection.SortParameters;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/22 13:20
 */
@SpringBootTest
public class ElasticsearchTest {
    @Resource
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(138, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(145, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(146, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(147, 0, 100));
    }

    @Test
    public void testUpdate() {
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(231);
        discussPost.setContent("我是我是新人,使劲使劲灌水~~~~");
        discussPostRepository.save(discussPost);
    }

    @Test
    public void testDelete() {
        discussPostRepository.deleteById(231);
//        discussPostRepository.deleteAll();
    }

    @Test
    public void testSearchByRepository() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        /*
        discussPostRepository.search(query)方法,查询到的高亮结果没做处理,返回的是原始的非高亮的结果
         */
        Page<DiscussPost> pages = discussPostRepository.search(query);
        //总命中数
        System.out.println(pages.getTotalElements());
        //总页数
        System.out.println(pages.getTotalPages());
        //当前第几页
        System.out.println(pages.getNumber());
        //页面尺寸
        System.out.println(pages.getSize());
        pages.forEach(post -> {
            System.out.println(post);
        });
    }

    @Test
    public void testSearchByTemplate() {
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        Page<DiscussPost> pages = elasticsearchTemplate.queryForPage(query, DiscussPost.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> aClass, Pageable pageable) {
                SearchHits hits = response.getHits();
                if (hits.getTotalHits() <= 0) {
                    return null;
                }
                List<DiscussPost> list = new ArrayList<>();
                hits.forEach(hit -> {
                    DiscussPost post = new DiscussPost();
                    Map<String, Object> map = hit.getSourceAsMap();

                    String id = map.get("id").toString();
                    post.setId(Integer.parseInt(id));
                    String userId = map.get("userId").toString();
                    post.setUserId(Integer.parseInt(userId));
                    String type = map.get("type").toString();
                    post.setType(Integer.parseInt(type));
                    String status = map.get("status").toString();
                    post.setStatus(Integer.parseInt(status));
                    String commentCount = map.get("commentCount").toString();
                    post.setCommentCount(Integer.parseInt(commentCount));
                    String createTime = map.get("createTime").toString();
                    post.setCreateTime(new Date(Long.parseLong(createTime)));
                    String score = map.get("score").toString();
                    post.setScore(Double.parseDouble(score));
                    String title = map.get("title").toString();
                    post.setTitle(title);
                    String content = map.get("content").toString();
                    post.setTitle(content);

                    HighlightField titleField = hit.getHighlightFields().get("title");
                    if (titleField != null) {
                        post.setTitle(titleField.getFragments()[0].toString());
                    }
                    HighlightField contentField = hit.getHighlightFields().get("content");
                    if (contentField != null) {
                        post.setContent(contentField.getFragments()[0].toString());
                    }
                    list.add(post);
                });

                return new AggregatedPageImpl(list, pageable, hits.getTotalHits(), response.getAggregations(), response.getScrollId(), hits.getMaxScore());
            }

            @Override
            public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                return null;
            }
        });

        //总命中数
        System.out.println(pages.getTotalElements());
        //总页数
        System.out.println(pages.getTotalPages());
        //当前第几页
        System.out.println(pages.getNumber());
        //页面尺寸
        System.out.println(pages.getSize());
        pages.forEach(post -> {
            System.out.println(post);
        });
    }

}
