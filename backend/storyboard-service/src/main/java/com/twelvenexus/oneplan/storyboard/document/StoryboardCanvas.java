package com.twelvenexus.oneplan.storyboard.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "storyboard_canvases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoryboardCanvas {
    @Id
    private String id;
    
    private String storyboardId;
    
    private CanvasSettings canvasSettings;
    
    private List<CanvasElement> elements;
    
    private List<CanvasConnection> connections;
    
    private Map<String, Object> metadata;
    
    private LocalDateTime lastModified;
    
    private String modifiedBy;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CanvasSettings {
        private Integer width;
        private Integer height;
        private Integer zoom;
        private String backgroundColor;
        private Boolean gridEnabled;
        private Integer gridSize;
        private Map<String, Object> customSettings;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CanvasElement {
        private String elementId;
        private String type;
        private Position position;
        private Dimensions dimensions;
        private ElementStyle style;
        private Map<String, Object> data;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Position {
        private Integer x;
        private Integer y;
        private Integer z;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Dimensions {
        private Integer width;
        private Integer height;
        private Integer rotation;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ElementStyle {
        private String backgroundColor;
        private String borderColor;
        private Integer borderWidth;
        private String borderStyle;
        private Integer borderRadius;
        private String fontFamily;
        private Integer fontSize;
        private String textColor;
        private Map<String, String> customStyles;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CanvasConnection {
        private String id;
        private String fromElementId;
        private String toElementId;
        private String connectionType;
        private ConnectionStyle style;
        private List<Point> path;
        private String label;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConnectionStyle {
        private String lineColor;
        private Integer lineWidth;
        private String lineStyle;
        private String arrowStyle;
        private Map<String, String> customStyles;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        private Integer x;
        private Integer y;
    }
}
