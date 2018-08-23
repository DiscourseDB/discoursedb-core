/*******************************************************************************
 * Copyright (C)  2015 - 2017  Carnegie Mellon University
 * Author:
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation
either version 2 of the License
or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not
see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation
Inc.
51 Franklin Street

 * Fifth Floor
Boston
MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.io.spirit.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.ForeignKey;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data

@NamedQueries({ @NamedQuery(name = "topic.get", query = "SELECT topic from Topic topic") })

@Entity
@Table(name = "spirit_topic_topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "slug", length = 50)
    private String slug;

    @Column(name = "date", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @Column(name = "last_active", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastActive;

    @Column(name = "is_pinned")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isPinned;

    @Column(name = "is_globally_pinned")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isGloballyPinned;

    @Column(name = "is_closed")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isClosed;

    @Column(name = "is_removed")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isRemoved;

    @Column(name = "view_count")
    private int view_count;

    @Column(name = "comment_count")
    private int commentCount;

    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "reindex_at", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reindexAt;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", insertable = false, updatable = false, referencedColumnName="user_id")
    private UserProfile user;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "topic")
    private List<Comment> comments = new ArrayList<>();
}
