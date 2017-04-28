package nanodegree.diegobaldi.it.tonightmovie.bindingadapters;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.databinding.BindingAdapter;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.drawee.view.SimpleDraweeView;

import nanodegree.diegobaldi.it.tonightmovie.R;

/**
 * Created by diego on 21/04/2017.
 */

public class BindingAdapters {
    @BindingAdapter("app:animatedInterest")
    public static void changeStyle(final Button button, final boolean interested) {
        if (interested) {
            button.setText(button.getContext().getString(R.string.following));
            button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.colorAccent));
        } else {
            button.setText(button.getContext().getString(R.string.follow));
            button.setTextColor(ContextCompat.getColor(button.getContext(), R.color.colorPrimaryLight));
        }
    }

    @BindingAdapter("app:actualImageUrl")
    public static void loadImage(SimpleDraweeView imageView, Uri url) {
        imageView.setImageURI(url);
    }

    @BindingAdapter({"app:animatedCompletion", "app:message"})
    public static void flip(final View view, final int completed, String message) {
        // Now create an animator
        if (completed > 0) {
            final LottieAnimationView lottie = (LottieAnimationView) view.findViewById(R.id.animation_view);
            TextView feedbackTV = (TextView) view.findViewById(R.id.feedback);
            feedbackTV.setText(message);
            AnimatorSet setOut = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(),
                    R.animator.card_flip_left_out);
            setOut.setTarget(view.findViewById(R.id.form_card));
            AnimatorSet setIn = (AnimatorSet) AnimatorInflater.loadAnimator(view.getContext(),
                    R.animator.card_flip_left_in);
            setIn.setTarget(view.findViewById(R.id.success_card));
            setIn.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    startLottieAnimation(lottie);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            if (completed == 1)
                lottie.setAnimation("check_pop.json");
            else if (completed == 2)
                lottie.setAnimation("x_pop.json");
            setOut.start();
            setIn.start();
        }
    }

    private static void startLottieAnimation(LottieAnimationView lottie) {
        lottie.setSpeed(0.5f);
        lottie.playAnimation();
    }

    @BindingAdapter("app:animatedLikeActions")
    public static void changeLikeIcons(final View view, final int likeStatus) {
        // Now create an animator
        switch (likeStatus) {
            case 0:
                ((ImageView) view.findViewById(R.id.upvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_up_neutral));
                ((ImageView) view.findViewById(R.id.downvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_down_neutral));
                break;
            case 1:
                ((ImageView) view.findViewById(R.id.upvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_up_pressed));
                ((ImageView) view.findViewById(R.id.downvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_down_neutral));
                break;
            case 2:
                ((ImageView) view.findViewById(R.id.upvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_up_neutral));
                ((ImageView) view.findViewById(R.id.downvote)).setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_thumb_down_pressed));
                break;
            default:
        }
    }

    @BindingAdapter("app:acceptedAdviceIcon")
    public static void changeAcceptIcon(final ImageView view, final boolean acceptedStatus) {
        // Now create an animator
        if (acceptedStatus) {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_check_circle_accent));
        } else {
            view.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_check_circle_neutral));
        }
    }

    @BindingAdapter("app:actualDrawable")
    public static void loadImage(ImageView imageButton, int id) {
        imageButton.setImageDrawable(ContextCompat.getDrawable(imageButton.getContext(), id));
    }

    @BindingAdapter("app:visibilityDependsOn")
    public static void loadImage(ImageView imageButton, boolean isRequester) {
        if (isRequester)
            imageButton.setVisibility(View.VISIBLE);
        else
            imageButton.setVisibility(View.GONE);
    }

}
