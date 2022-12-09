package lazy;

import com.google.gson.annotations.Expose;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.Arrays;

import static lazy.State.cell;
import static processing.core.PApplet.*;
import static lazy.ThemeColorType.*;

class Window implements UserInputSubscriber {
    final FolderNode folder;
    @Expose
    float posX;
    @Expose
    float posY;
    @Expose
    boolean closed = false;
    @Expose
    float windowSizeX; // can be resized by user
    float windowSizeY; // set every frame, locked to (cell size * child count) + 1 cell for the title bar
    protected boolean isCloseable;
    boolean isBeingDraggedAround;
    boolean isBeingResized;
    private boolean isTitleHighlighted;
    private boolean closeButtonPressInProgress;

    Window(FolderNode folder, boolean isCloseable, float posX, float posY, Float nullableSizeX) {
        this.posX = posX;
        this.posY = posY;
        this.isCloseable = isCloseable;
        UserInputPublisher.subscribe(this);
        this.folder = folder;
        folder.window = this;
        if(nullableSizeX == null){
            windowSizeX = cell * folder.idealWindowWidthInCells;
        }else{
            windowSizeX = nullableSizeX;
        }
    }

    void drawWindow(PGraphics pg) {
        pg.textFont(State.font);
        isTitleHighlighted = !closed && (isPointInsideTitleBar(State.app.mouseX, State.app.mouseY) && isBeingDraggedAround) || folder.isMouseOverNode;
        if (closed) {
            return;
        }
        constrainPosition(pg);
        pg.pushMatrix();
        drawBackgroundWithWindowBorder(pg, true);
        drawPathTooltipOnHighlight(pg);
        drawContent(pg);
        drawBackgroundWithWindowBorder(pg, false);
        drawTitleBar(pg, isTitleHighlighted);
        if (isCloseable) {
            drawCloseButton(pg);
        }
        drawResizeIndicator(pg);
        pg.popMatrix();
    }

    private void drawResizeIndicator(PGraphics pg) {
        if(!isPointInsideResizeBorder(State.app.mouseX, State.app.mouseY) || !State.getShouldDrawResizeIndicator()){
            return;
        }
        pg.pushMatrix();
        pg.translate(posX, posY);
        pg.strokeWeight(1);
        pg.strokeCap(SQUARE);
        pg.stroke(ThemeStore.getColor(FOCUS_FOREGROUND));
        pg.line(windowSizeX, 0, windowSizeX, windowSizeY);
        pg.popMatrix();
    }

    private void drawPathTooltipOnHighlight(PGraphics pg) {
        if (!isPointInsideTitleBar(State.app.mouseX, State.app.mouseY) || !WindowManager.showPathTooltips) {
            return;
        }
        pg.pushMatrix();
        pg.pushStyle();
        pg.translate(posX, posY);
        String[] pathSplit = Utils.splitFullPathWithoutEndAndRoot(folder.path);
        int lineCount = pathSplit.length;
        float tooltipHeight = lineCount * cell;
        float tooltipYOffset = 0;
        float tooltipXOffset = cell * 0.5f;
        float tooltipWidth = windowSizeX - tooltipXOffset - cell;
//        pg.stroke(ThemeStore.getColor(WINDOW_BORDER)); // tooltip border maybe?
        pg.noStroke();
        pg.fill(ThemeStore.getColor(NORMAL_BACKGROUND));
        pg.rect(tooltipXOffset, tooltipYOffset - tooltipHeight, tooltipWidth, tooltipHeight);
        pg.fill(ThemeStore.getColor(NORMAL_FOREGROUND));
        pg.textAlign(LEFT, CENTER);
        for (int i = 0; i < lineCount; i++) {
            pg.text(pathSplit[lineCount - 1 - i], State.textMarginX + tooltipXOffset, tooltipYOffset - i * cell - State.textMarginY);
        }
        pg.popMatrix();
        pg.popStyle();
    }

    protected void drawBackgroundWithWindowBorder(PGraphics pg, boolean drawBackgroundOnly) {
        pg.pushMatrix();
        pg.translate(posX, posY);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.strokeWeight(1);
        pg.fill(ThemeStore.getColor(NORMAL_BACKGROUND));
        if (drawBackgroundOnly) {
            pg.noStroke();
        } else {
            pg.noFill();
        }
        pg.rect(0, 0, windowSizeX, windowSizeY);
        pg.popMatrix();
    }

    private void drawCloseButton(PGraphics pg) {
        pg.pushMatrix();
        pg.translate(posX, posY);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.strokeWeight(1);
        pg.line(windowSizeX - cell, 0, windowSizeX - cell, cell - 1);
        if (isPointInsideCloseButton(State.app.mouseX, State.app.mouseY) || closeButtonPressInProgress) {
            pg.fill(ThemeStore.getColor(FOCUS_BACKGROUND));
            pg.noStroke();
            pg.rectMode(CORNER);
            pg.rect(windowSizeX - cell + 0.5f, 1, cell - 1, cell - 1);
            pg.stroke(ThemeStore.getColor(FOCUS_FOREGROUND));
            pg.strokeWeight(1.99f);
            pg.pushMatrix();
            pg.translate(windowSizeX - cell * 0.5f + 0.5f, cell * 0.5f);
            float n = cell * 0.2f;
            pg.line(-n, -n, n, n);
            pg.line(-n, n, n, -n);
            pg.popMatrix();
        }
        pg.popMatrix();
    }

    protected void drawContent(PGraphics pg) {
        drawInlineFolderChildren(pg);
    }

    protected void drawTitleBar(PGraphics pg, boolean highlight) {
        pg.pushMatrix();
        pg.pushStyle();
        pg.translate(posX, posY);
        pg.fill(highlight ? ThemeStore.getColor(FOCUS_BACKGROUND) : ThemeStore.getColor(NORMAL_BACKGROUND));
        float titleBarWidth = windowSizeX;
        pg.strokeWeight(1);
        pg.stroke(ThemeStore.getColor(WINDOW_BORDER));
        pg.rect(0, 0, titleBarWidth, cell);
        pg.fill(highlight ? ThemeStore.getColor(FOCUS_FOREGROUND) : ThemeStore.getColor(NORMAL_FOREGROUND));
        pg.textAlign(LEFT, CENTER);
        String trimmedName = Utils.getSubstringFromStartToFit(pg, folder.name, windowSizeX - cell - State.textMarginX);
        pg.text(trimmedName, State.textMarginX, cell - State.textMarginY);
        pg.popStyle();
        pg.popMatrix();
    }

    void drawContextLineFromTitleBarToInlineNode(PGraphics pg, float endRectSize, boolean pickShortestLine) {
        AbstractNode firstOpenParent = NodeTree.findFirstOpenParentNodeRecursively(folder);
        if (firstOpenParent == null || !firstOpenParent.isParentWindowVisible()) {
            return;
        }
        float xOffset = cell / 2f;
        float y0 = posY + cell / 2f;
        float y1 = firstOpenParent.pos.y + firstOpenParent.size.y / 2f;
        float x0a = posX - xOffset;
        float x0b = posX + windowSizeX + xOffset;
        float x1a = firstOpenParent.pos.x - xOffset;
        float x1b = firstOpenParent.pos.x + firstOpenParent.size.x + xOffset;
        float x0 = x0a;
        float x1 = x1b;
        if (pickShortestLine) {
            class PointDist {
                final float x0, x1, d;

                public PointDist(float x0, float x1, float d) {
                    this.x0 = x0;
                    this.x1 = x1;
                    this.d = d;
                }
            }
            PointDist[] pointsWithDistances = new PointDist[]{
                    new PointDist(x0a, x1a, dist(x0a, y0, x1a, y1)),
                    new PointDist(x0a, x1b, dist(x0a, y0, x1b, y1)),
                    new PointDist(x0b, x1b, dist(x0b, y0, x1b, y1)),
                    new PointDist(x0b, x1a, dist(x0b, y0, x1a, y1)),
            };
            Arrays.sort(pointsWithDistances, (p1, p2) -> Float.compare(p1.d, p2.d));
            x0 = pointsWithDistances[0].x0;
            x1 = pointsWithDistances[0].x1;
        }
        pg.line(x0, y0, x1, y1);
        pg.rectMode(CENTER);
        pg.rect(x0, y0, endRectSize, endRectSize);
        pg.rect(x1, y1, endRectSize, endRectSize);
    }

    private void constrainPosition(PGraphics pg) {
        if (!State.getShouldKeepWindowsInBounds()) {
            return;
        }
        float rightEdge = pg.width - windowSizeX - 1;
        float bottomEdge = pg.height - windowSizeY - 1;
        float lerpAmt = 0.3f;
        if (posX < 0) {
            posX = lerp(posX, 0, lerpAmt);
        }
        if (posY < 0) {
            posY = lerp(posY, 0, lerpAmt);
        }
        if (posX > rightEdge) {
            posX = lerp(posX, rightEdge, lerpAmt);
        }
        if (posY > bottomEdge) {
            posY = lerp(posY, bottomEdge, lerpAmt);
        }
    }

    void drawInlineFolderChildren(PGraphics pg) {
        windowSizeY = cell + heightSumOfChildNodes();
        pg.pushMatrix();
        pg.translate(posX, posY);
        pg.translate(0, cell);
        float y = cell;
        for (int i = 0; i < folder.children.size(); i++) {
            AbstractNode node = folder.children.get(i);
            float nodeHeight = cell * node.idealInlineNodeHeightInCells;
            node.updateInlineNodeCoordinates(posX, posY + y, windowSizeX, nodeHeight);
            pg.pushMatrix();
            pg.pushStyle();
            node.updateDrawInlineNode(pg);
            pg.popStyle();
            pg.popMatrix();
            y += nodeHeight;
            pg.translate(0, nodeHeight);
        }
        pg.popMatrix();
    }

    private float heightSumOfChildNodes() {
        float sum = 0;
        for (AbstractNode child : folder.children) {
            sum += child.idealInlineNodeHeightInCells * cell;
        }
        return sum;
    }


    @Override
    public void mouseWheelMoved(LazyMouseEvent e) {
        if (isPointInsideTitleBar(e.getX(), e.getY())) {
            return;
        }
        if (isPointInsideContent(e.getX(), e.getY())) {
            AbstractNode clickedNode = tryFindChildNodeAt(e.getX(), e.getY());
            if (clickedNode != null && clickedNode.isParentWindowVisible()) {
                clickedNode.mouseWheelMovedOverNode(e.getX(), e.getY(), e.getRotation());
            }
        }
    }

    @Override
    public void keyPressed(LazyKeyEvent keyEvent) {
        float x = State.app.mouseX;
        float y = State.app.mouseY;
        AbstractNode nodeUnderMouse = tryFindChildNodeAt(x, y);
        if (nodeUnderMouse != null && nodeUnderMouse.isParentWindowVisible()) {
            if (isPointInsideContent(x, y)) {
                nodeUnderMouse.keyPressedOverNode(keyEvent, x, y);
                keyEvent.consume();
            }
        }
    }

    private AbstractNode tryFindChildNodeAt(float x, float y) {
        for (AbstractNode node : folder.children) {
            if (Utils.isPointInRect(x, y, node.pos.x, node.pos.y, node.size.x, node.size.y)) {
                return node;
            }
        }
        return null;
    }


    @Override
    public void mousePressed(LazyMouseEvent e) {
        if (isClosed()) {
            return;
        }
        if (isPointInsideWindow(e.getX(), e.getY())) {
            if (!isFocused()) {
                setFocusOnThis();
            }
            e.setConsumed(true);
        }
        if (isPointInsideTitleBar(e.getX(), e.getY())) {
            isBeingDraggedAround = true;
            e.setConsumed(true);
            setFocusOnThis();
            return;
        } else if (isPointInsideCloseButton(e.getX(), e.getY())) {
            closeButtonPressInProgress = true;
            e.setConsumed(true);
        }
        if(isPointInsideResizeBorder(e.getX(), e.getY()) && State.getWindowResizeEnabled()){
            isBeingResized = true;
            e.setConsumed(true);
        }else if (isPointInsideContent(e.getX(), e.getY())) {
            AbstractNode node = tryFindChildNodeAt(e.getX(), e.getY());
            if (node != null && node.isParentWindowVisible()) {
                node.mousePressedOverNode(e.getX(), e.getY());
            }
        }
    }

    @Override
    public void mouseMoved(LazyMouseEvent e) {
        if (!closed && isPointInsideTitleBar(e.getX(), e.getY())) {
            e.setConsumed(true);
            folder.setIsMouseOverThisNodeOnly();
        }else if (isPointInsideContent(e.getX(), e.getY()) && !isPointInsideResizeBorder(e.getX(), e.getY())) {
            AbstractNode node = tryFindChildNodeAt(e.getX(), e.getY());
            if (node != null && node.isParentWindowVisible()) {
                node.setIsMouseOverThisNodeOnly();
                e.setConsumed(true);
            }
        } else {
            NodeTree.setAllNodesMouseOverToFalse();
        }
    }

    @Override
    public void mouseDragged(LazyMouseEvent e) {
        if (isClosed()) {
            return;
        }
        if (isBeingDraggedAround) {
            posX += e.getX() - e.getPrevX();
            posY += e.getY() - e.getPrevY();
            e.setConsumed(true);
        } else if (isBeingResized) {
            float minimumWindowSizeInCells = 4;
            float maximumWindowSize = State.app.width;
            windowSizeX += e.getX() - e.getPrevX();
            windowSizeX = constrain(windowSizeX, minimumWindowSizeInCells * cell, maximumWindowSize);
        }
        for (AbstractNode child : folder.children) {
            if (child.isDragged && child.isParentWindowVisible()) {
                child.mouseDragNodeContinue(e);
            }
        }
    }

    @Override
    public void mouseReleased(LazyMouseEvent e) {
        if (isClosed()) {
            return;
        }
        if (isCloseable && closeButtonPressInProgress && isPointInsideCloseButton(e.getX(), e.getY())) {
            close();
            e.setConsumed(true);
        } else if (isBeingDraggedAround) {
            trySnapToGrid();
            e.setConsumed(true);
        } else if(isBeingResized && UtilGridSnap.snapToGridEnabled){
            windowSizeX = UtilGridSnap.trySnapToGrid(windowSizeX, 0).x;
            e.setConsumed(true);
        }
        closeButtonPressInProgress = false;
        isBeingDraggedAround = false;
        isBeingResized = false;

        for (AbstractNode node : folder.children) {
            node.mouseReleasedAnywhere(e);
        }
        if (isPointInsideContent(e.getX(), e.getY())) {
            AbstractNode clickedNode = tryFindChildNodeAt(e.getX(), e.getY());
            if (clickedNode != null && clickedNode.isParentWindowVisible()) {
                clickedNode.mouseReleasedOverNode(e.getX(), e.getY());
                e.setConsumed(true);
            }
        }
    }

    private void trySnapToGrid() {
        PVector snappedPos = UtilGridSnap.trySnapToGrid(posX, posY);
        posX = snappedPos.x;
        posY = snappedPos.y;
    }

    void close() {
        closed = true;
        isBeingDraggedAround = false;
    }

    void open(boolean startDragging) {
        closed = false;
        if (startDragging) {
            isBeingDraggedAround = true;
            setFocusOnThis();
        }
    }

    private boolean isClosed() {
        return closed || LazyGui.isGuiHidden;
    }

    boolean isFocused() {
        return WindowManager.isFocused(this);
    }

    void setFocusOnThis() {
        WindowManager.setFocus(this);
        UserInputPublisher.setFocus(this);
    }

    boolean isPointInsideContent(float x, float y) {
        return Utils.isPointInRect(x, y,
                posX, posY + cell,
                windowSizeX, windowSizeY - cell);
    }

    boolean isPointInsideWindow(float x, float y) {
        return Utils.isPointInRect(x, y, posX, posY, windowSizeX, windowSizeY);
    }

    boolean isPointInsideTitleBar(float x, float y) {
        if (isCloseable) {
            return Utils.isPointInRect(x, y, posX, posY, windowSizeX - cell, cell);
        }
        return Utils.isPointInRect(x, y, posX, posY, windowSizeX, cell);
    }

    protected boolean isPointInsideCloseButton(float x, float y) {
        return Utils.isPointInRect(x, y,
                posX + windowSizeX - cell - 1, posY,
                cell + 1, cell - 1);
    }

    boolean isPointInsideResizeBorder(float x, float y) {
        if(!State.getWindowResizeEnabled()){
            return false;
        }
        float resizeBorderSize = 6;
        return Utils.isPointInRect(x, y, posX + windowSizeX - resizeBorderSize/2f, posY, resizeBorderSize, posY + windowSizeY);
    }

    public boolean isTitleHighlighted() {
        return isTitleHighlighted;
    }
}
