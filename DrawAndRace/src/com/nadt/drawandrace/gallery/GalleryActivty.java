package com.nadt.drawandrace.gallery;

import com.nadt.drawandrace.utils.Tools;
import com.nadt.drawnandrace.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class GalleryActivty extends Activity {


	private static int RESULT_LOAD_IMAGE = 1;
	private int screenHeight;
	private int screenWidth;
	private int angle;
	private float touchingX;
	private boolean alreadyChange;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		angle = 0;
		setContentView(R.layout.gallery_activity);
		View galleryView = findViewById(R.id.imgView);
		// Récuperation de la taille du device
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();

		Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
		buttonLoadImage.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});
		
		// On envoie la position touché par l'utilisateur
		galleryView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				final int action = event.getAction();
				switch(action) {
				case MotionEvent.ACTION_DOWN:
					touchingX = event.getX();
					alreadyChange = false;
					Tools.log(this, "Touch position : " + touchingX);
					return true;
				case MotionEvent.ACTION_MOVE:
					if(alreadyChange) {
						return true;
					}
					if(touchingX < event.getX() - 30) {
						angle -= 90;
						alreadyChange = true;
					}
					else if(touchingX > event.getX() + 30){
						angle += 90;
						alreadyChange = true;
					}
					Tools.log(this, "Rotate : " + angle);
					ImageView imageView = (ImageView) findViewById(R.id.imgView);
					Matrix matrix=new Matrix();
					imageView.setScaleType(ScaleType.MATRIX);   //required
					matrix.postRotate(angle, screenWidth / 2, screenHeight / 2);
					imageView.setImageMatrix(matrix);
				}
				return true;
			}
		});
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			ImageView imageView = (ImageView) findViewById(R.id.imgView);
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
		}
	}
}