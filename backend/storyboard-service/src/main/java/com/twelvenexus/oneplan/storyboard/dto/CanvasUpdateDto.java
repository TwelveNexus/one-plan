package com.twelvenexus.oneplan.storyboard.dto;

import com.twelvenexus.oneplan.storyboard.document.StoryboardCanvas;
import lombok.Data;
import java.util.List;

@Data
public class CanvasUpdateDto {
    private StoryboardCanvas.CanvasSettings canvasSettings;
    private List<StoryboardCanvas.CanvasElement> elements;
    private List<StoryboardCanvas.CanvasConnection> connections;
    private String modifiedBy;
}
