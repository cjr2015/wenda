package com.nowcoder.service;

import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

@Service
public class SensitiveService implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
    private static final String DEFAULT_REPLACEMENT = "***";
    private class  TrieNode {
        private boolean end = false;
        private Map<Character, TrieNode> subNodes = new HashMap<>();
        void addSubNode(Character key, TrieNode node) {
            subNodes.put(key, node);
        }

        TrieNode getSubNode(Character key) {
            return subNodes.get(key);
        }

        boolean isKeyWordEnd() {
            return end;
        }

        void setKeyWordEnd(boolean end) {
            this.end = end;
        }

        public int getSubNodeCount() {
            return subNodes.size();
        }
    }
        private TrieNode rootNode = new TrieNode();

        private void addWord(String lineTxt){
            TrieNode tempNode = rootNode;
            for(int i=0; i<lineTxt.length();i++){
                Character c = lineTxt.charAt(i);
                TrieNode node = tempNode.getSubNode(c);
                if(node == null){
                    node = new TrieNode();
                    tempNode.addSubNode(c,node);
                }
                tempNode=node;
                if(i == lineTxt.length()-1){
                    tempNode.setKeyWordEnd(true);
                }
            }
        }
        public void  afterPropertiesSet() throws Exception{
           rootNode = new TrieNode();
            try{

                InputStream is = Thread.currentThread().getContextClassLoader()
                        .getResourceAsStream("SensitiveWords.txt");
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String lintTxt;
                while ((lintTxt=bufferedReader.readLine())!=null){
                    lintTxt=lintTxt.trim();
                    addWord(lintTxt);
                }
                bufferedReader.close();
            }catch (Exception e){
                logger.error("添加敏感词失败"+e.getMessage());
            }

        }
        private boolean isSymbol(char c) {
            int ic = (int) c;
            // 0x2E80-0x9FFF 东亚文字范围
            return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
        }
        public String filter(String text){
            if(StringUtils.isBlank(text)){
                return text;
            }
            String replacement = DEFAULT_REPLACEMENT;
            TrieNode tempNode = rootNode;
            StringBuilder result = new StringBuilder();
            int begin=0;
            int position=0;
            while(position<text.length()){
                Character c = text.charAt(position);
                if (isSymbol(c)) {
                    if (tempNode == rootNode) {
                        result.append(c);
                        ++begin;
                    }
                    ++position;
                    continue;
                }
                tempNode = tempNode.getSubNode(c);
                if(tempNode==null){
                    result.append(text.charAt(begin));
                    position = begin + 1;
                    begin = position;
                    tempNode=rootNode;
                    continue;
                }else if (tempNode.isKeyWordEnd()) {
                    // 发现敏感词， 从begin到position的位置用replacement替换掉
                    result.append(replacement);
                    position = position + 1;
                    begin = position;
                    tempNode = rootNode;
                } else {
                    ++position;
                }
            }
            result.append(text.substring(begin));

            return result.toString();
        }
    public static void main(String[] argv) {
        SensitiveService s = new SensitiveService();
        s.addWord("色情");
        s.addWord("好色");
        System.out.print(s.filter("你⊙色 情"));
    }
}
