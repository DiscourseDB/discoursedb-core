package edu.cmu.cs.lti.discoursedb.io.csvimporter;

import lombok.Data;
import java.util.ArrayList;

@Data
class AnnotationDescription {
  String annotation;
  ArrayList<String> features;
  ArrayList<Integer> offsets;
}