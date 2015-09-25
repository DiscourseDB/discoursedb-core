package edu.cmu.cs.lti.discoursedb.core.service.macro;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartType;
import edu.cmu.cs.lti.discoursedb.core.model.macro.QDiscoursePart;

public final class DiscoursePartPredicates {

	private DiscoursePartPredicates() {
	}

	public static BooleanExpression discoursePartHasDiscourse(Discourse discourse) {
		if (discourse == null) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.discourseToDiscourseParts.any().discourse.eq(discourse);
		}
	}

	public static BooleanExpression discourseHasName(String name) {
		if (name == null||name.isEmpty()) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.name.eq(name);
		}
	}

	public static BooleanExpression discourseHasType(DiscoursePartType type) {
		if (type == null) {
			return QDiscoursePart.discoursePart.isNull();
		} else {
			return QDiscoursePart.discoursePart.type.eq(type);
		}
	}

}
