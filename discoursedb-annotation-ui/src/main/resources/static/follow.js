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
/*
 * Follow links determined by an array of relationships (rel-values)
 * 
 * api: client object used to make REST calls
 * rootPath: root URI to start from 
 * relArray: array of relationships to navigate along 
 */ 
module.exports = function follow(api, rootPath, relArray) {
	var root = api({
		method: 'GET',
		path: rootPath
	});

	return relArray.reduce(function(root, arrayItem) {
		var rel = typeof arrayItem === 'string' ? arrayItem : arrayItem.rel;
		return traverseNext(root, rel, arrayItem);
	}, root);

	function traverseNext (root, rel, arrayItem) {
		return root.then(function (response) {
			if (hasEmbeddedRel(response.entity, rel)) {
				return response.entity._embedded[rel];
			}

			if(!response.entity._links) {
				return [];
			}

			if (typeof arrayItem === 'string') {
				return api({
					method: 'GET',
					path: response.entity._links[rel].href
				});
			} else {
				return api({
					method: 'GET',
					path: response.entity._links[rel].href,
					params: arrayItem.params
				});
			}
		});
	}

	function hasEmbeddedRel (entity, rel) {
		return entity._embedded && entity._embedded.hasOwnProperty(rel);
	}
};
