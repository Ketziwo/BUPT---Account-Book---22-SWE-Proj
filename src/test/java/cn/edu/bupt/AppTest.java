package cn.edu.bupt;

import cn.edu.bupt.utils.DateUtils;
import cn.edu.bupt.model.*;

/*
 * maven自动生成的
 */
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * 用于单元测试的类
 */
public class AppTest {
    @Test
    public void shouldAnswerWithTrue() {
        System.out.println("Hello");

        Transaction y = new Transaction();
        Transaction x = new Transaction();
        x.addTag("wx");
        x.addTag("apple");
        y.addTag("wx");
        x.removeTag("wx");
        x.removeTag("wx");
        System.out.println(x.getTags().size());

        
    }
}
