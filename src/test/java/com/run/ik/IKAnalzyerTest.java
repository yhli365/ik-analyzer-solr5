package com.run.ik;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;

public class IKAnalzyerTest {

    @Test
    public void basic() {
        index2("中华人民共和国People's Republic of China 2020-10-01");
    }

    @Test
    public void httpPostData() {
        index2("提交数据:username=runner&password=pwd2020&userId=231888");
    }

    private void index2(String text) {
        index(false, text);
        index(true, text);
    }

    private void index(boolean useSmart, String text) {
        //构建IK分词器，使用smart分词模式
        Analyzer analyzer = new IKAnalyzer(useSmart);

        //获取Lucene的TokenStream对象
        TokenStream ts = null;
        try {
            ts = analyzer.tokenStream("myfield", new StringReader(text));

            //获取词元位置属性
            OffsetAttribute offset = ts.addAttribute(OffsetAttribute.class);
            //获取词元文本属性
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            //获取词元文本属性
            TypeAttribute type = ts.addAttribute(TypeAttribute.class);

            System.out.println("\n----------[Index: useSmart=" + useSmart + "]----------");
            System.out.println("[TEXT] " + text);

            //重置TokenStream（重置StringReader）
            ts.reset();
            //迭代获取分词结果
            while (ts.incrementToken()) {
//                System.out.println(offset.startOffset() + " - " + offset.endOffset() + " : " + term.toString() + " | " + type.type());

                System.out.println(String.format("%3d - %3d | %7s : %s",
                        offset.startOffset(),
                        offset.endOffset(),
                        type.type(),
                        term.toString()));
            }
            //关闭TokenStream（关闭StringReader）
            ts.end();   // Perform end-of-stream operations, e.g. set the final offset.

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //释放TokenStream的所有资源
            if (ts != null) {
                try {
                    ts.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
