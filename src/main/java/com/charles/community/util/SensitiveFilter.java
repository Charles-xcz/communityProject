package com.charles.community.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author charles
 * @date 2020/3/19 12:30
 */
@Component
@Slf4j
public class SensitiveFilter {
    /**
     * 定义前缀树结构
     */
    private class TrieNode {
        //关键词结束的标识
        private boolean isKeyWordsEnd = false;
        //子节点(key是子节点字符,value是子节点)
        private Map<Character, TrieNode> childNodes = new HashMap<>();

        public boolean isKeyWordsEnd() {
            return isKeyWordsEnd;
        }

        public void setKeyWordsEnd(boolean keyWordsEnd) {
            isKeyWordsEnd = keyWordsEnd;
        }

        public void addChildNode(Character c, TrieNode node) {
            childNodes.put(c, node);
        }

        public TrieNode getChildNode(Character c) {
            return childNodes.get(c);
        }
    }

    /**
     * 替换敏感词
     */
    private static final String REPLACEMENT = "***";
    /**
     * 根节点
     */
    private TrieNode rootNode = new TrieNode();

    /**
     * 初始化Trie树方法
     */
    @PostConstruct
    public void init() {
        try (
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words/sensitive-words.txt");
                BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword = null;
            while ((keyword = bf.readLine()) != null) {
                //添加到trie树
                this.add(keyword);
            }
        } catch (Exception e) {
            log.error("加载敏感词文件失败:{}", e.getMessage());
        }

    }

    private void add(String keyword) {
        TrieNode currentNode = rootNode;
        for (char c : keyword.toCharArray()) {
            TrieNode childNode = currentNode.getChildNode(c);
            if (null == childNode) {
                childNode = new TrieNode();
                currentNode.addChildNode(c, childNode);
            }
            // Enter the next cycle
            currentNode = childNode;
        }
        currentNode.setKeyWordsEnd(true);
    }

    /**
     * 过滤敏感词
     *
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //指针1:
        TrieNode currentNode = rootNode;
        //指针2:
        int beginIndex = 0;
        //指针3:
        int position = 0;
        //输出的结果
        StringBuilder sb = new StringBuilder();
        while (position < text.length()) {
            char c = text.charAt(position);
            //跳过特殊符号
            if (isSymbol(c)) {
                //若指针1处于根节点,则将此符号计入结果,让指针2向后移动
                if (currentNode == rootNode) {
                    sb.append(c);
                    beginIndex++;
                }
                position++;
                continue;
            }
            //检查下一节点
            currentNode = currentNode.getChildNode(c);
            if (currentNode == null) {
                //以beginIndex开头的字符串不是敏感词
                sb.append(text.charAt(beginIndex));
                position = ++beginIndex;
                currentNode = rootNode;
            } else if (currentNode.isKeyWordsEnd) {
                //发现敏感词,beginIndex~position段字符串替换掉
                sb.append(REPLACEMENT);
                beginIndex = ++position;
            } else {
                position++;
            }
        }
        sb.append(text.substring(beginIndex));
        return sb.toString();
    }

    /**
     * 判别特殊符号
     *
     * @param c 输入字符
     * @return 如果是特殊符号返回true, 不是返回false
     */
    private boolean isSymbol(Character c) {
        //0x2E80~0x9FFF是东亚文字范围(包括中文)
        return !(CharUtils.isAsciiAlphanumeric(c) || (c > 0x2E80 && c < 0x9FFF));
    }
}
