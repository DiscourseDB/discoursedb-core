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

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.google.common.collect.ImmutableMap;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Configuration
public class DatabaseConfiguration {
    @Data
    @RequiredArgsConstructor
    public class SpiritEntityManagerFactory {
        @NonNull private EntityManagerFactory entityManagerFactory;
    }

    @Bean(name="spritEntityManagerFactory")
    public SpiritEntityManagerFactory entityManagerFactory(Environment env) {
        ImmutableMap<String , String> properties = ImmutableMap.of(
                "javax.persistence.jdbc.url",      env.getRequiredProperty("spirit.jdbc.url"),
                "javax.persistence.jdbc.user",     env.getRequiredProperty("spirit.jdbc.user"),
                "javax.persistence.jdbc.password", env.getRequiredProperty("spirit.jdbc.password"));

        return new SpiritEntityManagerFactory(Persistence.createEntityManagerFactory("SpiritPersistenceUnit", properties));
    }
}
