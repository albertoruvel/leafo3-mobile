package com.leafo3.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * Created by Alberto Rubalcaba on 4/12/2015.
 */
public class ImageUtil {

    private static Picasso PICASSO_INSTANCE;

    public static Picasso getPicassoInstance(Context context){
        if(PICASSO_INSTANCE == null)
            PICASSO_INSTANCE = Picasso.with(context);
        return PICASSO_INSTANCE;
    }

    public static void loadImagePicasso(ImageView imageView, Context context, String uri){
        try{
            RequestCreator creator = getPicassoInstance(context).load(Uri.parse(uri))
                    .config(Bitmap.Config.ARGB_8888)
                    .error(android.R.drawable.stat_notify_error);

                creator.fit();

            creator.into(imageView);
        }catch(Exception ex){

        }
    }

    private static final int EPS = 80;
    public static Bitmap transform(int progress, Bitmap bitmap){
        //get rad
        float rad = progress * ((float)Math.sqrt(2) / 100);
        int N = bitmap.getHeight();
        int M = bitmap.getWidth();
        Bitmap resultBitmap = Bitmap.createBitmap(M, N, Bitmap.Config.ARGB_8888);

        int[] pixel = new int[N * M];
        bitmap.getPixels(pixel, 0, M, 0, 0, M, N);

        int[] r = new int[N * M];
        int[] g = new int[N * M];
        int[] b = new int[N * M];

        for(int i = 0; i < (N * M); i ++){
            r[i] = (pixel[i] >> 16) & 0xff;
            g[i] = (pixel[i] >> 8) & 0xff;
            b[i] = pixel[i] & 0xff;
        }

        float[] hr = histAcum(r, (N * M));
        float[] hg = histAcum(g, (N * M));
        float[] hb = histAcum(b, (N * M));


        int[] sr = new int[N* M];
        int[] sg = new int[N* M];
        int[] sb = new int[N* M];

        for(int i = 0; i < (N * M); i ++){
            sr[i] = (int)hr[r[i]];
            sg[i] = (int)hg[g[i]];
            sb[i] = (int)hb[b[i]];
        }

        //get color components
        int firstPixel = bitmap.getPixel(0, 0);
        float x = (firstPixel >> 16) & 0xff; //red
        float y = (firstPixel >> 8) & 0xff; //green
        float z = firstPixel & 0xff; //blue
        //falseColor
        int damage = 0;
        int total = 0;
        double sum = 0;
        double sqrt = 0;


        for(int i = 0; i < (N * M); i ++){
            sum = ((double)(Math.pow(sr[i], 2))) + ((double)Math.pow(sg[i], 2)) + ((double)Math.pow(sb[i], 2));
            sqrt = Math.sqrt(sum);
            if(sqrt < rad){
                damage ++;
                resultBitmap.setPixel(M, N, Color.parseColor("blue"));
            }else{
                resultBitmap.setPixel(M, N, Color.parseColor("red"));
            }
            /**else if(Math.sqrt(((double)Math.pow(sr[i][j] - 1, 2)) + ((double)Math.pow(sg[i][j] - 1, 2)) + ((double)Math.pow(sb[i][j] - 1, 2))) < rad)
             s[i][j] = 1;
             else
             s[i][j] = 2;**/

            sum = (Math.pow((double)r[i] - x, 2)) + (Math.pow((double)g[i] - y, 2)) + (Math.pow((double)b[i] - z, 2));
            if(Math.sqrt(sum) >= EPS)
                //s[i][j] = 0;
                total ++;
        }

        double percentage = ((double)damage/ (double)total) * 100f;

        return resultBitmap;
    }

    private static float[] histAcum(int[] array, int n){
        float[] h = new float[256];
        float[] hr = new float[256];
        for(int i = 0; i <= 255; i ++){
            h[i] = 0f;
            hr[i] = 0f;
        }

        for(int i = 0; i < n; i ++){
            h[array[i]] ++;
        }
        float sum = 0;
        for(int i = 0; i <= 255; i ++)
            sum += h[i];
        for(int i = 0; i <= 255; i ++)
            h[i] /= sum;
        for(int i = 0; i <= 255; i ++)
            for(int j = 0; j<= i; j ++)
                hr[i] += h[j];

        return hr;
    }
}
