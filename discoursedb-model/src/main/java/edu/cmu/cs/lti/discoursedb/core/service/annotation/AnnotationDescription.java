package edu.cmu.cs.lti.discoursedb.core.service.annotation;

import lombok.Data;
import java.util.ArrayList;

@Data
class AnnotationDescription {
  String annotation;
  ArrayList<String> features;
  ArrayList<Integer> offsets;
}