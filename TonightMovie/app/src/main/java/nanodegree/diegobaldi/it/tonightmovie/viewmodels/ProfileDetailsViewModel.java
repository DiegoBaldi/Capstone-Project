package nanodegree.diegobaldi.it.tonightmovie.viewmodels;

import android.databinding.BaseObservable;
import android.net.Uri;

import nanodegree.diegobaldi.it.tonightmovie.TonightMovieApp;
import nanodegree.diegobaldi.it.tonightmovie.models.User;

/**
 * Created by diego on 05/03/2017.
 */

public class ProfileDetailsViewModel extends BaseObservable {

    private User profile;

    public ProfileDetailsViewModel(User profile) {
        this.profile = profile;
    }

    public Uri getImage() {
        if(profile.getPhotoURL()!=null)
            return Uri.parse(profile.getPhotoURL());
        else
            return Uri.parse("");
    }

    public String getName() {
        return profile.getDisplayName();
    }

    public String getBio() {
        if (profile.getBio() != null && !profile.getBio().equalsIgnoreCase(""))
            return profile.getBio();
        else {
            return "n/a";
        }
    }

    public int getMovieMasterLvl() {
        return findDistanceFromLevel(profile.getMovieMaster());
    }

    public int getRecklessLvl() {
        return findDistanceFromLevel(profile.getReckless());
    }

    public int getEmpathicLvl() {
        return findDistanceFromLevel(profile.getEmpathic());
    }

    public int getNerdLvl() {
        return findDistanceFromLevel(profile.getNerd());
    }

    public int getHilariousLvl() {
        return findDistanceFromLevel(profile.getHilarious());
    }

    public int getFearlessLvl() {
        return findDistanceFromLevel(profile.getFearless());
    }

    public int getOverprotectiveLvl() {
        return findDistanceFromLevel(profile.getOverprotective());
    }

    public String getNextMovieMasterLvl() {
        return getNextLevel(profile.getMovieMaster());
    }

    public String getPreviousMovieMasterLvl() {
        return getPreviousLevel(profile.getMovieMaster());
    }

    public String getNextHilariousLvl() {
        return getNextLevel(profile.getHilarious());
    }

    public String getPreviousHilariousLvl() {
        return getPreviousLevel(profile.getHilarious());
    }

    public String getNextRecklessLvl() {
        return getNextLevel(profile.getReckless());
    }

    public String getPreviousRecklessLvl() {
        return getPreviousLevel(profile.getReckless());
    }

    public String getNextNerdLvl() {
        return getNextLevel(profile.getNerd());
    }

    public String getPreviousNerdLvl() {
        return getPreviousLevel(profile.getNerd());
    }

    public String getNextEmpathicLvl() {
        return getNextLevel(profile.getEmpathic());
    }

    public String getPreviousEmpathicLvl() {
        return getPreviousLevel(profile.getEmpathic());
    }

    public String getNextFearlessLvl() {
        return getNextLevel(profile.getFearless());
    }

    public String getPreviousFearlessLvl() {
        return getPreviousLevel(profile.getFearless());
    }

    public String getNextOverprotectiveLvl() {
        return getNextLevel(profile.getOverprotective());
    }

    public String getPreviousOverprotectiveLvl() {
        return getPreviousLevel(profile.getOverprotective());
    }

    public String getPreviousLevel(int value) {
        return "" + (int) getLevel(value);
    }

    public String getNextLevel(int value) {
        return "" + (int) (getLevel(value) + 1);
    }

    public double getLevel(int value) {
        return TonightMovieApp.KONSTANT_LEVEL * Math.sqrt(value);
    }

    public int findDistanceFromLevel(int value) {
        double difference = getLevel(value) - (int) getLevel(value);
        return (int) (difference * 100);
    }

}