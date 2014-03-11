package ceab.movlab.tigre;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class MarkerDrawable extends Drawable {

    private final static int    IMAGE_PADDING           = 3;
    private final static int    ROUNDED_RECT_RADIUS    = 5;

    private final Bitmap    image;
    private final Paint     bgPaint;
    private final RectF     bgBounds;

    public MarkerDrawable(Context context, int color) {


        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        this.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_location_found, options); 
        

        // Background
        this.bgPaint = new Paint();
        bgPaint.setAntiAlias(true);
        bgPaint.setColor(color);
        float rectHeight  = IMAGE_PADDING * 2 + image.getWidth();
        float rectWidth   = IMAGE_PADDING * 2 + image.getHeight();
        //float rectWidth   = TEXT_PADDING * 2 + textHeight;  // Square (alternative)
        // Create the background - use negative start x/y coordinates to centre align the icon
        this.bgBounds = new RectF(rectWidth/-2, -rectHeight,rectWidth/2,0);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(bgBounds, ROUNDED_RECT_RADIUS, ROUNDED_RECT_RADIUS, bgPaint);
        canvas.drawBitmap(image, image.getWidth()/-2, -(image.getHeight() + IMAGE_PADDING), null);
    }

    @Override
    public void setAlpha(int alpha) {
        bgPaint.setAlpha(alpha);
       
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        bgPaint.setColorFilter(cf);
        
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}