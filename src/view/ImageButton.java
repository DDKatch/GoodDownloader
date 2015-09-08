package view;

import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Lighting;
import javafx.scene.image.ImageView;

public interface ImageButton {
	default public void makeImageSimilarButton(ImageView imageView){
		Lighting oldEffect =  (Lighting) imageView.getEffect();
		imageView.setFocusTraversable(true);
				
		
		
		imageView.setOnMousePressed((event) -> {
			imageView.setBlendMode(BlendMode.MULTIPLY);
		});
		
		imageView.setOnMouseReleased((event) -> {
			imageView.setEffect(oldEffect);
		});
		
		imageView.setOnMouseEntered((event) -> {
			imageView.setBlendMode(BlendMode.HARD_LIGHT);
		});
		
		imageView.setOnMouseExited((event) -> {
			imageView.setBlendMode(BlendMode.SRC_OVER);
		});
	}
}
