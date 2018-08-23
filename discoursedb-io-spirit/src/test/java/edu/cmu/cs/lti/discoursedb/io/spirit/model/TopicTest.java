package edu.cmu.cs.lti.discoursedb.io.spirit.model;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import edu.cmu.cs.lti.discoursedb.io.spirit.converter.SpiritConverterApplication;
import edu.cmu.cs.lti.discoursedb.io.spirit.io.SpiritDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpiritConverterApplication.class, loader = AnnotationConfigContextLoader.class)
public class TopicTest {
    @Autowired
    SpiritDAO database;

    @Test
    public void testTopic() {
        for (Topic topic : database.getTopics()) {
            System.out.println("THE TOPIC IS " + topic);

            List<Comment> comments = topic.getComments();

            comments.sort((c1, c2) -> {
                return c1.getDate().compareTo(c2.getDate());
            });

            for (Comment comment : comments) {
                System.out.println("\t" + comment);
            }

            Comment parent = comments.get(0);

            for (int i = comments.size() - 1; i > 0; i--) {
                System.out.println("RELATION");
                System.out.println("\t parent - 0 " + parent);
                System.out.println("\t current - " + (i) + " " + comments.get(i));
                System.out.println("\t previous - " + (i - 1) + " " + comments.get(i - 1));
            }

        }
    }
}
