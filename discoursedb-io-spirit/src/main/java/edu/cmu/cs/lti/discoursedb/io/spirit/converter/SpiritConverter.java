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
package edu.cmu.cs.lti.discoursedb.io.spirit.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class SpiritConverter implements CommandLineRunner {
    private String dataSetName;
    private String discourseName;

    @Autowired
    private SpiritConverterService converterService;

    @Override
    public void run(String... args) throws Exception {
        Assert.isTrue(args.length == 2,
                "Usage: SpiritConverterApplication <DataSetName> <DiscourseName>");

        dataSetName   = args[0];
        discourseName = args[1];

        log.info("Starting Spirit conversion");

        converterService.map(dataSetName, discourseName);

        log.info("Spirit conversion completed");
    }
}
