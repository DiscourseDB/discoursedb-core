/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
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
define(function() {
	'use strict';

	/* Convert a single or array of resources into "URI1\nURI2\nURI3..." */
	return {
		read: function(str /*, opts */) {
			return str.split('\n');
		},
		write: function(obj /*, opts */) {
			// If this is an Array, extract the self URI and then join using a newline
			if (obj instanceof Array) {
				return obj.map(function(resource) {
					return resource._links.self.href;
				}).join('\n');
			} else { // otherwise, just return the self URI
				return obj._links.self.href;
			}
		}
	};

});
