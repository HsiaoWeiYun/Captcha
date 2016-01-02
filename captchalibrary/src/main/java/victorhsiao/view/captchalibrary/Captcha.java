package victorhsiao.view.captchalibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.io.CharArrayWriter;
import java.security.SecureRandom;

/**
 * Created by victor on 2015/12/31.
 */
public class Captcha extends ImageView {

    private Bitmap mBitMap = null;
    private final Paint mPaint = new Paint();
    private final SecureRandom mRandom = new SecureRandom(SecureRandom.getSeed(8));
    private final CharArrayWriter mCharArrayWriter = new CharArrayWriter();
    private final float SCALE = getContext().getResources().getDisplayMetrics().density;

    private boolean mDrawAgain = false;
    private boolean mDirty = true;

    /*   parameters   */
    private int mCaptchaNum = 4;
    private int mTextSize = (int) Math.ceil(24 * SCALE);  //24dp to pixel
    private int mDirtyTimes = 20;
    /*   parameters   */


    private String[] mCaptchaData = new String[]{
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
            "V", "W", "X", "Y", "Z",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
            "u", "v", "w", "x", "y", "z"
    };

    private final static int[] MULTIPLE = new int[]{1, -1};

    public Captcha(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttributes(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int iWidth = getMeasuredWidth();
        int iHeight = getMeasuredHeight();
        if (iWidth < 1 || iHeight < 1) return;

        if (mBitMap == null)
            mBitMap = Bitmap.createBitmap(iWidth, iHeight, Bitmap.Config.ARGB_8888);

        if (mDrawAgain) {
            if (mBitMap != null) {
                canvas.drawBitmap(mBitMap, 0, 0, mPaint);
                return;
            } else {
                throw new NullPointerException();
            }
        }

        drawTextToBitmap(mBitMap);
        canvas.drawBitmap(mBitMap, 0, 0, mPaint);
        mDrawAgain = true;
    }

    private void setAttributes(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Captcha);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int iAttr = typedArray.getIndex(i);
            //iAttr may be non-final
            if (iAttr == R.styleable.Captcha_captcha_num) {
                mCaptchaNum = typedArray.getInt(iAttr, mCaptchaNum);
            } else if (iAttr == R.styleable.Captcha_text_size) {
                mTextSize = typedArray.getInt(iAttr, mTextSize);
                mTextSize = (int) Math.ceil(mTextSize * SCALE);
            } else if (iAttr == R.styleable.Captcha_dirty_time) {
                mDirtyTimes = typedArray.getInt(iAttr, mDirtyTimes);
            } else if (iAttr == R.styleable.Captcha_dirty_background) {
                mDirty = typedArray.getBoolean(iAttr, mDirty);
            }
        }

        if (typedArray != null) typedArray.recycle();
    }

    private void drawTextToBitmap(Bitmap bitmap) {
        if (mBitMap == null) throw new NullPointerException();

        cleanCaptcha();  //clean the bitmap
        reset();
        generateRandomCaptcha();
        initPaint();
        Canvas canvas = new Canvas(bitmap);
        setDirtyBackground(canvas);
        Path path = new Path();

        /*   get height and width of captcha   */
        int iHeight = getMeasuredHeight();
        int iWidth = getMeasuredWidth();
        /*   get height and width of captcha   */

        /*   init point   */
        int iStartX = 0;
        int iStartY = iHeight / 2;
        /*   init point   */

        float[] fTextWidths = calTextWidths();//get Text widths
        int widthPerWord = (iWidth / mCaptchaNum);
        final char[] cTextData = mCharArrayWriter.toCharArray();

        for (int i = 0; i < fTextWidths.length; i++) {
            iStartX = widthPerWord * (i + 1);
            iStartX -= fTextWidths[i];
            path.moveTo(0, iStartY);
            mPaint.setFakeBoldText(mRandom.nextBoolean());
            mPaint.setARGB(255, mRandom.nextInt(150), mRandom.nextInt(150), mRandom.nextInt(150));
            int offset = mRandom.nextInt((int) fTextWidths[i]);
            path.lineTo(iWidth, iStartY + (offset * MULTIPLE[mRandom.nextInt(1)]));
            canvas.drawTextOnPath(String.valueOf(cTextData[i]), path, iStartX, 0, mPaint);
            path.reset();
        }
    }

    public boolean invalidTextWidth() {
        int iWidth = getMeasuredWidth();
        float fSumOfTextsWidth = 0;
        float fTextWidths[] = calTextWidths();
        for (float fTextWidth : fTextWidths) {
            fSumOfTextsWidth += fTextWidth;
        }
        return fSumOfTextsWidth > iWidth;
    }


    private float[] calTextWidths() {
        float fTextWidths[] = new float[mCharArrayWriter.size()];
        mPaint.getTextWidths(mCharArrayWriter.toString(), fTextWidths);
        return fTextWidths;
    }

    private void setDirtyBackground(Canvas canvas) {
        if (!mDirty) return;
        int iHeight = getMeasuredHeight();
        int iWidth = getMeasuredWidth();

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        for (int i = 0; i < mDirtyTimes; i++) {
            mPaint.setARGB(50, mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
            canvas.drawCircle(mRandom.nextInt(iWidth), mRandom.nextInt(iHeight), mRandom.nextInt(30), mPaint);
            canvas.drawLine(mRandom.nextInt(iWidth), mRandom.nextInt(iHeight), mRandom.nextInt(iWidth), mRandom.nextInt(iHeight), mPaint);
            canvas.drawRect(mRandom.nextInt(iWidth), mRandom.nextInt(iHeight), mRandom.nextInt(iWidth), mRandom.nextInt(iHeight), mPaint);
        }
    }

    private void generateRandomCaptcha() {
        for (int i = 0; i < mCaptchaNum; i++) {
            mCharArrayWriter.append(mCaptchaData[mRandom.nextInt(mCaptchaData.length)]);
        }
    }

    private void reset() {
        if (mCharArrayWriter == null || mPaint == null) throw new NullPointerException();
        mCharArrayWriter.reset();
        mPaint.reset();
    }

    private void initPaint() {
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }

    public void setTextSize(int iTextSize) {
        mTextSize = iTextSize;
    }

    public int getTextSize() {
        return mTextSize;
    }

    public String[] getCaptchaData() {
        return mCaptchaData;
    }

    public void setCaptchaData(String[] mCaptchaData) {
        this.mCaptchaData = mCaptchaData;
    }

    public void cleanCaptcha() {
        if (mBitMap == null) throw new NullPointerException();

        for (int i = 0; i < mBitMap.getHeight(); i++) {
            for (int j = 0; j < mBitMap.getWidth(); j++) {
                mBitMap.setPixel(j, i, Color.WHITE);
            }
        }
    }

    public boolean verify(String sText) {
        final String sCaptcha = mCharArrayWriter.toString();
        return sCaptcha.equals(sText);
    }

    public void release() {
        if (mBitMap != null) mBitMap.recycle();
    }

    public void refreshCaptcha() {
        mDrawAgain = false;
        invalidate();
    }
}
