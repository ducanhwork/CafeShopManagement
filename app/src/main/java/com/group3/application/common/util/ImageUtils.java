package com.group3.application.common.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.group3.application.R;

/**
 * Utility class for loading images using Glide library
 * Handles ingredient images with placeholders and error states
 */
public class ImageUtils {
    
    // Default placeholder and error resource IDs
    private static final int DEFAULT_PLACEHOLDER = R.drawable.ic_ingredient_placeholder;
    private static final int DEFAULT_ERROR = R.drawable.ic_image_error;
    
    /**
     * Load ingredient image with default placeholder and error handling
     */
    public static void loadIngredientImage(Context context, String imageUrl, ImageView imageView) {
        loadIngredientImage(context, imageUrl, imageView, DEFAULT_PLACEHOLDER, DEFAULT_ERROR);
    }
    
    /**
     * Load ingredient image with custom placeholder and error images
     */
    public static void loadIngredientImage(Context context, String imageUrl, ImageView imageView,
                                          @DrawableRes int placeholder, @DrawableRes int error) {
        if (context == null || imageView == null) {
            return;
        }
        
        RequestOptions options = new RequestOptions()
                .placeholder(placeholder)
                .error(error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();
        
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }
    
    /**
     * Load ingredient image in circular shape (for thumbnails)
     */
    public static void loadIngredientImageCircular(Context context, String imageUrl, ImageView imageView) {
        if (context == null || imageView == null) {
            return;
        }
        
        RequestOptions options = new RequestOptions()
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_ERROR)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .circleCrop();
        
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }
    
    /**
     * Load ingredient image with custom drawable placeholder
     */
    public static void loadIngredientImage(Context context, String imageUrl, ImageView imageView,
                                          Drawable placeholder, Drawable error) {
        if (context == null || imageView == null) {
            return;
        }
        
        RequestOptions options = new RequestOptions()
                .placeholder(placeholder)
                .error(error)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();
        
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }
    
    /**
     * Load ingredient thumbnail (smaller size for list items)
     */
    public static void loadIngredientThumbnail(Context context, String imageUrl, ImageView imageView) {
        if (context == null || imageView == null) {
            return;
        }
        
        RequestOptions options = new RequestOptions()
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_ERROR)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .override(200, 200) // Smaller size for thumbnails
                .centerCrop();
        
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }
    
    /**
     * Load ingredient image for detail view (full size)
     */
    public static void loadIngredientImageFull(Context context, String imageUrl, ImageView imageView) {
        if (context == null || imageView == null) {
            return;
        }
        
        RequestOptions options = new RequestOptions()
                .placeholder(DEFAULT_PLACEHOLDER)
                .error(DEFAULT_ERROR)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter();
        
        Glide.with(context)
                .load(imageUrl)
                .apply(options)
                .into(imageView);
    }
    
    /**
     * Clear Glide memory cache (useful for logout/cleanup)
     */
    public static void clearCache(Context context) {
        if (context != null) {
            Glide.get(context).clearMemory();
        }
    }
    
    /**
     * Clear Glide disk cache (must be called on background thread)
     */
    public static void clearDiskCache(Context context) {
        if (context != null) {
            new Thread(() -> Glide.get(context).clearDiskCache()).start();
        }
    }
    
    /**
     * Preload ingredient image (for better performance)
     */
    public static void preloadIngredientImage(Context context, String imageUrl) {
        if (context != null && imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .preload();
        }
    }
}
