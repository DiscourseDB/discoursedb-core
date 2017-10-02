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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data

@NamedQueries({
    @NamedQuery(name = "user.get", query = "SELECT user from UserProfile user"),
    @NamedQuery(name = "user.getByUserId", query = "SELECT user from UserProfile user where user_id=:user_id")
})

@Entity
@Table(name = "spirit_user_userprofile")
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "slug", length = 50)
    private String slug;

    @Column(name = "location", length = 75)
    private String location;

    @Column(name = "last_seen", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSeen;

    @Column(name = "last_ip", nullable = true)
    private String lastIp;

    @Column(name = "timezone", length = 32)
    private String timezone;

    @Column(name = "is_administrator")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean is_administrator;

    @Column(name = "is_moderator")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean is_moderator;

    @Column(name = "is_verified")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isVerified;

    @Column(name = "topic_count")
    private int topicCount;

    @Column(name = "comment_count")
    private int commentCount;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "last_post_hash", length = 75)
    private String lastPostHash;

    @Column(name = "last_post_on", columnDefinition = "DATETIME", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastPostOn;
}
