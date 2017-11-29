package com.example.etudiant.interfacesaisie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by etudiant on 04/10/17.
 */

public class DrawingView extends View{
    private static final float TOUCH_TOLERANCE = 4;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;
    private Paint mPaint;
    private boolean mDrawMode;
    private float mX, mY;
    private float mEraserSize = 10;
	private int curw;
	private int curh;

	// constructeur en cascade pour pouvoir initialisé une instance meme à vide
    public DrawingView(Context c) {
        this(c, null);
    }

    public DrawingView(Context c, AttributeSet attrs) {
        this(c, attrs, 0);
    }

    public DrawingView(Context c, AttributeSet attrs, int defStyle) {
        super(c, attrs, defStyle);
        init();
    }

    private void init() {
		// initialisation du chemin qui stocke les points à dessiner
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		// initialisation du pinceau qui défini les propriétés du trait
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
		// couleur du trait
        mPaint.setColor(Color.BLACK);
		// type de trait (STROKE pour ne dessiner que les contours, en opposition à FILL qui remplie la forme, FILL_AND_STROKE pour les deux)
        mPaint.setStyle(Paint.Style.STROKE);
		// forme des contours (ROUND pour arrondie)
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
		// largeur du trait
        mPaint.setStrokeWidth(10);

        mDrawMode = true;
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
    }

	// evenement déclenché lors du redimensionnement de l'activité (meme le démarrage de l'application déclenche l'événement)
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// dimension actuelle gardé pour crée un nouveau Bitmap de meme dimension lors d'une remise à zéro
		curw=w;
		curh=h;

        super.onSizeChanged(w, h, oldw, oldh);
		// si on a pas de Bitmap, on en crée un
        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }
		// affectation du Bitmap au Canva
        mCanvas = new Canvas(mBitmap);
		// définition de la couleur de fond du Canva
        mCanvas.drawColor(Color.TRANSPARENT);
    }

	// evenement déclenché lors de l'affichage de l'activité
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
		// on dessine le Bitmap
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		// puis les points stocké dans mPath
        canvas.drawPath(mPath, mPaint);
    }

	// Lors de l'appuie sur l'écran (on commence à toucher l'écran)
    private void touch_start(float x, float y) {
		// un nouveau chemin est crée pour qu'il ne soit pas relié à celui qui l'avait été auparavent
        mPath.reset();
		// on positione le début du nouveau chemin où on touche l'écran
        mPath.moveTo(x, y);
		// on garde cette position
        mX = x;
        mY = y;
		// et on dessine ce nouveau chemin
        mCanvas.drawPath(mPath, mPaint);
    }

	// Lors d'un mouvement sur l'écran (on à déjà le doigt dessus et on le glisse sur l'écran)
    private void touch_move(float x, float y) {
		// on récupère la différence de position depuis les dernières coordonées gardées
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
		// si on s'est suffisament éloigné (pour ne garder que ce qui est visible et économiser de la mémoire, sinon stackoverflow et crash)
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			// alors on ajoute la nouvelle position au chemin et on la garde
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
		// puis on dessine ce nouveau chemin
        mCanvas.drawPath(mPath, mPaint);
    }

	// Lors de l'arret de l'appuis sur l'écran (on retire le doigt de l'écran)
    private void touch_up() {
		// on termine le chemin vers la dernière position gardé
        mPath.lineTo(mX, mY);
		// on dessine le chemin
        mCanvas.drawPath(mPath, mPaint);
		// et on en crée un nouveau
        mPath.reset();
		// si en a le droit de dessiner
        if (mDrawMode) {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        } else {
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
    }

	// evenement lors d'une action sur l'écran
    @Override public boolean onTouchEvent(MotionEvent event) {
		// position sur l'écran
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
			// on appuie sur l'écran
            case MotionEvent.ACTION_DOWN:
                if (!mDrawMode) {
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                } else {
                    mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
                }
                touch_start(x, y);
                invalidate();
			break;

			// on glisse le doigt sur l'écran
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                if (!mDrawMode) {
                    mPath.lineTo(mX, mY);
                    mPath.reset();
                    mPath.moveTo(x, y);
                }
                mCanvas.drawPath(mPath, mPaint);
                invalidate();
			break;

			// on retire le doigt de l'écran
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
			break;
        }
        return true;
    }

	// fonction de sauvegarde de l'image
    public boolean saveImage(String filePath, String filename, Bitmap.CompressFormat format,
                             int quality) throws Exception {
		// si la qualité de compression est supérieur à 100 alors erreur
        if (quality > 100) {
            Log.d("saveImage", "quality cannot be greater that 100");
            return false;
        }

		// création du fichier temporaire et du fichier de sortie
        File file;
        FileOutputStream out = null;

        try {
			// compression en fonction du format d'image choisie
            switch (format) {
                case PNG:
					// initialisation d'un fichier qui se situera à filePath et à pour nom filename.png
                    file = new File(filePath, filename + ".png");
					// initialisation d'un fichier de sortie qui sera file
                    out = new FileOutputStream(file);
					// compression du Bitmap selon la qualité quality dans le fichier out
                    return mBitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
                case JPEG:
                    file = new File(filePath, filename + ".jpg");
                    out = new FileOutputStream(file);
                    return mBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
				// si le format n'est pas précisé, on compresse en PNG
                default:
                    file = new File(filePath, filename + ".png");
                    out = new FileOutputStream(file);
                    return mBitmap.compress(Bitmap.CompressFormat.PNG, quality, out);
            }
        } catch (Exception e) {
            e.printStackTrace();
			throw e;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

	// Crée un nouveau Canva, lui applique un nouveau Bitmap et force le rafraichissement de l'affichage
	public void reset() {
		mBitmap = Bitmap.createBitmap(curw, curh, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawColor(Color.TRANSPARENT);
		invalidate();
	}

	public Bitmap getBitmap(){
        return mBitmap;
    }

    public void hidemPaint() {
        mPaint.setColor(INVISIBLE);
    }

    public void showmPaint() {
        mPaint.setColor(Color.BLACK);
    }

    public boolean dessinEmpty() {
        Bitmap emptyBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), mBitmap.getConfig());
       return mBitmap.sameAs(emptyBitmap);
    }

}