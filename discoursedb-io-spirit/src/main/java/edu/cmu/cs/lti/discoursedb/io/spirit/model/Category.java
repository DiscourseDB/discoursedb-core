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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data

@NamedQueries({
    @NamedQuery(name = "category.get", query = "SELECT category from Category category") 
})

@Entity
@Table(name = "spirit_category_category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title", length = 75)
    private String title;

    @Column(name = "slug", length = 50)
    private String slug;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_closed")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isClosed;

    @Column(name = "is_removed")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isRemoved;

    @Column(name = "is_private")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isPrivate;

    @Column(name = "parent_id", nullable = true)
    private Integer parentId;

    @Column(name = "is_global")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean isGlobal;

    @Column(name = "color", length = 7)
    private String color;

    @Column(name = "reindex_at", columnDefinition = "DATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date reindexAt;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Category parentCategory;
}
