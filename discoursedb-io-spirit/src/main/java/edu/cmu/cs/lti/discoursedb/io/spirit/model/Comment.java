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
package edu.cmu.cs.lti.discoursedb.io.spirit.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(exclude="topic")

@NamedQueries({
    @NamedQuery(name = "comment.get", query = "SELECT comment from Comment comment"),
    @NamedQuery(name = "comment.getByTopicDate", query = "SELECT comment from Comment comment where topic_id=:topic_id order by date asc")
})

@Entity
@Table(name = "spirit_comment_comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Lob
    @Column(name = "comment")
    private String comment;

    @Lob
    @Column(name = "comment_html")
    private String commentHtml;

    @Column(name = "action")
    private int action;

    @Column(name = "date", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "is_removed")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isRemoved;

    @Column(name = "is_modified")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isModified;

    @Column(name = "ip_address", nullable = true)
    private String ipAddress;

    @Column(name = "modified_count")
    private int modifiedCount;

    @Column(name = "likes_count")
    private int likesCount;

    @Column(name = "topic_id")
    private int topicId;

    @Column(name = "user_id")
    private int userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "topic_id", insertable = false, updatable = false)
    private Topic topic;
}
