package com.sloydev.eathub;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

public class ImageUrl {


    private static final boolean ENABLED = true;
    private int mAlturaLista;
    private boolean mWifiHD;
    private boolean m3gHD;


    public enum TipoFoto {
        TIPO_SUPER_MINI,
        TIPO_MINIATURA,
        TIPO_GRANDE,
        TIPO_AVATAR,
    }

    private static final int MINIMO_LISTA = 200;

    /**
     * 1) Url foto
     * 2) Anchura px
     * 3) Altura px
     * 4) Calidad %
     */
    private static final String THUMBNAIL_URL_FORMAT = "/resize/%d/%d/%d/%s";

    private static ImageUrl mInstance;

    private NetworkInfo mNetworkInfo;
    private int mScreenWidth;
    private int mAvatarSize;
    private String mUrlFormat;

    public static ImageUrl getInstance(Activity context) {
        if (mInstance == null) {
            mInstance = new ImageUrl(context);
        }
        mInstance.updateNetworkInfo(context);
        mInstance.updatePreferences(context);
        return mInstance;
    }

    public static ImageUrl getSoftInstance(Activity context) {
        if (mInstance == null) {
            mInstance = getInstance(context);
        }
        return mInstance;
    }

    protected ImageUrl(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        Resources resources = activity.getResources();
        mAvatarSize = resources.getDimensionPixelSize(R.dimen.recipe_avatar_size);
        mAlturaLista = resources.getDimensionPixelSize(R.dimen.lista_recetas_altura);
        if (mAlturaLista < MINIMO_LISTA) {
            mAlturaLista = MINIMO_LISTA;
        }
        mUrlFormat = activity.getString(R.string.api_endpoint) + THUMBNAIL_URL_FORMAT;
    }

    /**
     * Devuelve una url para descargar una imagen, dependiendo de la resolución del teléfono y el estado de la conexión
     *
     * @param urlOriginal
     * @return
     */
    public String getFotoUrl(String urlOriginal, TipoFoto tipo) {
        if (!ENABLED) {
            return urlOriginal;
        } else {
            if (mNetworkInfo == null) {
                return null;
            }
            int calidad;
            int ancho;
            int alto = -1; // Valor negativo significa automático
            boolean isWifiActive = mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
            boolean isHD = (isWifiActive && mWifiHD) || (!isWifiActive && m3gHD);
            switch (tipo) {
                case TIPO_MINIATURA:
                    calidad = isHD ? 90 : 30;
                    ancho = isHD ? mScreenWidth / 2 : mScreenWidth / 4;
                    break;
                default:
                case TIPO_GRANDE:
                    calidad = isHD ? 95 : 40;
                    ancho = isHD ? mScreenWidth : mScreenWidth / 2;
                    break;
                case TIPO_SUPER_MINI:
                    calidad = isHD ? 90 : 30;
                    alto = ancho = mAlturaLista;
                    break;
                case TIPO_AVATAR:
                    calidad = isHD ? 90 : 30;
                    alto = ancho = mAvatarSize;
                    break;
            }
            return String.format(mUrlFormat, ancho, alto, calidad, urlOriginal);
        }
    }

    public void updateNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mNetworkInfo = cm.getActiveNetworkInfo();
    }

    private void updatePreferences(Activity context) {
        //TODO saca los valores de las preferencias
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        mWifiHD = Integer.valueOf(prefs.getString("pref_red_imagenes_wifi", "1")) > 0;
        m3gHD = Integer.valueOf(prefs.getString("pref_red_imagenes_3g", "0")) > 0;
    }
}
