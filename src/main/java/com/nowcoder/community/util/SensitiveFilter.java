package com.nowcoder.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换敏感词的常量
    private static final String REPLACEMENT = "***";


    //根节点
    TrieNode root = new TrieNode();
    @PostConstruct
    public void init() {

        try(    //读sensitive-words.txt
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                //将字节流转为字符流
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                //BufferedReader可以一行一行读，为了提升效率将字符流转为缓冲字符流
                BufferedReader reader = new BufferedReader(inputStreamReader);
        ) {
            String sensitiveWord;
            while((sensitiveWord = reader.readLine())!=null) {
                //将读到的敏感词添加到前缀树
                this.addSensitiveWord(sensitiveWord);

            }
        } catch(IOException e) {
            logger.error("加载敏感词文件失败" + e.getMessage());
        }

    }
    //将一个敏感词添加到前缀树
    private void addSensitiveWord(String sensitiveWord) {
        TrieNode tempNode = root;
        for(int i = 0; i < sensitiveWord.length(); i++) {
            char c = sensitiveWord.charAt(i);
            TrieNode subNode = tempNode.getSubNode(c);
            if(subNode == null) {
                subNode = new TrieNode();
                tempNode.addSubNode(c,subNode);
            }
            tempNode = subNode;
            if(i == sensitiveWord.length() - 1) {
                tempNode.setSensitiveWordEnd(true);
            }
        }
    }
    /**
     * 过滤敏感词
     * @param text 带过滤的文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        //指针1
        TrieNode tempNode = root;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;
        //记录结果
        StringBuilder sb = new StringBuilder();
        while (begin < text.length()) {
            if (position < text.length()) {
                char c = text.charAt(position);
                //跳过符号 ——赌-博--
                if (isSymbol(c)) {
                    if (tempNode == root) {
                        begin++;
                        sb.append(c);
                    }
                    position++;
                    continue;
                }
                TrieNode subNode = tempNode.getSubNode(c);
                if (subNode == null) {
                    sb.append(text.substring(begin,position+1));
                    begin++;
                    position++;
                } else {
                    if (subNode.isSensitiveWordEnd) {
                        sb.append(REPLACEMENT);
                        begin = position + 1;
                        position = begin;
                        tempNode = root;
                    } else {
                        position++;
                        tempNode = subNode;
                    }

                }
            } else {
                begin++;
                position = begin;
                tempNode = root;
            }
        }
        return sb.toString();
    }

    //判断是否为符号
    //CharUtils.isAsciiAlphanumeric(c)是数字字母返回true
    //0x2E80~0x9FFF是东亚文字范围
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }
    //定义前缀树
    private class TrieNode {

        //标志是否是敏感词结尾
        public boolean isSensitiveWordEnd = false;
        //定义子节点
        public Map<Character, TrieNode> subNodes = new HashMap<>();

        public boolean isSensitiveWordEnd() {
            return isSensitiveWordEnd;
        }

        public void setSensitiveWordEnd(boolean sensitiveWordEnd) {
            isSensitiveWordEnd = sensitiveWordEnd;
        }
        //添加子节点
        public void addSubNode(Character c, TrieNode node) {
            subNodes.put(c,node);
        }
        //获取子节点
        public TrieNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }
}

