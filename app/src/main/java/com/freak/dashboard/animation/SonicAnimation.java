package com.freak.dashboard.animation;

import android.widget.ImageView;

import com.freak.dashboard.R;

public class SonicAnimation extends SpriteAnimation {

	private static final int[] sonic_wait = {
		/*R.drawable.sonic_wait_1, 
		R.drawable.sonic_wait_2, 
		R.drawable.sonic_wait_1, 
		R.drawable.sonic_wait_2,
		R.drawable.sonic_wait_1, 
		R.drawable.sonic_wait_2, 
		R.drawable.sonic_wait_1, 
		R.drawable.sonic_wait_2,
		R.drawable.sonic_wait_1, 
		R.drawable.sonic_wait_2, 
		R.drawable.sonic_wait_1, 
		R.drawable.sonic_wait_2,
		R.drawable.sonic_wait_1, 
		R.drawable.sonic_wait_2, 
		R.drawable.sonic_wait_1, 
		R.drawable.sonic_wait_2,
		R.drawable.sonic_wait_1, 
		R.drawable.sonic_wait_3, 
		R.drawable.sonic_wait_4, 
		R.drawable.sonic_wait_5, 
		R.drawable.sonic_wait_6, 
		R.drawable.sonic_wait_6, 
		R.drawable.sonic_wait_6, 
		R.drawable.sonic_wait_5 */
		R.drawable.sonic_stand
	};
    private static final int[] sonic_walk = {
		R.drawable.sonic_walk_1, 
		R.drawable.sonic_walk_2, 
		R.drawable.sonic_walk_3, 
		R.drawable.sonic_walk_4, 
		R.drawable.sonic_walk_5, 
		R.drawable.sonic_walk_6, 
		R.drawable.sonic_walk_7, 
		R.drawable.sonic_walk_8
	};
    private static final int[] sonic_run = {
		R.drawable.sonic_run_1, 
		R.drawable.sonic_run_2, 
		R.drawable.sonic_run_3, 
		R.drawable.sonic_run_4
	};
    private static final int[] sonic_gold = {
		R.drawable.sonic_gold_1, 
		R.drawable.sonic_gold_2
	};

    public SonicAnimation(ImageView animation, int low, int medium, int high) {
        super(animation, low, medium, high);
    }

    @Override
    protected int[] getAnimationStop() {
        return sonic_wait;
    }

    @Override
    protected int[] getAnimationLow() {
        return sonic_walk;
    }

    @Override
    protected int[] getAnimationMedium() {
        return sonic_run;
    }

    @Override
    protected int[] getAnimationHigh() {
        return sonic_gold;
    }
}
