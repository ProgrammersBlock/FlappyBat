package com.llcdevelopers.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.scenes.scene2d.ui.Button;
//import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
//import com.badlogic.gdx.utils.Scaling;
import java.util.Random;
import com.badlogic.gdx.graphics.g2d.Animation;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	ShapeRenderer shapeRenderer;

	Texture topTube;
	Texture bottomTube;
	float gap = 500;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 3;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	Texture gameOver;

	Texture birds;
	//int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	Circle birdCircle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;

	int gameState = 0;
	float gravity = 2;

	int score = 0;
	int scoringTube = 0;
	BitmapFont font;

	float gameOverY = 0;

	// Constant rows and columns of the sprite sheet
	private static final int FRAME_COLS = 2, FRAME_ROWS = 1;

	// Objects used
	Animation<TextureRegion> batAnimation; // Must declare frame type (TextureRegion)
	Texture batSheet;
	SpriteBatch spriteBatch;

	// A variable for tracking elapsed time for the animation
	float stateTime;

	// Basically creates the variables we use/change later on.

	@Override
	public void create () {
		batch = new SpriteBatch();

		background = new Texture("moonbg.png");
		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[numberOfTubes];
		bottomTubeRectangles = new Rectangle[numberOfTubes];
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(15);

		gameOver = new Texture("gameovernew.png");

		topTube = new Texture("topcastlenew.png");
		bottomTube = new Texture("bottomcastlenew.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = 700;

		birds = new Texture("bat.png");
		birdY = Gdx.graphics.getHeight() / 2 - birds.getHeight() / 2;
		gameOverY = Gdx.graphics.getHeight();

		// Load the sprite sheet as a Texture
		batSheet = new Texture(Gdx.files.internal("spritesheet.png"));

		// Use the split utility method to create a 2D array of TextureRegions. This is
		// possible because this sprite sheet contains frames of equal size and they are
		// all aligned.
		TextureRegion[][] tmp = TextureRegion.split(batSheet,
				batSheet.getWidth() / FRAME_COLS,
				batSheet.getHeight() / FRAME_ROWS);

		// Place the regions into a 1D array in the correct order, starting from the top
		// left, going across first. The Animation constructor requires a 1D array.
		TextureRegion[] walkFrames = new TextureRegion[FRAME_COLS * FRAME_ROWS];
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				walkFrames[index++] = tmp[i][j];
			}
		}

		// Initialize the Animation with the frame interval and array of frames
		batAnimation = new Animation<TextureRegion>(0.17f, walkFrames);

		// Instantiate a SpriteBatch for drawing and reset the elapsed animation
		// time to 0
		spriteBatch = new SpriteBatch();
		stateTime = 0f;

		// Basically creates objects based on the variables we created above.

		startGame();

	}

	public void startGame() {
		birdY = Gdx.graphics.getHeight() / 2 - birds.getHeight() / 2;

		for (int i = 0; i < numberOfTubes; i++) {
			tubeX[i] = Gdx.graphics.getWidth() - topTube.getWidth() / 2 + i * distanceBetweenTubes + 200;
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}

		// This is the class we reference back to when the game is reloaded after gameOver.
		// It resets the locations, values, etc that need to be reset. No point in reloading everything else.

	}


	@Override
	public void render () {

		// This is the class that constantly renders as the program is running.
		// You draw the Sprites, update values, etc within the render class.

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Draws the background image.

		//batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);

		// Draws the initial bat image.

		if (gameState == 1) {

			// When gameState = 1, the user is in the middle of playing the game.

			//Sound pointSound = Gdx.audio.newSound(Gdx.files.internal("data/pointsound.wav"));

			if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 3) {

				// When every 4th tube (or however many tubes there are later on) passes this X position,
				// one point is added to the current score.

				score++;

				if (scoringTube < numberOfTubes -1) {

					scoringTube++;

					// Because there are multiple tubes on the screen at once, one tube has to add a point at a time.
					// One is added to the value of scoringTube until it reaches what we set as the numberOfTubes on-screen.

				} else {

					scoringTube = 0;

					// After a point is added to the score, scoringTube is returned to 0 and the process repeats.

				}

			}

			if (Gdx.input.justTouched()) {

				velocity = -30;
				//birds = new Texture("bat2.png");
				//birds = new Texture("bat2.png");


			//} else if (velocity < 0) {

				//birds = new Texture("bat2.png");
				//birds = new Texture("bat2.png");

			//} else {

				//birds = new Texture("bat.png");
				//birds = new Texture("bat.png");
			}

			// This is what animates our bat each touch, but it doesn't delete previously used textures, causing a memory leak.

			for (int i = 0; i < numberOfTubes; i++) {

				if (tubeX[i] < - topTube.getWidth()) {

					tubeX[i] += numberOfTubes * distanceBetweenTubes;
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

				} else {

					tubeX[i] = tubeX[i] - tubeVelocity;
				}

				tubeX[i] = tubeX[i] - tubeVelocity;

				batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

				// All of this creates all the tubes on the screen, randomizes their Y-values, and moves them to the left.

				topTubeRectangles[i] = new Rectangle(tubeX[i] + 100, Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth() - 150, topTube.getHeight());
				bottomTubeRectangles[i] = new Rectangle(tubeX[i] + 100, Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth() - 150, bottomTube.getHeight());

				// Creates rectangles (shapes) around the tubes used for collisions.

			}

			if (birdY > 0) {

				velocity = velocity + gravity;
				birdY -= velocity;
			} else {

				gameState = 2;

			}

			// Keeps the bat from falling past the bottom of the screen and ends the game upon collision with the bottom.

		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}

			// gameState = 0 is the very beginning of the game, and touching changes it to gameState = 1.

		} else if (gameState == 2){

			batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, gameOverY);

			if (gameOverY > Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2) {

				velocity = velocity + gravity;
				gameOverY -= velocity;

				// gameState = 2 is the gameOver screen. gameOver is animated.

			} else {

				if (Gdx.input.justTouched()) {

					gameState = 0;
					startGame();
					score = 0;
					scoringTube = 0;
					velocity = 0;
					gameOverY = Gdx.graphics.getHeight();

					// Once the gameOver screen is touched, all the values are reset, effectively restarting the game.

				}

			}

		}

		//if (flapState == 0) {
			//flapState = 1;
		//} else {
			//flapState = 0;
		//}

		// Not necessary at the moment. Used to animate bat without user input based on render speed.

		font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth() / 2 - 60, Gdx.graphics.getHeight() - 150);

		// Draws the current score at the top of the screen.

		batch.end();

		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds.getHeight() / 2, birds.getHeight() / 4);

		// Creates a circle (shape) around the bat used for collisions.

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		// Used to make the shapes around the sprites visible for testing. (All shapeRenderer is used for this.)

		for (int i = 0; i < numberOfTubes; i++) {
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
			//shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

			if (Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
				Gdx.app.log("Collision", "Yes!");
				gameState = 2;
			}

			// Checks to see if any shapes overlap, or collide, with one another.
			// If so, gameState is set back to 2. gameOver.

		}

		//shapeRenderer.end();

		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen but we don't need
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

		// Get current frame of animation for the current stateTime
		TextureRegion currentFrame = batAnimation.getKeyFrame(stateTime, true);
		spriteBatch.begin();
		spriteBatch.draw(currentFrame, Gdx.graphics.getWidth() / 2 - birds.getWidth() / 2, birdY); // Draw current frame at (50, 50)
		spriteBatch.end();

		// Draws the animated bat.

	}

	@Override
	public void dispose() { // SpriteBatches and Textures must always be disposed
		spriteBatch.dispose();
		batSheet.dispose();
	}

}
