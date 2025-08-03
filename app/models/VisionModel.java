package models;

import entities.Photo;
import play.libs.F;

import java.util.List;

public interface VisionModel {
    F.Tuple<List<String>, List<String>> annotate(Photo photo);
}
