package in.cpp.picoimg;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

class BaseState extends Drawable.ConstantState
{
    int mWidth;
    int mHeight;
    int mScaleShift;
    int mOrientation;
    Bitmap mOutput;

    // animation dummies
    int mNumPlays;
    int mPlayFrame;
    long mNextFrameTime;

    protected BaseState()
    {
    }

    BaseState(InputStream inp, int targetWidth, int targetHeight, Context ctx, int resId, String assetName, File urlCache) throws IOException
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        InputStream activeStream = inp;
        int origWidth = 0;

        // find sample size
        if ((targetWidth > 0) || (targetHeight > 0))
        {
            inp.mark(1024);
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inp, null, opts);
            origWidth = mWidth = opts.outWidth;
            mHeight = opts.outHeight;
            scaleToTarget(targetWidth, targetHeight);
            try
            {
                inp.reset();
            }
            catch (IOException e)
            {
                // we can't reset the provided stream, so we should open another one for decoding
                if (resId != 0)
                    activeStream = ctx.getResources().openRawResource(resId);
                else if (!TextUtils.isEmpty(assetName))
                    activeStream = ctx.getResources().getAssets().open(assetName);
                else if (null != urlCache)
                    activeStream = new FileInputStream(urlCache);
            }
        }

        // decode the bitmap
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = 1 << mScaleShift;
        mOutput = BitmapFactory.decodeStream(activeStream, null, opts);
        mWidth = opts.outWidth;
        mHeight = opts.outHeight;

        // some androids fail to downsample interlaced gifs
        // detect it here to prevent further chaos
        if ((mScaleShift > 0) && (mWidth == origWidth))
            mScaleShift = 0;

        // if we've opened second stream, it's time to close it
        if (activeStream != inp)
        {
            try
            {
                activeStream.close();
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }

        // ok?
        if (null == mOutput)
            throw new IOException("Wrong image format");

        // try to read exif orientation
        if (urlCache != null)
        {
            try
            {
                ExifInterface exif = new ExifInterface(urlCache.getAbsolutePath());
                mOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                if ((mOrientation >= 5) && (mOrientation <= 8))
                {
                    int temp = mHeight;
                    mHeight = mWidth;
                    mWidth = temp;
                }
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void scaleToTarget(int targetWidth, int targetHeight)
    {
        mScaleShift = 0;
        while ((targetWidth < (mWidth / (1 << (mScaleShift + 1)))) && (targetHeight < (mHeight / (1 << (mScaleShift + 1)))))
            ++mScaleShift;
        mWidth >>= mScaleShift;
        mHeight >>= mScaleShift;
    }

    protected void step()
    {
    }

    boolean isAnimated()
    {
        return false;
    }

    @Override
    public Drawable newDrawable()
    {
        return new PicoDrawable(this);
    }

    @Override
    public int getChangingConfigurations()
    {
        return 0; // no dependency on system events
    }
}
