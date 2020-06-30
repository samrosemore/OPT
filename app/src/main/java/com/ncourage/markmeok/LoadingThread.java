package com.ncourage.markmeok;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LoadingThread extends Thread
{
    private ImageView image;
    private FrameLayout frameLayout;
    private AnimationDrawable animation;
    private LinearLayout linearLayout;

    public LoadingThread(ImageView id, FrameLayout frameLayout, LinearLayout backgroundID)
    {
        this.frameLayout = frameLayout;
        this.image = id;
        this.linearLayout = backgroundID;
    }

    public void run()
    {
        linearLayout.setAlpha((float) 0.4);
        frameLayout.setVisibility(View.VISIBLE);
        animation = (AnimationDrawable) image.getDrawable();
        animation.start();




    }
}
