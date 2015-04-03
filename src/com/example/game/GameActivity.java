package com.example.game;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends Activity {
	private Bitmap mBitmap;
	private boolean isGaming=false;
	private AbsoluteLayout puzzle = null;
	private int screenHeight;
	private int screenWidth;
	private int row;
	private int col;
	 
	private ArrayList<Piece> allPieces = new ArrayList<Piece>();
	private ArrayList<Piece> movePieces = new ArrayList<Piece>();
	
	private int INACCURACY = 6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		puzzle = (AbsoluteLayout) inflater.inflate(R.layout.puzzle, null);
		setContentView(puzzle);
		initControls();
	}
	
	@SuppressWarnings("deprecation")
	private void initControls(){
		Bundle extras=this.getIntent().getExtras();
		int level=extras.getInt("level");
		int pictureIndex=extras.getInt("pictureIndex");
    	System.out.println("picture"+pictureIndex); 
    	
    	if(0==pictureIndex){
    		 mBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img0);
    	 }else if(1==pictureIndex){
    		 mBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img1);
    	 }else if(2==pictureIndex){
    		 mBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img2);
    	 }else if(3==pictureIndex){
    		 mBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img3);
    	 }else if(4==pictureIndex){
    		 mBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img4);
    	 }else if(5==pictureIndex){
    		 mBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img5);
    	 }else if(6==pictureIndex){
    		 mBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img6);
    	 }else if(7==pictureIndex){
    		 mBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img7);
    	 }else if(8==pictureIndex){
    		 mBitmap=BitmapFactory.decodeResource(getResources(),R.drawable.img8);
    	 }

    	 if(level==0){
     		 row = col = 3;
    	 }else if(level==1){
    		 row = 3;
    		 col = 4;
    	 }else if(level==2){
    		 row = 4;
    		 col = 3;
    	 }else if(level==3){
    		 row = 4;
    		 col = 4;
    	 }
    	 
		DisplayMetrics dm = new DisplayMetrics();
        dm = this.getApplicationContext().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
		int height = mBitmap.getHeight()/row;
		int width = mBitmap.getWidth()/col;
		int startx = (screenWidth-(width+10)*col-10)/2;
		int starty = screenHeight-(height+10)*row-80;
		
		int x,y,autoX,autoY;
		Bitmap bitmap;
		int ax, ay;
		ArrayList<Integer> rarray = new ArrayList<Integer>();
		ArrayList<Integer> carray = new ArrayList<Integer>();
		
    	for (int i = 0; i< row; i++) {
    		for (int j = 0; j < col; j++) {
    			x = j*width;
    			y = i*height;
    			bitmap = Bitmap.createBitmap(mBitmap,x,y,width,height);
    		    
    			Piece newpiece = new Piece(this);
    			newpiece.setId(i*col+j);
    			newpiece.setBackgroundDrawable(new BitmapDrawable(bitmap));
    			newpiece.setOnTouchListener(new OnTouchListener(){
    		    	int lastX;
    		    	int lastY;
    		    	
    				public boolean onTouch(View v, MotionEvent event) {
    					// TODO Auto-generated method stub
    					switch(event.getAction()){
    					case MotionEvent.ACTION_DOWN:
    						lastX = (int) event.getRawX(); 
    						lastY = (int) event.getRawY();
    						
    						puzzle.bringChildToFront(v);  //把该视图置于其他所有子视图之上
    						displayFront((Piece)v);
    						break;
    						
    					case MotionEvent.ACTION_MOVE:
    						int dx =(int)event.getRawX() - lastX;
    						int dy =(int)event.getRawY() - lastY;
    						
    						//存在延迟
    						checkMove((Piece)v, dx, dy, movePieces);
    						moveSomePieces(movePieces);
    						movePieces.clear(); 
    						cleanPath();
    						    						
    						lastX = (int) event.getRawX();
    						lastY = (int) event.getRawY();
    						break;
    						
    					case MotionEvent.ACTION_UP:    						
    						cleanPath();
    						Piece firstPiece = checkAbsorb((Piece)v);
    						
    						cleanPath();
    						absorb(firstPiece);	
    						
    						displayFront(firstPiece);
    						
    						hasComplete();
    						break;        		
    					}
    					return false;
    				}});
    			
    			ax = (int) (Math.random()*row);
    			ay = (int) (Math.random()*col);
    			while (containSameCoordiante(rarray,carray,ax,ay)) {
        			ax = (int) (Math.random()*row);
        			ay = (int) (Math.random()*col);
    			}
    			rarray.add(ax);
    			carray.add(ay);
    			
    			autoX = startx+(width+10)*ay;
    			autoY = starty+(height+10)*ax;
    			//System.out.println("autoX"+ax);
    			//System.out.println("autoY"+ay);

    			Point loc = new Point(autoX, autoY);
    			Point initial_loc = new Point(width*j, height*i);
    			
    			newpiece.setLocation(loc);
    			newpiece.setInitialLocation(initial_loc); //初始的相对坐标，后面判断是否可以吸引
    			
    			allPieces.add(newpiece);
    			
    			AbsoluteLayout.LayoutParams autoParams = new AbsoluteLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, autoX, autoY);
    			newpiece.setLayoutParams(autoParams);
    			puzzle.addView(newpiece);
    		}
    	}
    }
	
	private boolean containSameCoordiante(ArrayList<Integer> ra, ArrayList<Integer> ca, int x, int y) {
		if (ra.size() == 0) return false;
		for (int i = 0; i < ra.size(); i++) {
			if (ra.get(i)==x && ca.get(i)==y) 
				return true;
		}
		return false;
	}
	
    private void checkMove(Piece p, int dx, int dy, ArrayList<Piece> movePieces){
    	int l = p.getLeft() + dx;
    	int t = p.getTop() + dy;
    	int r = p.getRight() + dx;
    	int b = p.getBottom() + dy;
    	p.setLocation(new Point(l, t));

		if(l < 0){
			l = 0;
			r = l + p.getWidth();
		}
		if(r > screenWidth){
			r = screenWidth;
			l = r - p.getWidth();
		}
		if(t < 0){
			t = 0;
			b = t + p.getHeight();
		}
		if(b > screenHeight){
			b = screenHeight;
			t = b - p.getHeight();
		}
    	
		//curPIB.layout(l, t, r, b);
    	movePieces.add(p);
    	
		int id = p.getId();
    	int curRow = id / col;
    	int curCol = id % col;
    	
    	if(p.isHasTop()){
    		Piece topPIB = (Piece) allPieces.get((curRow - 1) * col + curCol);
    		if(!topPIB.isTraverse()){
    			topPIB.setTraverse(true);
    			checkMove(topPIB, dx, dy, movePieces);
    		}
    		
    	}
    	
    	if(p.isHasRight()){
    		Piece rightPIB = (Piece) allPieces.get(id + 1);
        	if(!rightPIB.isTraverse()){
        		rightPIB.setTraverse(true);
        		checkMove(rightPIB, dx, dy, movePieces);
        	}
    		
    	}
    	
    	if(p.isHasFeet()){
    		Piece feetPIB = (Piece) allPieces.get((curRow + 1) * col + curCol);
        	if(!feetPIB.isTraverse()){
        		feetPIB.setTraverse(true);
        		checkMove(feetPIB, dx, dy, movePieces);
        	}
    		
    	}
    	
    	if(p.isHasLeft()){
    		Piece leftPIB = (Piece) allPieces.get(id - 1);
        	if(!leftPIB.isTraverse()){
        		leftPIB.setTraverse(true);
        		checkMove(leftPIB, dx, dy, movePieces);
        	}
    		
    	}
    	
    }
    
    private void moveSomePieces(ArrayList<Piece> pieces){
    	for(int i = 0; i < pieces.size(); i++){
    		Piece piece = (Piece) pieces.get(i);
    		Point loc = piece.getLocation();
    		piece.layout(loc.x, loc.y, loc.x + piece.getWidth(), loc.y + piece.getHeight());
    	}
    }
    
    private void cleanPath(){
    	for(int i = 0; i < allPieces.size(); i++){
    		Piece piece = (Piece) allPieces.get(i);
    		piece.setTraverse(false);
    	}
    }
    
    private void hasComplete(){
    	int finish = 0;
    	for(int i = 0; i < allPieces.size(); i++){
    		Piece piece = (Piece) allPieces.get(i);
    		if(piece.isTraverse()){
    			finish ++;
    		}
        	
    	}
    	if(finish == row * col){
    		openFinishDialog();
    	}
    }
    
    private void openFinishDialog(){
    	Builder builder = new AlertDialog.Builder(this);
    	builder.setIcon(R.drawable.success);
		builder.setTitle("Congratulations！");
		builder.setMessage("Return to game");
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						GameActivity.this.finish();
					}
				});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
    }

	private void displayFront(Piece curPiece){
    	puzzle.bringChildToFront(curPiece);   //把该视图置于其他所有子视图之上
    	curPiece.postInvalidate();
    	
    	int id = curPiece.getId();
    	int curRow = id / col;
    	int curCol = id % col;
    	
    	//top
    	if(curPiece.isHasTop()){
    		Piece topPiece = (Piece) allPieces.get((curRow - 1) * col + curCol);
    		if(!topPiece.isTraverse()){
    			topPiece.setTraverse(true);
    			displayFront(topPiece);
    		}
    		
    	}
    	
    	//right
    	if(curPiece.isHasRight()){
    		Piece rightPiece = (Piece) allPieces.get(id + 1);
    		if(!rightPiece.isTraverse()){
    			rightPiece.setTraverse(true);
        		displayFront(rightPiece);
    		}
    		
    	}
    	
    	//feet
    	if(curPiece.isHasFeet()){
    		Piece feetPiece = (Piece) allPieces.get((curRow + 1) * col + curCol);
    		if(!feetPiece.isTraverse()){
    			feetPiece.setTraverse(true);
        		displayFront(feetPiece);
    		}
    		
    	}
    	
    	//left
    	if(curPiece.isHasLeft()){
    		Piece leftPiece = (Piece) allPieces.get(id - 1);
    		if(!leftPiece.isTraverse()){
    			leftPiece.setTraverse(true);
        		displayFront(leftPiece);
    		}
    		
    	}
    	
    }
    
    private Piece checkAbsorb(Piece v){
    	Piece firstPiece = null;
    	
    	Piece curPiece = v;
    	curPiece.setTraverse(true);
    	
    	int curId = curPiece.getId();
    	int curRow = curId / col;
    	int curCol = curId % col;
       	Point curInitialP = curPiece.getInitialLocation();
    	Point curLoc = curPiece.getLocation();
 
    	//top
    	if(curRow > 0){   //当前碎片在原图片里存在上面的碎片
    		int topPieceId = (curRow - 1) * col + curCol;
    		if(!curPiece.isHasTop()){  //如果上面的碎片还未吸附
    			//如果存在上面的碎片，还没有碰撞，则得到上面碎片的位置判断是否吸附
    			Piece topPiece = allPieces.get(topPieceId);
	    		Point topLoc = topPiece.getLocation();
	    		Point topInitialP = topPiece.getInitialLocation();
	    		
	    		//如果吸附条件成立，则吸附
	    		if(distance(curInitialP, topInitialP, curLoc, topLoc, INACCURACY)){
	    			curPiece.setHasTop(true);
	    			topPiece.setHasFeet(true);
	    			if(firstPiece == null){
	    				firstPiece = topPiece;
	    			}

	    		}
    		}else{  //如果上面的碎片已经吸附,且不是搜索的来源（避免死循环）,则继续上面的碎片查找
    			Piece topPiece = allPieces.get(topPieceId);
    			if(!topPiece.isTraverse()){
    				checkAbsorb(topPiece);
    			}
    		}
    	}
    	
    	//right
    	if(curCol < (col -1)){  //当前碎片存在右面的碎片
    		int rightPieceId = curId + 1;
    		if(!curPiece.isHasRight()){  //如果右面的碎片还为吸附
    			//如果存在右面的碎片，还没有碰撞，则得到右面碎片的位置判断是否吸附
    			Piece rightPiece = allPieces.get(rightPieceId);
	    		Point rightLoc = rightPiece.getLocation();
	    		Point rightInitialP = rightPiece.getInitialLocation();

	    		//如果吸附条件成立，则吸附
	    		if(distance(curInitialP, rightInitialP, curLoc, rightLoc, INACCURACY)){
	    			curPiece.setHasRight(true);
	    			rightPiece.setHasLeft(true);
	    			if(firstPiece == null){
	    				firstPiece = rightPiece;
	    			}
	    			
	    		}
    		}else{
    			Piece rightPiece = allPieces.get(rightPieceId);
    			if(!rightPiece.isTraverse()){
    				checkAbsorb(rightPiece);
    			}

    		}
    	}
    	
    	//feet
    	if(curRow < (row - 1)){
    		int feetPieceId = (curRow + 1) * col + curCol;
    		if(!curPiece.isHasFeet()){
    			//如果存在右面的碎片，还没有碰撞，则得到右面碎片的位置判断是否吸附
    			Piece feetPiece = (Piece) allPieces.get(feetPieceId);
	    		Point feetLoc = feetPiece.getLocation();
	    		Point feetInitialP = feetPiece.getInitialLocation();
	    		
	    		//如果吸附条件成立，则吸附
	    		if(distance(curInitialP, feetInitialP, curLoc, feetLoc, INACCURACY)){
	    			curPiece.setHasFeet(true);
	    			feetPiece.setHasTop(true);
	    			if(firstPiece == null){
	    				firstPiece = feetPiece;
	    			}
	    			
	    		}
    		}else{
    			Piece feetPiece = (Piece) allPieces.get(feetPieceId);
    			if(!feetPiece.isTraverse()){
    				checkAbsorb(feetPiece);
    			}
    		}

    	}

    	//left
    	if(curCol > 0){
    		int leftPieceId = curId - 1;
    		if(!curPiece.isHasLeft()){
    			//如果存在右面的碎片，还没有碰撞，则得到右面碎片的位置判断是否吸附
    			Piece leftPiece = (Piece) allPieces.get(leftPieceId);
	    		Point leftLoc = leftPiece.getLocation();
	    		Point leftInitialP = leftPiece.getInitialLocation();
	    		
	    		//如果吸附条件成立，则吸附
	    		if(distance(curInitialP, leftInitialP, curLoc, leftLoc, INACCURACY)){
	    			curPiece.setHasLeft(true);
	    			leftPiece.setHasRight(true);
	    			if(firstPiece == null){
	    				firstPiece = leftPiece;
	    			}
	    			
	    		}
    		}else{
    			Piece leftPiece = allPieces.get(leftPieceId);
    			if(!leftPiece.isTraverse()){
    				checkAbsorb(leftPiece);
    			}
    		}

    	}
    	if(firstPiece == null){
    		firstPiece = v;
    	}
    	return firstPiece;
    }
    
    private boolean distance(Point srckey, Point destkey, Point srcloc, Point destloc, int inaccuracy){
		if(Math.abs((srckey.x - destkey.x) - (srcloc.x - destloc.x)) <= inaccuracy){
			if(Math.abs((srckey.y - destkey.y) - (srcloc.y - destloc.y)) <= inaccuracy){
				return true;
			}
		}
    	return false;
    }
    
    private void absorb(Piece curPiece){
    	Point curInitialP = curPiece.getInitialLocation();
    	Point curLoc = curPiece.getLocation();
    	curPiece.layout(curLoc.x, curLoc.y, curLoc.x + curPiece.getWidth(), curLoc.y + curPiece.getHeight());
    	
    	int id = curPiece.getId();
    	int curRow = id / col;
    	int curCol = id % col;
    	
    	//top
    	if(curPiece.isHasTop()){
    		Piece topPiece = (Piece) allPieces.get((curRow - 1) * col + curCol);
    		if(!topPiece.isTraverse()){
    			Point topInitialP = topPiece.getInitialLocation();
        		topPiece.setLocation(new Point(curLoc.x + (topInitialP.x - curInitialP.x), curLoc.y + (topInitialP.y - curInitialP.y)));
        		topPiece.setTraverse(true);
        		absorb(topPiece);
    		}
    		
    	}
    	
    	//right
    	if(curPiece.isHasRight()){
    		Piece rightPiece = (Piece) allPieces.get(id + 1);
    		if(!rightPiece.isTraverse()){
    			Point rightInitialP = rightPiece.getInitialLocation();
        		rightPiece.setLocation(new Point(curLoc.x + (rightInitialP.x - curInitialP.x), curLoc.y + (rightInitialP.y - curInitialP.y)));
        		rightPiece.setTraverse(true);
        		absorb(rightPiece);
    		}
    		
    	}
    	
    	//feet
    	if(curPiece.isHasFeet()){
    		Piece feetPiece = (Piece) allPieces.get((curRow + 1) * col + curCol);
    		if(!feetPiece.isTraverse()){
    			Point feetInitialP = feetPiece.getInitialLocation();
        		feetPiece.setLocation(new Point(curLoc.x + (feetInitialP.x - curInitialP.x), curLoc.y + (feetInitialP.y - curInitialP.y)));
        		feetPiece.setTraverse(true);
        		absorb(feetPiece);
    		}
    		
    	}
    	
    	//left
    	if(curPiece.isHasLeft()){
    		Piece leftPiece = (Piece) allPieces.get(id - 1);
    		if(!leftPiece.isTraverse()){
    			Point leftInitialP = leftPiece.getInitialLocation();
        		leftPiece.setLocation(new Point(curLoc.x + (leftInitialP.x - curInitialP.x), curLoc.y + (leftInitialP.y - curInitialP.y)));
        		leftPiece.setTraverse(true);
        		absorb(leftPiece);
    		}
    		
    	}
    	
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK){
			 exitThisActivity();
			 return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void exitThisActivity(){	
		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setIcon(R.drawable.question_dialog_icon);
		builder.setTitle("Exit");
		builder.setMessage("Are you sure to back to reselect level and picture？");
		builder.setPositiveButton(R.string.confirm,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						GameActivity.this.finish();
					}
				});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}
}
