/*******************************************************************************
 * Copyright (C)  2015 - 2017  Carnegie Mellon University
 * Author:
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.io.spirit.io;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import edu.cmu.cs.lti.discoursedb.io.spirit.io.DatabaseConfiguration.SpiritEntityManagerFactory;
import edu.cmu.cs.lti.discoursedb.io.spirit.model.Category;
import edu.cmu.cs.lti.discoursedb.io.spirit.model.Comment;
import edu.cmu.cs.lti.discoursedb.io.spirit.model.Topic;
import edu.cmu.cs.lti.discoursedb.io.spirit.model.UserProfile;

@Component
public class SpiritDAO {
    @Autowired
    @Qualifier("spritEntityManagerFactory")
    private SpiritEntityManagerFactory spritEntityManagerFactory;

    private EntityManagerFactory emf;

    @PostConstruct
    public void postConstruct() {
        emf = spritEntityManagerFactory.getEntityManagerFactory();
    }

    @SuppressWarnings("unchecked")
    public List<Category> getCategories() {
        EntityManager em = emf.createEntityManager();

        try {
            return (List<Category>) em.createNamedQuery("category.get").getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Comment> getComments() {
        EntityManager em = emf.createEntityManager();

        try {
            return (List<Comment>) em.createNamedQuery("comment.get").getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Topic> getTopics() {
        EntityManager em = emf.createEntityManager();

        try {
            return (List<Topic>) em.createNamedQuery("topic.get").getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<UserProfile> getUsers() {
        EntityManager em = emf.createEntityManager();

        try {
            return (List<UserProfile>) em.createNamedQuery("user.get").getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public List<Comment> getCommentsForTopicByDate(Integer topicId) {
        EntityManager em = emf.createEntityManager();

        try {
            return (List<Comment>) em.createNamedQuery("comment.getByTopicDate")
                                     .setParameter("topic_id", topicId)
                                     .getResultList();
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    public UserProfile getUserByUserId(Integer userId) {
        EntityManager em = emf.createEntityManager();

        try {
            return (UserProfile) em.createNamedQuery("user.getByUserId")
                                     .setParameter("user_id", userId)
                                     .getSingleResult();
        } catch (Exception e) {
            throw e;
        } finally {
            em.close();
        }
    }
}
