package in.apparray.mylibrary.classes;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import in.apparray.mylibrary.R;


public class MaterialFancyLayout extends LinearLayout {

    public static final String TAG = MaterialFancyLayout.class.getSimpleName();

    // # Background Attributes
    private int mDefaultBackgroundColor = Color.BLACK;
    private int mFocusBackgroundColor = 0;
    private int mDisabledBackgroundColor = Color.parseColor("#f6f7f9");
    private int mDisabledTextColor = Color.parseColor("#bec2c9");
    private int mDisabledBorderColor = Color.parseColor("#dddfe2");

    private int mBorderColor = Color.TRANSPARENT;
    private int mBorderWidth = 0;

    private int mRadius = 0;
    private int mRadiusTopLeft = 0;
    private int mRadiusTopRight = 0;
    private int mRadiusBottomLeft = 0;
    private int mRadiusBottomRight = 0;

    private boolean mEnabled = true;

    /**
     * Tags to identify icon position
     */
    public static final int POSITION_LEFT = 1;
    public static final int POSITION_RIGHT = 2;
    public static final int POSITION_TOP = 3;
    public static final int POSITION_BOTTOM = 4;

    private boolean mGhost = false; // Default is a solid button !
    //private boolean mUseSystemFont = false; // Default is using robotoregular.ttf
    private boolean mUseRippleEffect = true;

    /**
     * Default constructor
     *
     * @param context : Context
     */
    public MaterialFancyLayout(Context context) {
        super(context);

        //mTextTypeFace = FontUtil.findFont(context, mDefaultTextFont, null);
        //mIconTypeFace = FontUtil.findFont(context, mDefaultIconFont, null);
        initializeMaterialFancyButton();
    }

    /**
     * Default constructor called from Layouts
     *
     * @param context : Context
     * @param attrs : Attributes Array
     */
    public MaterialFancyLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray attrsArray =
                context.obtainStyledAttributes(attrs, R.styleable.MaterialFancyBtnAttrs, 0, 0);
        initAttributesArray(attrsArray);
        attrsArray.recycle();

        initializeMaterialFancyButton();
    }

    /**
     * Initialize Button dependencies
     * - Initialize Button Container : The LinearLayout
     * - Initialize Button TextView
     * - Initialize Button Icon
     * - Initialize Button Font Icon
     */
    private void initializeMaterialFancyButton() {
        initializeButtonContainer();
        //this.removeAllViews();
        setupBackground();
    }

    /**
     * Initialize Attributes arrays
     *
     * @param attrsArray : Attributes array
     */
    private void initAttributesArray(TypedArray attrsArray) {

        mDefaultBackgroundColor =
                attrsArray.getColor(R.styleable.MaterialFancyBtnAttrs_mfb_defaultColor,
                        mDefaultBackgroundColor);
        mFocusBackgroundColor = attrsArray.getColor(R.styleable.MaterialFancyBtnAttrs_mfb_focusColor,
                mFocusBackgroundColor);
        mDisabledBackgroundColor =
                attrsArray.getColor(R.styleable.MaterialFancyBtnAttrs_mfb_disabledColor,
                        mDisabledBackgroundColor);

        mEnabled = attrsArray.getBoolean(R.styleable.MaterialFancyBtnAttrs_android_enabled, true);

        mDisabledTextColor =
                attrsArray.getColor(R.styleable.MaterialFancyBtnAttrs_mfb_disabledTextColor,
                        mDisabledTextColor);
        mDisabledBorderColor =
                attrsArray.getColor(R.styleable.MaterialFancyBtnAttrs_mfb_disabledBorderColor,
                        mDisabledBorderColor);

        mBorderColor =
                attrsArray.getColor(R.styleable.MaterialFancyBtnAttrs_mfb_borderColor, mBorderColor);
        mBorderWidth =
                (int) attrsArray.getDimension(R.styleable.MaterialFancyBtnAttrs_mfb_borderWidth,
                        mBorderWidth);

        // Handle radius for button.
        mRadius =
                (int) attrsArray.getDimension(R.styleable.MaterialFancyBtnAttrs_mfb_radius, mRadius);

        mRadiusTopLeft =
                (int) attrsArray.getDimension(R.styleable.MaterialFancyBtnAttrs_mfb_radiusTopLeft,
                        mRadius);
        mRadiusTopRight =
                (int) attrsArray.getDimension(R.styleable.MaterialFancyBtnAttrs_mfb_radiusTopRight,
                        mRadius);
        mRadiusBottomLeft =
                (int) attrsArray.getDimension(R.styleable.MaterialFancyBtnAttrs_mfb_radiusBottomLeft,
                        mRadius);
        mRadiusBottomRight =
                (int) attrsArray.getDimension(R.styleable.MaterialFancyBtnAttrs_mfb_radiusBottomRight,
                        mRadius);


        mGhost = attrsArray.getBoolean(R.styleable.MaterialFancyBtnAttrs_mfb_ghost, mGhost);
        //mUseSystemFont = attrsArray.getBoolean(R.styleable.MaterialFancyBtnAttrs_fb_useSystemFont,
        //    mUseSystemFont);

        String text = attrsArray.getString(R.styleable.MaterialFancyBtnAttrs_mfb_text);

        if (text == null) { //no mfb_text attribute
            text = attrsArray.getString(R.styleable.MaterialFancyBtnAttrs_android_text);
        }

        String fontIcon =
                attrsArray.getString(R.styleable.MaterialFancyBtnAttrs_mfb_fontIconResource);

        String iconFontFamily = attrsArray.getString(R.styleable.MaterialFancyBtnAttrs_mfb_iconFont);
        String textFontFamily = attrsArray.getString(R.styleable.MaterialFancyBtnAttrs_mfb_textFont);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Drawable getRippleDrawable(Drawable defaultDrawable, Drawable focusDrawable,
                                       Drawable disabledDrawable) {
        if (!mEnabled) {
            return disabledDrawable;
        } else {
            return new RippleDrawable(ColorStateList.valueOf(mFocusBackgroundColor), defaultDrawable,
                    focusDrawable);
        }
    }

    @SuppressLint("NewApi") private void setupBackground() {

        // Default Drawable
        GradientDrawable defaultDrawable = new GradientDrawable();
        defaultDrawable.setCornerRadius(mRadius);
        defaultDrawable.setCornerRadii(new float[] {
                mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight, mRadiusBottomRight,
                mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft
        });
        if (mGhost) {
            // Hollow Background
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
                defaultDrawable.setColor(getResources().getColor(android.R.color.transparent));
            } else {
                defaultDrawable.setColor(
                        getResources().getColor(android.R.color.transparent, getContext().getTheme()));
            }
        } else {
            defaultDrawable.setColor(mDefaultBackgroundColor);
        }

        //Focus Drawable
        GradientDrawable focusDrawable = new GradientDrawable();
        focusDrawable.setCornerRadius(mRadius);
        focusDrawable.setCornerRadii(new float[] {
                mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight, mRadiusBottomRight,
                mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft
        });
        focusDrawable.setColor(mFocusBackgroundColor);

        // Disabled Drawable
        GradientDrawable disabledDrawable = new GradientDrawable();
        disabledDrawable.setCornerRadius(mRadius);
        focusDrawable.setCornerRadii(new float[] {
                mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight, mRadiusBottomRight,
                mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft
        });
        disabledDrawable.setColor(mDisabledBackgroundColor);
        disabledDrawable.setStroke(mBorderWidth, mDisabledBorderColor);

        // Handle Border
        if (mBorderColor != 0) {
            defaultDrawable.setStroke(mBorderWidth, mBorderColor);
        }

        // Handle disabled border color
        if (!mEnabled) {
            defaultDrawable.setStroke(mBorderWidth, mDisabledBorderColor);
            if (mGhost) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    disabledDrawable.setColor(getResources().getColor(android.R.color.transparent));
                } else {
                    disabledDrawable.setColor(
                            getResources().getColor(android.R.color.transparent, getContext().getTheme()));
                }
            }
        }

        if (mUseRippleEffect && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setBackground(getRippleDrawable(defaultDrawable, focusDrawable, disabledDrawable));
        } else {

            StateListDrawable states = new StateListDrawable();

            // Focus/Pressed Drawable
            GradientDrawable drawable2 = new GradientDrawable();
            drawable2.setCornerRadius(mRadius);
            focusDrawable.setCornerRadii(new float[] {
                    mRadiusTopLeft, mRadiusTopLeft, mRadiusTopRight, mRadiusTopRight, mRadiusBottomRight,
                    mRadiusBottomRight, mRadiusBottomLeft, mRadiusBottomLeft
            });
            if (mGhost) {
                drawable2.setColor(getResources().getColor(android.R.color.transparent)); // No focus color
            } else {
                drawable2.setColor(mFocusBackgroundColor);
            }

            // Handle Button Border
            if (mBorderColor != 0) {
                if (mGhost) {
                    drawable2.setStroke(mBorderWidth,
                            mFocusBackgroundColor); // Border is the main part of button now
                } else {
                    drawable2.setStroke(mBorderWidth, mBorderColor);
                }
            }

            if (!mEnabled) {
                if (mGhost) {
                    drawable2.setStroke(mBorderWidth, mDisabledBorderColor);
                } else {
                    drawable2.setStroke(mBorderWidth, mDisabledBorderColor);
                }
            }

            if (mFocusBackgroundColor != 0) {
                states.addState(new int[] { android.R.attr.state_pressed }, drawable2);
                states.addState(new int[] { android.R.attr.state_focused }, drawable2);
                states.addState(new int[] { -android.R.attr.state_enabled }, disabledDrawable);
            }

            states.addState(new int[] {}, defaultDrawable);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                this.setBackgroundDrawable(states);
            } else {
                this.setBackground(states);
            }
        }
    }

    /**
     * Initialize button container
     */
    private void initializeButtonContainer() {

        if (this.getLayoutParams() == null) {
            LayoutParams containerParams =
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            this.setLayoutParams(containerParams);
        }

//        this.setGravity(Gravity.CENTER);
        this.setClickable(true);
        this.setFocusable(false);
        //this.setPadding(20, 20, 20, 20);
    }

    /**
     * Set Background color of the button
     *
     * @param color : use Color.parse('#code')
     */
    public void setBackgroundColor(int color) {
        this.mDefaultBackgroundColor = color;
        this.setupBackground();
    }

    /**
     * Set Focus color of the button
     *
     * @param color : use Color.parse('#code')
     */
    public void setFocusBackgroundColor(int color) {
        this.mFocusBackgroundColor = color;
        this.setupBackground();
    }

    /**
     * Set color of the button border
     *
     * @param color : Color
     * use Color.parse('#code')
     */
    @SuppressWarnings("unused") public void setBorderColor(int color) {
        this.mBorderColor = color;
        this.setupBackground();
    }

    /**
     * Set Width of the button
     *
     * @param width : Width
     */
    @SuppressWarnings("unused") public void setBorderWidth(int width) {
        this.mBorderWidth = width;
        this.setupBackground();
    }

    /**
     * Set Border Radius of the button
     *
     * @param radius : Radius
     */
    public void setRadius(int radius) {
        this.mRadius = radius;
        this.setupBackground();
    }

    public void setRadius(int radiusTopLeft, int radiusTopRight, int radiusBottomLeft, int radiusBottomRight) {
        this.mRadiusTopLeft = radiusTopLeft;
        this.mRadiusTopRight = radiusTopRight;
        this.mRadiusBottomLeft = radiusBottomLeft;
        this.mRadiusBottomRight = radiusBottomRight;
        this.setupBackground();
    }

    /**
     * Set border radius top left of the button
     * @param radiusTopLeft radius top left of the button
     */
    public void setRadiusTopLeft(int radiusTopLeft) {
        this.mRadiusTopLeft = radiusTopLeft;
        this.setupBackground();
    }

    /**
     * Set border radius top right of the button
     * @param radiusTopRight radius top right of the button
     */
    public void setRadiusTopRight(int radiusTopRight) {
        this.mRadiusTopRight = radiusTopRight;
        this.setupBackground();
    }

    /**
     * Set border radius bottom left of the button
     * @param radiusBottomLeft radius bottom left of the button
     */
    public void setRadiusBottomLeft(int radiusBottomLeft) {
        this.mRadiusBottomLeft = radiusBottomLeft;
        this.setupBackground();
    }

    /**
     * Set border radius bottom right of the button
     * @param radiusBottomRight radius bottom right of the button
     */
    public void setRadiusBottomRight(int radiusBottomRight) {
        this.mRadiusBottomRight = radiusBottomRight;
        this.setupBackground();
    }

    /**
     * Override setEnabled and rebuild the fancybutton view
     * To redraw the button according to the state : enabled or disabled
     */
    @Override public void setEnabled(boolean value) {
        super.setEnabled(value);
        this.mEnabled = value;
        initializeMaterialFancyButton();
    }

    /**
     * Setting the button to have hollow or solid shape
     */
    @SuppressWarnings("unused") public void setGhost(boolean ghost) {
        this.mGhost = ghost;
        this.setupBackground();
    }

}
