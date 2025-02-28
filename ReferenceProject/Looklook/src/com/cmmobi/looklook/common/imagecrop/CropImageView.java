package com.cmmobi.looklook.common.imagecrop;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.cmmobi.looklook.fragment.EditPhotoCropFragment;

public class CropImageView extends ImageViewTouchBase {
    public ArrayList<HighlightView> HighlightViews = new ArrayList<HighlightView>();
    HighlightView mMotionHighlightView = null;
    float mLastX, mLastY;
    int mMotionEdge;
    private static final String TAG = "CropImageView";

    public EditPhotoCropFragment mFragment = null;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mBitmapDisplayed.getBitmap() != null) {
            for (HighlightView hv : HighlightViews) {
                hv.mMatrix.set(getImageMatrix());
                hv.invalidate();
                if (hv.mIsFocused) {
                    centerBasedOnHighlightView(hv);
                }
            }
        }
        
        if (mFragment != null) {
        	mFragment.initImageView();
        }
    }
    
    @Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		Log.d(TAG,"CropImageView onSizeChanged in");
    	super.onSizeChanged(w, h, oldw, oldh);
		
	}
    
	public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void zoomTo(float scale, float centerX, float centerY) {
        super.zoomTo(scale, centerX, centerY);
        for (HighlightView hv : HighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomIn() {
        super.zoomIn();
        for (HighlightView hv : HighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomOut() {
        super.zoomOut();
        for (HighlightView hv : HighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        for (int i = 0; i < HighlightViews.size(); i++) {
            HighlightView hv = HighlightViews.get(i);
            hv.mMatrix.postTranslate(deltaX, deltaY);
            hv.invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            for (int i = 0; i < HighlightViews.size(); i++) {
                HighlightView hv = HighlightViews.get(i);
                int edge = hv.getHit(event.getX(), event.getY());
                if (edge != HighlightView.GROW_NONE) {
                    mMotionEdge = edge;
                    mMotionHighlightView = hv;
                    mLastX = event.getX();
                    mLastY = event.getY();
                    mMotionHighlightView
                            .setMode((edge == HighlightView.MOVE) ? HighlightView.ModifyMode.Move
                                    : HighlightView.ModifyMode.Grow);
                    break;
                }
            }
            break;
        case MotionEvent.ACTION_UP:
            if (mMotionHighlightView != null) {
                centerBasedOnHighlightView(mMotionHighlightView);
                mMotionHighlightView.setMode(HighlightView.ModifyMode.None);
            }
            mMotionHighlightView = null;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mMotionHighlightView != null) {
                mMotionHighlightView.handleMotion(mMotionEdge, event.getX()
                        - mLastX, event.getY() - mLastY);
                mLastX = event.getX();
                mLastY = event.getY();

                if (true) {
                    // This section of code is optional. It has some user
                    // benefit in that moving the crop rectangle against
                    // the edge of the screen causes scrolling but it means
                    // that the crop rectangle is no longer fixed under
                    // the user's finger.
                    ensureVisible(mMotionHighlightView);
                }
            }
            break;
        }

        switch (event.getAction()) {
        case MotionEvent.ACTION_UP:
            center(true, true);
            break;
        case MotionEvent.ACTION_MOVE:
            // if we're not zoomed then there's no point in even allowing
            // the user to move the image around. This call to center puts
            // it back to the normalized location (with false meaning don't
            // animate).
            if (getScale() == 1F) {
                center(true, true);
            }
            break;
        }

        return true;
    }

    // Pan the displayed image to make sure the cropping rectangle is visible.
    private void ensureVisible(HighlightView hv) {
        Rect r = hv.mDrawRect;

        int panDeltaX1 = Math.max(0, getLeft() - r.left);
        int panDeltaX2 = Math.min(0, getRight() - r.right);

        int panDeltaY1 = Math.max(0, getTop() - r.top);
        int panDeltaY2 = Math.min(0, getBottom() - r.bottom);

        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX, panDeltaY);
        }
    }

    // If the cropping rectangle's size changed significantly, change the
    // view's center and scale according to the cropping rectangle.
    private void centerBasedOnHighlightView(HighlightView hv) {
        Rect drawRect = hv.mDrawRect;

        float width = drawRect.width();
        float height = drawRect.height();

        float thisWidth = getWidth();
        float thisHeight = getHeight();

        float z1 = thisWidth / width * .6F;
        float z2 = thisHeight / height * .6F;

        float zoom = Math.min(z1, z2);
        zoom = zoom * this.getScale();
        zoom = Math.max(1F, zoom);

        if ((Math.abs(zoom - getScale()) / zoom) > .1) {
            float[] coordinates = new float[] { hv.mCropRect.centerX(),
                    hv.mCropRect.centerY() };
            getImageMatrix().mapPoints(coordinates);
            zoomTo(zoom, coordinates[0], coordinates[1], 300F);
        }

        ensureVisible(hv);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    	Log.d(TAG,"CropImageView onDraw in");
        super.onDraw(canvas);
        for (int i = 0; i < HighlightViews.size(); i++) {
            HighlightViews.get(i).draw(canvas);
        }
    }

    public void add(HighlightView hv) {
        HighlightViews.add(hv);
        invalidate();
    }
}
